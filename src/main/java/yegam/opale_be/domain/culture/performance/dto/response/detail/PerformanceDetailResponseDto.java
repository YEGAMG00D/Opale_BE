package yegam.opale_be.domain.culture.performance.dto.response.detail;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.List;

/**
 * 공연 상세 정보 응답 DTO
 * - 공연 기본 정보 + 영상 + 이미지 + 예매처 등 포함
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "PerformanceDetailResponse DTO", description = "공연의 상세 정보를 제공하는 DTO")
public class PerformanceDetailResponseDto {

  @Schema(description = "공연 ID", example = "PF12345")
  private String performanceId;

  @Schema(description = "공연명", example = "레미제라블")
  private String title;

  @Schema(description = "티켓 가격", example = "R석 150,000원 / S석 120,000원")
  private String price;


  // 할인 정보 이미지 목록, 좌석배치도 이미지 목록, 캐스팅 이미지 목록
  @Schema(description = "할인 정보 이미지 목록")
  private List<PerformanceImageResponseDto> discountImages;

  @Schema(description = "좌석 배치도 이미지 목록")
  private List<PerformanceImageResponseDto> seatImages;

  @Schema(description = "캐스팅 이미지 목록")
  private List<PerformanceImageResponseDto> castingImages;

  @Schema(description = "공지/안내 이미지 목록")
  private List<PerformanceImageResponseDto> noticeImages;

  @Schema(description = "기타 이미지 목록")
  private List<PerformanceImageResponseDto> otherImages;

}
