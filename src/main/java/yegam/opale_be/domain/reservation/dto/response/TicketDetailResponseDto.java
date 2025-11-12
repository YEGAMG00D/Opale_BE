package yegam.opale_be.domain.reservation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 티켓 인증 단일 상세 조회 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "TicketResponse DTO", description = "티켓 인증 단일 상세 응답 DTO")
public class TicketDetailResponseDto {

  @Schema(description = "티켓 고유 ID", example = "101")
  private Long ticketId;

  @Schema(description = "예매 번호", example = "A123456789")
  private String ticketNumber;

  @Schema(description = "공연명", example = "뮤지컬 위키드 내한공연")
  private String performanceName;

  @Schema(description = "공연 ID (일치하는 공연 없으면 null)", example = "PF12345")
  private String performanceId;

  @Schema(description = "공연장 ID (일치하는 공연장 없으면 null)", example = "PLC0001")
  private String placeId;

  @Schema(description = "공연 관람 날짜 및 시간", example = "2025-10-23T19:00:00")
  private LocalDateTime performanceDate;

  @Schema(description = "좌석 정보", example = "나 구역 15열 23번")
  private String seatInfo;

  @Schema(description = "공연장명", example = "블루스퀘어 신한카드홀")
  private String placeName;

  @Schema(description = "티켓 이미지 URL (MANUAL, ADMIN은 null 가능)", example = "https://s3.amazonaws.com/opale/tickets/123.png")
  private String ticketImageUrl;

  @Schema(description = "티켓 인증 여부", example = "true")
  private Boolean isVerified;

  @Schema(description = "인증 요청 시각", example = "2025-10-20T15:00:00")
  private LocalDateTime requestedAt;

  @Schema(description = "마지막 수정 시각", example = "2025-10-21T11:00:00")
  private LocalDateTime updatedAt;
}
