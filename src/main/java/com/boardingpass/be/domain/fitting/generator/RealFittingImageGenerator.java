package com.boardingpass.be.domain.fitting.generator;

import com.boardingpass.be.domain.product.ProductColor;
import com.boardingpass.be.domain.product.ProductImage;
import com.boardingpass.be.domain.storage.AzureBlobStorageService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.time.Duration;
import java.util.Base64;
import java.util.Comparator;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Primary
@Service
@RequiredArgsConstructor
public class RealFittingImageGenerator implements FittingImageGenerator {

  private static final String EDIT_URI = "https://api.openai.com/v1/images/edits";
  private static final String PROMPT_TEMPLATE_WITH_REFERENCE = """
      Photorealistic edit of the first attached photo (the person). The second attached \
      photo shows the actual MCM "%s" in %s color. Copy that reference photo's item \
      exactly — same silhouette, proportions, structure, and overall shape; same color \
      and any gradient or color transition across the item exactly as shown; same \
      texture, pattern, logo placement, and hardware. Do not invent, simplify, flatten a \
      gradient into a solid color, or otherwise alter the product's appearance in any way.

      First, look at the second (reference) photo itself to determine what kind of \
      fashion item this actually is and its actual shape and type — do not guess this \
      from the product name alone. Then edit the first photo so the person is naturally \
      wearing or carrying that exact item, following these placement rules:

      - Bag: judge its type and apparent size from the reference photo, not the name. \
      A large bag (tote, shopper, weekender, duffel, backpack, etc.) should be worn over \
      one shoulder or carried by its top handle, matching how it is shown in the \
      reference photo. A small bag, mini bag, handbag, clutch, or wallet should be held \
      in one hand.
      - Clothing: determine whether it is a top or a bottom, and show it worn on the correct \
      body region (top on the torso/arms, bottom on the legs/waist) in a natural wearing \
      shot. If it is short-sleeved or short-length (shorts), make sure the skin that would \
      be exposed - not covered by the garment - is clearly visible and rendered to match \
      the person's own skin tone as seen elsewhere in the original photo (e.g. their face, \
      hands, or other exposed areas), not covered by anything that isn't actually part of \
      the item.
      - Scarf: wrap it naturally around the neck.
      - Footwear: determine whether it is a sandal/slide or a closed shoe (sneakers, boots, \
      loafers, etc.) and show it worn on the feet. If it is a sandal or slide, make sure the \
      parts of the foot not covered by the straps are clearly visible and rendered to match \
      the person's own skin tone as seen elsewhere in the original photo.

      Keep the person's face, hair, body proportions, pose, skin tone, and background \
      exactly as they are in the first photo. Do not alter anything else about the photo. \
      High quality, realistic lighting and shadows consistent with the original photo.
      """;
  private static final String PROMPT_TEMPLATE_NO_REFERENCE = """
      Photorealistic edit of the attached photo. The item is the MCM "%s" in %s color.

      First, determine what kind of fashion item this is based on its name, then edit \
      the photo so the person is naturally wearing or carrying it, following these \
      placement rules:

      - Bag: judge its apparent size from the name. A large bag (tote, shopper, weekender, \
      duffel, backpack, etc.) should be worn over one shoulder or carried by its top handle. \
      A small bag, mini bag, handbag, clutch, or wallet should be held in one hand.
      - Clothing: determine whether it is a top or a bottom, and show it worn on the correct \
      body region (top on the torso/arms, bottom on the legs/waist) in a natural wearing \
      shot. If it is short-sleeved or short-length (shorts), make sure the skin that would \
      be exposed - not covered by the garment - is clearly visible and rendered to match \
      the person's own skin tone as seen elsewhere in the original photo (e.g. their face, \
      hands, or other exposed areas), not covered by anything that isn't actually part of \
      the item.
      - Scarf: wrap it naturally around the neck.
      - Footwear: determine whether it is a sandal/slide or a closed shoe (sneakers, boots, \
      loafers, etc.) and show it worn on the feet. If it is a sandal or slide, make sure the \
      parts of the foot not covered by the straps are clearly visible and rendered to match \
      the person's own skin tone as seen elsewhere in the original photo.

      Keep the person's face, hair, body proportions, pose, skin tone, and background \
      exactly as they are in the original image. Do not alter anything else about the \
      photo. High quality, realistic lighting and shadows consistent with the original \
      photo.
      """;

  private final WebClient webClient;
  private final ObjectMapper objectMapper;
  private final AzureBlobStorageService azureBlobStorageService;

  @Value("${openai.api-key:}")
  private String apiKey;

  @Value("${openai.image-model:gpt-image-2}")
  private String imageModel;

  @Value("${openai.timeout-seconds:120}")
  private long timeoutSeconds;

