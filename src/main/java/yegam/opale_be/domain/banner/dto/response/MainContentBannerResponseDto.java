package yegam.opale_be.domain.banner.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "MainContentBannerResponseDto", description = "메인 페이지 콘텐츠 배너 응답 DTO")
public class MainContentBannerResponseDto {

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
}
