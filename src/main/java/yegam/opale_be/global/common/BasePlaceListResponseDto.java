package yegam.opale_be.global.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.List;

/**
 * 공통 공연장 리스트 응답 DTO
 * (공연장 ID + 공연장명 + 주소 + 총 개수 + 데이터 목록)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "BasePlaceListResponse DTO", description = "공연장 ID, 공연장명, 주소, 총 개수, 데이터 목록을 포함하는 기본 리스트 응답 구조")
public class BasePlaceListResponseDto<T> {

  @Schema(description = "공연장 ID", example = "PLC0001")
  private String placeId;

  @Schema(description = "공연장명", example = "세종문화회관")
  private String placeName;

  @Schema(description = "주소", example = "서울특별시 종로구 세종대로 175")
  private String address;

  @Schema(description = "총 항목 개수", example = "3")
  private int totalCount;

  @Schema(description = "데이터 목록")
  private List<T> items;
}
