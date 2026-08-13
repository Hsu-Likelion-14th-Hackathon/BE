package com.boardingpass.be.domain.fitting.generator;

import com.boardingpass.be.domain.product.ProductColor;
import com.boardingpass.be.domain.storage.AzureBlobStorageService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Base64;
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

@Slf4j
@Primary
@Service
@RequiredArgsConstructor
public class RealFittingImageGenerator implements FittingImageGenerator {

  private static final String EDIT_URI = "https://api.openai.com/v1/images/edits";
  private static final String PROMPT_TEMPLATE = """
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

  @Value("${openai.image-model:gpt-image-1}")
  private String imageModel;

  @Value("${openai.timeout-seconds:5}")
  private long timeoutSeconds;

  @Override
  public FittingGenerationResult generate(FittingGenerationCommand command) {
    if (apiKey == null || apiKey.isBlank()) {
      log.warn("OPENAI_API_KEY가 설정되지 않아 가상 피팅 이미지 생성을 건너뜁니다.");
      return FittingGenerationResult.failure();
    }

    try {
      ResponseEntity<byte[]> sourceResponse = webClient.get()
          .uri(command.sourceImageUrl())
          .retrieve()
          .toEntity(byte[].class)
          .timeout(Duration.ofSeconds(timeoutSeconds))
          .block();

      byte[] sourceBytes = sourceResponse != null ? sourceResponse.getBody() : null;
      if (sourceBytes == null || sourceBytes.length == 0) {
        return FittingGenerationResult.failure();
      }
      MediaType sourceContentType = sourceResponse.getHeaders().getContentType() != null
          ? sourceResponse.getHeaders().getContentType()
          : MediaType.IMAGE_JPEG;

      String b64Image = requestEditedImage(command, sourceBytes, sourceContentType);
      byte[] resultBytes = Base64.getDecoder().decode(b64Image);
      String resultUrl = azureBlobStorageService.uploadGeneratedImage(resultBytes, "image/png");

      return FittingGenerationResult.success(resultUrl);
    } catch (Exception e) {
      log.warn("AI 가상 피팅 이미지 생성에 실패했습니다.", e);
      return FittingGenerationResult.failure();
    }
  }

  private String requestEditedImage(
      FittingGenerationCommand command,
      byte[] sourceBytes,
      MediaType sourceContentType
  ) throws Exception {
    MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
    bodyBuilder.part("model", imageModel);
    bodyBuilder.part("prompt", buildPrompt(command.productColor()));
    bodyBuilder.part("image", new ByteArrayResource(sourceBytes))
        .filename("source.jpg")
        .contentType(sourceContentType);

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

  private String buildPrompt(ProductColor productColor) {
    return PROMPT_TEMPLATE.formatted(
        productColor.getProduct().getName(),
        productColor.getColorName()
    );
  }
}
