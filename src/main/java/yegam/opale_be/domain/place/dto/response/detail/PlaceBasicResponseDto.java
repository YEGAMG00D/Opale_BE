package yegam.opale_be.domain.place.dto.response.detail;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "PlaceBasicResponse DTO", description = "공연장 기본 정보 응답 DTO")
public class PlaceBasicResponseDto {

  @Schema(description = "공연장 ID", example = "PLC0001")
  private String placeId;

  @Schema(description = "공연장명", example = "세종문화회관")
  private String name;

  @Schema(description = "주소", example = "서울특별시 종로구 세종대로 175")
  private String address;

  @Schema(description = "전화번호", example = "02-399-1114")
  private String telno;

  @Schema(description = "공연장 성격", example = "공공기관")
  private String fcltychartr;

  @Schema(description = "개관년도", example = "1978")
  private Integer opende;

  @Schema(description = "총 좌석 수", example = "3022")
  private Integer seatscale;

  @Schema(description = "공연장 내 공연장 수", example = "3")
  private Integer stageCount;

  @Schema(description = "위도", example = "37.5721")
  private BigDecimal la;

  @Schema(description = "경도", example = "126.9769")
  private BigDecimal lo;

  @Schema(description = "관련 URL", example = "https://www.sejongpac.or.kr/")
  private String relateurl;
}
