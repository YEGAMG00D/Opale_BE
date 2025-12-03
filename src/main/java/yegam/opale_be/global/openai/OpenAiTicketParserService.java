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

  private static final String OPENAI_URL =
      "https://api.openai.com/v1/responses";

  public OpenAiTicketParserService(
      @Qualifier("openAiRestTemplate") RestTemplate restTemplate
  ) {
    this.restTemplate = restTemplate;
  }

  /**
   * ✅ 좌석 문자열 최소 보정
   * - "9일" / "11월" → "9열" / "11열"
   * - 맨 끝이 숫자면 "번" 자동 추가
   * - 객석 / 블록 / 존 / 섹션 / 석 / 구역 등은 절대 변경하지 않음
   */
  private String normalizeSeatInfo(String seatInfo) {
    if (seatInfo == null) return null;

    String text = seatInfo.trim();
    if (text.isEmpty()) return null;

    // ✅ OCR 오인식 보정: 숫자 + 일/월 → 숫자 + 열
    text = text.replaceAll("(\\d+)\\s*[일월]", "$1열");

    // ✅ 맨 끝이 숫자로 끝나면 "번" 자동 추가
    if (text.matches(".*\\d$")) {
      text = text + "번";
    }

    return text.trim();
  }

  /**
   * ✅ "다 11열 4번" → ["다 11열", "4"]
   * ✅ "객석1층 17열 15번" → ["객석1층 17열", "15"]
   */
  private String[] splitSeat(String seatInfo) {
    if (seatInfo == null)
      return new String[]{null, null};

    java.util.regex.Pattern p =
        java.util.regex.Pattern.compile("^(.*?)(\\d+)번$");
    java.util.regex.Matcher m = p.matcher(seatInfo.trim());

    if (m.matches()) {
      return new String[]{
          m.group(1).trim(),  // seatFront
          m.group(2).trim() + "번"   // seatNumber
      };
    }

    // fallback
    return new String[]{seatInfo, null};
  }

  public TicketOcrResponseDto parse(String rawText) {

    try {

      // ✅ 프롬프트 (너가 원하는 규칙 정확히 반영)
      String prompt = """
다음은 공연 티켓 OCR 텍스트이다.
아래 항목만 JSON 객체로 정확하게 추출해라.

{
  "performanceName": "공연명",
  "performanceDate": "yyyy-MM-dd HH:mm",
  "placeName": "공연장명",
  "seatInfo": "좌석 전체 문자열"
}

좌석 처리 규칙:
- 티켓에 적힌 좌석 용어(객석, 블록, 존, 섹션, 석, 구역 등)는 절대 다른 단어로 바꾸지 마라.
- 숫자 바로 뒤의 "일", "월"은 좌석의 "열"로 교정해라. (예: 9일 → 9열, 11월 → 11열)
- 좌석 번호 맨 끝의 숫자에는 반드시 "번"을 붙여라.
- seatInfo는 사람이 자연스럽게 읽을 수 있는 형태로 반환해라.
  예:
  - "1층 9일 23" → "1층 9열 23번"
  - "객석1층 17열 15" → "객석1층 17열 15번"
  - "1층 3구역 11월 8" → "1층 3구역 11열 8번"

조건:
- 값이 없으면 null
- 오직 JSON 객체 {}만 반환
- 절대 설명, 백틱, 추가 텍스트 넣지 마라

[OCR TEXT]
""" + rawText;

      Map<String, Object> body = new HashMap<>();
      body.put("model", model);
      body.put("input", prompt);
      body.put("temperature", 0);

      HttpEntity<Map<String, Object>> request = new HttpEntity<>(body);

      ResponseEntity<String> response =
          restTemplate.postForEntity(OPENAI_URL, request, String.class);

      JsonNode root = objectMapper.readTree(response.getBody());
      String rawJsonText = root.path("output").get(0)
          .path("content").get(0)
          .path("text").asText();

      String cleanJsonText = rawJsonText.trim();
      if (cleanJsonText.startsWith("```")) {
        cleanJsonText = cleanJsonText
            .replaceFirst("```[\\w]*\\n", "")
            .replaceAll("```", "")
            .trim();
      }

      JsonNode node = objectMapper.readTree(cleanJsonText);

      // ✅ 날짜 파싱
      LocalDateTime date = null;
      if (!node.path("performanceDate").isNull()) {
        String dateString = node.get("performanceDate").asText();
        if (!dateString.isBlank()) {
          date = LocalDateTime.parse(
              dateString.trim(),
              DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
          );
        }
      }

      // ✅ 좌석 정규화 + 분리
      String seatRaw = node.path("seatInfo").asText(null);
      String seatNormalized = normalizeSeatInfo(seatRaw);
      String[] seatParts = splitSeat(seatNormalized);

      log.info("✅ OCR 좌석 원본: {}", seatRaw);
      log.info("✅ OCR 좌석 보정 후: {}", seatNormalized);
      log.info("✅ OCR 좌석 분리: front={}, number={}", seatParts[0], seatParts[1]);

      return TicketOcrResponseDto.builder()
          .performanceName(node.path("performanceName").asText(null))
          .performanceDate(date)
          .seatInfo(seatNormalized)  // ✅ "1층 9열 23번"
          .seatFront(seatParts[0])  // ✅ "1층 9열"
          .seatNumber(seatParts[1]) // ✅ "23"
          .placeName(node.path("placeName").asText(null))
          .build();

    } catch (Exception e) {
      log.error("❌ GPT OCR 파싱 실패", e);
      throw new RuntimeException("GPT 티켓 파싱 실패");
    }
  }
}
