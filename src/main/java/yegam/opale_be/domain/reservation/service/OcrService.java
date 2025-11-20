package yegam.opale_be.domain.reservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import yegam.opale_be.global.exception.CustomException;
import yegam.opale_be.domain.reservation.exception.ReservationErrorCode;
import yegam.opale_be.global.openai.OpenAiClient;

import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OcrService {

  private final OpenAiClient openAiClient;

  public Map<String, String> extractFromImage(MultipartFile file) {
    try {
      String base64 = Base64.getEncoder().encodeToString(file.getBytes());

      String prompt = """
                다음 공연 티켓 이미지에서 아래 항목을 JSON 형태로 정확하게 반환해줘.
                - ticketNumber
                - performanceName
                - performanceDate (YYYY-MM-DD HH:mm)
                - seatInfo
                - placeName
                
                JSON만 반환해.
            """;

      return openAiClient.requestOcr(prompt, base64);

    } catch (Exception e) {
      log.error("❌ OCR 처리 실패", e);
      throw new CustomException(ReservationErrorCode.INVALID_TICKET_DATA);
    }
  }
}
