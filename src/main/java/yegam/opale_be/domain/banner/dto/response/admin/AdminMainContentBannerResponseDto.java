package yegam.opale_be.domain.banner.dto.response.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "AdminMainContentBannerResponseDto", description = "관리자 메인 콘텐츠 배너 응답 DTO")
public class AdminMainContentBannerResponseDto {

  @Schema(description = "콘텐츠 배너 ID")
  private Long contentBannerId;

  @Schema(description = "배너 타이틀")
  private String title;

  @Schema(description = "배너 내용")
  private String content;

  @Schema(description = "이미지 URL")
  private String imageUrl;

  @Schema(description = "이동 링크 URL")
  private String linkUrl;

  @Schema(description = "연결된 공연 ID")
  private String performanceId;

  @Schema(description = "노출 순서")
  private Integer displayOrder;

  @Schema(description = "활성화 여부")
  private Boolean isActive;
}
