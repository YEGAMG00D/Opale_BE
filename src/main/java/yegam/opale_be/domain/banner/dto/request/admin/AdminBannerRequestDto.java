package yegam.opale_be.domain.banner.dto.request.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "AdminBannerRequestDto", description = "관리자 배너 등록 요청 DTO")
public class AdminBannerRequestDto {

  @Schema(description = "연결할 공연 ID", example = "PF264321")
  private String performanceId;

  @Schema(description = "배너 메인 문구", example = "12년을 기다린 오리지널 내한공연")
  private String titleText;

  @Schema(description = "배너 부제 문구", example = "뮤지컬 위키드")
  private String subtitleText;

  @Schema(description = "배너 서브 설명", example = "The untold true story of the Witches of Oz")
  private String descriptionText;

  @Schema(description = "공연 날짜 텍스트", example = "2025.7.12 Flying Soon")
  private String dateText;

  @Schema(description = "공연 장소 텍스트", example = "BLUESQUARE 신한카드홀")
  private String placeText;

  @Schema(description = "S3 배너 이미지 URL")
  private String imageUrl;

  @Schema(description = "배너 클릭 시 이동할 URL", example = "https://withopale.com/performances/PF264321")
  private String linkUrl;

  @Schema(description = "노출 순서", example = "1")
  private Integer displayOrder;

  @Schema(description = "활성화 여부", example = "true")
  private Boolean isActive;
}
