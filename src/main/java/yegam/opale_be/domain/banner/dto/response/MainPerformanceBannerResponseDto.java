package yegam.opale_be.domain.banner.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "MainPerformanceBannerResponseDto", description = "메인 페이지 공연 배너 응답 DTO")
public class MainPerformanceBannerResponseDto {

  @Schema(description = "배너 ID")
  private Long bannerId;

  @Schema(description = "공연 ID")
  private String performanceId;

  @Schema(description = "공연명")
  private String title;

  @Schema(description = "공연 시작일")
  private String startDate;

  @Schema(description = "공연 종료일")
  private String endDate;

  @Schema(description = "공연 장소")
  private String placeName;

  @Schema(description = "장르명", example = "뮤지컬")
  private String genrenm;

  @Schema(description = "평점", example = "4.7")
  private Double rating;

  @Schema(description = "포스터 이미지 URL")
  private String posterUrl;




}
