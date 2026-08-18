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
  private static final String PROMPT = """
      Photorealistic edit of the first attached photo (the person). The second attached \
      photo shows the exact product to be added to the image.

      Treat the second image as the source of truth for the product's appearance. \
      Reproduce that exact product, preserving its silhouette, proportions, structure, \
      colors and color distribution, pattern, logo placement, material, texture, and \
      hardware. Use only the colors visibly shown in the reference image, including any \
      gradients or color transitions. Do not redesign, simplify, recolor, substitute, or \
      create a similar product.

      First, inspect the second image to determine what type of fashion item it actually \
      is and how it should naturally be worn, carried, or placed. Do not rely on the \
      product name alone.

      Apply the exact product naturally to the person with realistic scale, perspective, \
      lighting, shadows, and physical interaction with the person's body or clothing.

      For bags, use the carrying method appropriate to the bag's actual shape and size. \
      For clothing, place it on the correct body region. For scarves, wrap it naturally \
      around the neck. For footwear, place it naturally on the feet with correct scale \
      and orientation.

      Keep the person's face, hair, body proportions, pose, skin tone, clothing, \
      background, and composition unchanged. Do not alter anything unrelated to applying \
      the product.

      The final image should look like the exact product from the second reference image \
      was physically present in the original photograph.
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
      if (productImage == null) {
        log.warn("상품 참고 이미지를 가져오지 못해 가상 피팅 이미지 생성을 건너뜁니다.");
        return FittingGenerationResult.failure();
      }

      String b64Image = requestEditedImage(sourceImage, productImage);
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
      FetchedImage sourceImage,
      FetchedImage productImage
  ) throws Exception {
    MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
    bodyBuilder.part("model", imageModel);
    bodyBuilder.part("prompt", PROMPT);
    bodyBuilder.part("quality", "medium");
    bodyBuilder.part("image[]", new ByteArrayResource(sourceImage.bytes()))
        .filename("person.jpg")
        .contentType(sourceImage.contentType());
    bodyBuilder.part("image[]", new ByteArrayResource(productImage.bytes()))
        .filename("product.png")
        .contentType(productImage.contentType());

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

  private record FetchedImage(byte[] bytes, MediaType contentType) {
  }
}
