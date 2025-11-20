package yegam.opale_be.domain.reservation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 티켓 이미지 OCR 결과 응답 DTO
 * - 티켓 이미지에서 추출된 예매 정보 자동 채우기용
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "TicketOcrResponse DTO", description = "티켓 이미지 OCR 결과 응답 DTO")
public class TicketOcrResponseDto {


  @Schema(description = "공연명", example = "뮤지컬 위키드 내한공연")
  private String performanceName;

  @Schema(description = "공연 관람 날짜 및 시간 (LocalDateTime 형식)", example = "2025-10-23T19:00:00")
  private LocalDateTime performanceDate;

  @Schema(description = "좌석 정보", example = "나 구역 15열 23번")
  private String seatInfo;

  @Schema(description = "공연장명", example = "블루스퀘어 신한카드홀")
  private String placeName;
}
