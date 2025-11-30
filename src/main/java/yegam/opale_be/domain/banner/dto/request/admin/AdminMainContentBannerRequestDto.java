package yegam.opale_be.domain.banner.dto.request.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "AdminMainContentBannerRequestDto", description = "관리자 메인 콘텐츠 배너 등록/수정 요청 DTO")
public class AdminMainContentBannerRequestDto {

  @Schema(description = "배너 타이틀", example = "차은우·김재환, 군복 깜짝 투샷")
  private String title;

  @Schema(description = "배너 내용", example = "차은우, 군입대 후 첫 근황 공개")
  private String content;

  @Schema(description = "이동할 페이지 URL (선택)", example = "https://news.site/article/123")
  private String linkUrl;

  @Schema(description = "연결할 공연 ID (선택)", example = "PF264321")
  private String performanceId;

  @Schema(description = "노출 순서", example = "1")
  private Integer displayOrder;

  @Schema(description = "활성화 여부", example = "true")
  private Boolean isActive;
}
