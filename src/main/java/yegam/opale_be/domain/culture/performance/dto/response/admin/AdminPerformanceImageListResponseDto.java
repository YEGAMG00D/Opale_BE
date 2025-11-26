package yegam.opale_be.domain.culture.performance.dto.response.admin;

import lombok.*;
import java.sql.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminPerformanceImageListResponseDto {

  // 공연 정보
  private String performanceId;
  private String title;
  private String placeName;
  private Date startDate;
  private Date endDate;

  // 이미지 개수
  private int totalCount;
  private int discountCount;
  private int castingCount;
  private int seatCount;
  private int noticeCount;
  private int otherCount;

  // 타입별 이미지 리스트
  private List<AdminPerformanceImageResponseDto> discountImages;
  private List<AdminPerformanceImageResponseDto> castingImages;
  private List<AdminPerformanceImageResponseDto> seatImages;
  private List<AdminPerformanceImageResponseDto> noticeImages;
  private List<AdminPerformanceImageResponseDto> otherImages;
}
