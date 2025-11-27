package yegam.opale_be.domain.banner.dto.response.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "AdminBannerResponseDto", description = "관리자 배너 응답 DTO")
public class AdminBannerResponseDto {

  @Schema(description = "배너 ID", example = "1")
  private Long bannerId;

  @Schema(description = "연결된 공연 ID", example = "PF264321")
  private String performanceId;

  @Schema(description = "배너 메인 문구")
  private String titleText;

  @Schema(description = "배너 부제")
  private String subtitleText;

  @Schema(description = "배너 설명 문구")
  private String descriptionText;

  @Schema(description = "공연 날짜 텍스트")
  private String dateText;

  @Schema(description = "공연 장소 텍스트")
  private String placeText;

  @Schema(description = "배너 이미지 S3 URL")
  private String imageUrl;

  @Schema(description = "노출 순서")
  private Integer displayOrder;

  @Schema(description = "활성화 여부")
  private Boolean isActive;
}
