package yegam.opale_be.global.openai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import yegam.opale_be.domain.reservation.dto.response.TicketOcrResponseDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class OpenAiTicketParserService {

  private final RestTemplate restTemplate;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Value("${openai.gpt-model}")
  private String model;

  // 실제 API URL에 맞게 수정이 필요할 수 있습니다. (예: /v1/chat/completions)
  private static final String OPENAI_URL =
      "https://api.openai.com/v1/responses";

  // Qualifier 유지
  public OpenAiTicketParserService(
      @Qualifier("openAiRestTemplate") RestTemplate restTemplate
  ) {
    this.restTemplate = restTemplate;
  }

  public TicketOcrResponseDto parse(String rawText) {

    try {

      // ✅ 컴파일 오류를 일으키던 백슬래시를 제거했습니다. (```json 부분 참고)
      String prompt = """
다음은 공연 티켓 OCR 텍스트이다.
아래 항목만 JSON 객체로 정확하게 추출해라.

{
  "performanceName": "공연명",
  "performanceDate": "yyyy-MM-dd HH:mm",
  "placeName": "공연장명",
  "seatInfo": "구역-열-번"
}

조건:
- 값이 없으면 null
- 오직 JSON 객체 {}만 반환해라. (가장 중요)
- JSON 객체 앞뒤에 절대 백틱(```), 설명, 또는 추가 텍스트를 넣지 마라.

[OCR TEXT]
""" + rawText;

      Map<String, Object> body = new HashMap<>();
      body.put("model", model);
      body.put("input", prompt);
      body.put("temperature", 0);

      HttpEntity<Map<String, Object>> request =
          new HttpEntity<>(body);

      ResponseEntity<String> response =
          restTemplate.postForEntity(OPENAI_URL, request, String.class);

      // 1. 최상위 응답에서 GPT가 생성한 텍스트 추출
      JsonNode root = objectMapper.readTree(response.getBody());
      String rawJsonText = root
          .path("output")
          .get(0)
          .path("content")
          .get(0)
          .path("text")
          .asText();

      // 2. ✅ JSON 클린징 로직: 백틱, markdown 태그 등 불필요한 문자 제거 (지난번 파싱 오류 해결)
      String cleanJsonText = rawJsonText.trim();

      // GPT가 실수로 마크다운 코드를 감싸서 보낼 경우를 대비해 처리
      if (cleanJsonText.startsWith("```")) {
        // 백틱과 뒤따르는 언어 이름(예: json)을 제거
        cleanJsonText = cleanJsonText.replaceFirst("```[\\w]*\\n", "").trim();

        // 마지막 백틱(```) 제거
        if (cleanJsonText.endsWith("```")) {
          cleanJsonText = cleanJsonText.substring(0, cleanJsonText.length() - 3).trim();
        }
      }

      // 3. 클린징된 텍스트를 JSON으로 파싱 시도 (이전 JsonParseException 발생 지점)
      JsonNode node = objectMapper.readTree(cleanJsonText);

      // 4. 데이터 추출 및 DTO 빌드
      LocalDateTime date = null;
      if (!node.path("performanceDate").isNull() && node.get("performanceDate") != null) {
        String dateString = node.get("performanceDate").asText();
        // 날짜 파싱 시 혹시 모를 공백 제거 및 길이 확인
        if (!dateString.isBlank()) {
          try {
            date = LocalDateTime.parse(
                dateString.trim(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            );
          } catch (Exception dateParseEx) {
            log.warn("날짜 파싱 실패: {}", dateString, dateParseEx);
            // date는 null로 유지
          }
        }
      }

      // ✅ GPT 호출 성공 로그 추가
      log.info("✅ GPT OCR 파싱 성공. 추출된 JSON: {}", cleanJsonText);


      return TicketOcrResponseDto.builder()
          .performanceName(node.path("performanceName").asText(null))
          .performanceDate(date)
          .seatInfo(node.path("seatInfo").asText(null))
          .placeName(node.path("placeName").asText(null))
          .build();

    } catch (Exception e) {
      log.error("❌ GPT OCR 파싱 실패: JSON 구조 또는 응답 텍스트 문제", e);
      throw new RuntimeException("GPT 티켓 파싱 실패");
    }
  }
}