  @Override
  public FittingGenerationResult generate(FittingGenerationCommand command) {
    if (apiKey == null || apiKey.isBlank()) {
      log.warn("OPENAI_API_KEY가 설정되지 않아 가상 피팅 이미지 생성을 건너뜁니다.");
      return FittingGenerationResult.failure();
    }

    try {
      FetchedImage sourceImage = fetchImage(command.sourceImageUrl());
      if (sourceImage == null) {
        return FittingGenerationResult.failure();
      }

      String productImageUrl = resolveProductImageUrl(command.productColor());
      FetchedImage productImage =
          productImageUrl != null ? fetchNormalizedProductImage(productImageUrl) : null;

      String b64Image = requestEditedImage(command, sourceImage, productImage);
      byte[] resultBytes = Base64.getDecoder().decode(b64Image);
      String resultUrl = azureBlobStorageService.uploadGeneratedImage(resultBytes, "image/png");

      return FittingGenerationResult.success(resultUrl);
    } catch (WebClientResponseException e) {
      log.warn("AI 가상 피팅 이미지 생성에 실패했습니다. status={}, body={}",
          e.getStatusCode(), e.getResponseBodyAsString(), e);
      return FittingGenerationResult.failure();
    } catch (Exception e) {
      log.warn("AI 가상 피팅 이미지 생성에 실패했습니다.", e);
      return FittingGenerationResult.failure();
    }
  }

  private FetchedImage fetchImage(String url) {
    ResponseEntity<byte[]> response = webClient.get()
        .uri(URI.create(url))
        .retrieve()
        .toEntity(byte[].class)
        .timeout(Duration.ofSeconds(timeoutSeconds))
        .block();

    byte[] bytes = response != null ? response.getBody() : null;
    if (bytes == null || bytes.length == 0) {
      return null;
    }
    MediaType contentType = response.getHeaders().getContentType() != null
        ? response.getHeaders().getContentType()
        : MediaType.IMAGE_JPEG;

    return new FetchedImage(bytes, contentType);
  }

  private FetchedImage fetchNormalizedProductImage(String url) {
    FetchedImage raw = fetchImage(url);
    if (raw == null) {
      return null;
    }
    byte[] normalized = normalizeToRgbPng(raw.bytes());
    if (normalized == null) {
      log.warn("상품 이미지를 표준 형식으로 변환하지 못했습니다: {}", url);
      return null;
    }
    return new FetchedImage(normalized, MediaType.IMAGE_PNG);
  }

  private byte[] normalizeToRgbPng(byte[] original) {
    try {
      BufferedImage source = ImageIO.read(new ByteArrayInputStream(original));
      if (source == null) {
        return null;
      }

      BufferedImage rgbImage = new BufferedImage(
          source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
      Graphics2D graphics = rgbImage.createGraphics();
      graphics.setColor(Color.WHITE);
      graphics.fillRect(0, 0, source.getWidth(), source.getHeight());
      graphics.drawImage(source, 0, 0, null);
      graphics.dispose();

      ByteArrayOutputStream output = new ByteArrayOutputStream();
      ImageIO.write(rgbImage, "png", output);
      return output.toByteArray();
    } catch (Exception e) {
      log.warn("이미지 정규화에 실패했습니다.", e);
      return null;
    }
  }

  private String resolveProductImageUrl(ProductColor productColor) {
    return productColor.getImages().stream()
        .min(Comparator.comparing(ProductImage::getOrderNo))
        .map(ProductImage::getImageUrl)
        .orElse(null);
  }

  private String requestEditedImage(
      FittingGenerationCommand command,
      FetchedImage sourceImage,
      FetchedImage productImage
  ) throws Exception {
    MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
    bodyBuilder.part("model", imageModel);
    bodyBuilder.part("prompt", buildPrompt(command.productColor(), productImage != null));
    bodyBuilder.part("quality", "medium");
    bodyBuilder.part("image[]", new ByteArrayResource(sourceImage.bytes()))
        .filename("person.jpg")
        .contentType(sourceImage.contentType());
    if (productImage != null) {
      bodyBuilder.part("image[]", new ByteArrayResource(productImage.bytes()))
          .filename("product.png")
          .contentType(productImage.contentType());
    }

    String raw = webClient.post()
        .uri(EDIT_URI)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
        .retrieve()
        .bodyToMono(String.class)
        .timeout(Duration.ofSeconds(timeoutSeconds))
        .block();

    JsonNode root = objectMapper.readTree(raw);
    String b64Image = root.path("data").path(0).path("b64_json").asText(null);
    if (b64Image == null || b64Image.isBlank()) {
      throw new IllegalStateException("OpenAI 응답에 이미지 데이터가 없습니다.");
    }
    return b64Image;
  }

  private String buildPrompt(ProductColor productColor, boolean hasProductReference) {
    String template = hasProductReference
        ? PROMPT_TEMPLATE_WITH_REFERENCE
        : PROMPT_TEMPLATE_NO_REFERENCE;
    return template.formatted(
        productColor.getProduct().getName(),
        productColor.getColorName()
    );
  }

  private record FetchedImage(byte[] bytes, MediaType contentType) {
  }
}
