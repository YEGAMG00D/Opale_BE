package yegam.opale_be.domain.banner.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "MainBannerResponseDto", description = "메인 페이지 배너 응답 DTO")
public class MainBannerResponseDto {

  @Schema(description = "배너 ID", example = "1")
  private Long bannerId;

  @Schema(description = "배너 이미지 URL", example = "https://s3.amazonaws.com/...")
  private String imageUrl;

  @Schema(description = "연결된 공연 ID", example = "PF271999")
  private String performanceId;

  @Schema(description = "배너 메인 문구", example = "12년을 기다린 오리지널 내한공연")
  private String titleText;

  @Schema(description = "배너 부제", example = "WICKED")
  private String subtitleText;

  @Schema(description = "배너 설명 문구", example = "뮤지컬 위키드")
  private String descriptionText;

  @Schema(description = "공연 날짜 텍스트", example = "2025.7.12 Flying Soon")
  private String dateText;

  @Schema(description = "공연 장소 텍스트", example = "BLUESQUARE 신한카드홀")
  private String placeText;
}
