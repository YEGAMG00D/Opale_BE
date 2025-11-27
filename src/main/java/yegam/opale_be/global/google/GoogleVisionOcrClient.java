package yegam.opale_be.global.google;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleVisionOcrClient {

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Value("${google.vision.api-key}")
  private String apiKey;

  private static final String GOOGLE_VISION_URL =
      "https://vision.googleapis.com/v1/images:annotate?key=%s";

  public String extractTextFromImageBase64(String base64Image) {

    try {
      Map<String, Object> image = new HashMap<>();
      image.put("content", base64Image);

      Map<String, Object> feature = new HashMap<>();
      feature.put("type", "TEXT_DETECTION");

      Map<String, Object> request = new HashMap<>();
      request.put("image", image);
      request.put("features", List.of(feature));

      Map<String, Object> body = new HashMap<>();
      body.put("requests", List.of(request));

      String jsonBody = objectMapper.writeValueAsString(body);

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      HttpEntity<String> httpEntity = new HttpEntity<>(jsonBody, headers);

      String url = String.format(GOOGLE_VISION_URL, apiKey);

      ResponseEntity<String> response =
          restTemplate.postForEntity(url, httpEntity, String.class);

      JsonNode root = objectMapper.readTree(response.getBody());

      JsonNode annotations =
          root.path("responses").get(0).path("textAnnotations");

      if (!annotations.isArray() || annotations.isEmpty()) {
        return "";
      }

      return annotations.get(0).path("description").asText();

    } catch (Exception e) {
      log.error("❌ Google Vision OCR 실패", e);
      throw new RuntimeException("Google OCR 호출 실패");
    }
  }
}
