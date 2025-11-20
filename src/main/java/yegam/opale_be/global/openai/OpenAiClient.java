package yegam.opale_be.global.openai;

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
public class OpenAiClient {

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper = new ObjectMapper();

  // 🔥 Vision + 이미지 입력 가능한 공식 엔드포인트
  private static final String OPENAI_URL = "https://api.openai.com/v1/responses";

  @Value("${openai.api-key}")
  private String apiKey;

  public Map<String, String> requestOcr(String prompt, String base64Image) {

    try {
      // 1) Vision 메시지 포맷 구성
      Map<String, Object> userMessage = new HashMap<>();
      List<Object> contentList = new ArrayList<>();

      // 🔹 텍스트 프롬프트
      contentList.add(Map.of(
          "type", "text",
          "text", prompt
      ));

      // 🔹 이미지(base64)
      contentList.add(Map.of(
          "type", "image_url",
          "image_url", Map.of(
              "url", "data:image/png;base64," + base64Image
          )
      ));

      userMessage.put("role", "user");
      userMessage.put("content", contentList);

      // 2) request body — ★ Responses API 전용
      Map<String, Object> body = new HashMap<>();
      body.put("model", "gpt-4o-mini");         // Vision 지원 모델
      body.put("input", List.of(userMessage));  // Chat-like 방식은 messages → Responses는 input

      String jsonBody = objectMapper.writeValueAsString(body);

      log.warn("📤 [OpenAI Request JSON] {}", jsonBody);

      // 3) 헤더
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setAccept(List.of(MediaType.APPLICATION_JSON));
      headers.setBearerAuth(apiKey);
      headers.set("User-Agent", "Opale-SpringBoot");

      HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

      // 4) 요청 보내기
      ResponseEntity<String> responseEntity =
          restTemplate.exchange(OPENAI_URL, HttpMethod.POST, request, String.class);

      String response = responseEntity.getBody();
      log.warn("📥 [OpenAI Response JSON] {}", response);

      // 5) 응답 파싱
      Map<String, Object> json = objectMapper.readValue(response, Map.class);

      List<Map<String, Object>> outputs = (List<Map<String, Object>>) json.get("output_text");
      String content = outputs.get(0).toString();  // 첫 번째 텍스트 응답

      // GPT가 준 JSON 문자열 → Map 변환
      return objectMapper.readValue(content, Map.class);

    } catch (Exception e) {
      log.error("❌ OCR API 호출 실패", e);
      throw new RuntimeException("OpenAI Vision OCR 호출 실패");
    }
  }
}
