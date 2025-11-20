package yegam.opale_be.domain.reservation.mapper;

import org.springframework.stereotype.Component;
import yegam.opale_be.domain.reservation.dto.request.TicketCreateRequestDto;
import yegam.opale_be.domain.reservation.dto.response.TicketDetailResponseDto;
import yegam.opale_be.domain.reservation.dto.response.TicketSimpleResponseDto;
import yegam.opale_be.domain.reservation.entity.UserTicketVerification;
import yegam.opale_be.domain.reservation.entity.Source;
import yegam.opale_be.domain.culture.performance.entity.Performance;
import yegam.opale_be.domain.place.entity.Place;
import yegam.opale_be.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ReservationMapper
 * - 티켓 인증 관련 Entity ↔ DTO 변환 담당
 */
@Component
public class ReservationMapper {

  /** ✅ 티켓 등록 요청 DTO → Entity */
  public UserTicketVerification toEntity(
      TicketCreateRequestDto dto,
      User user,
      Performance performance,
      Place place
  ) {
    return UserTicketVerification.builder()
        .performanceName(dto.getPerformanceName())
        .placeName(dto.getPlaceName())
        .performance(performance)   // null 가능
        .place(place)               // null 가능
        .user(user)
        .seatInfo(dto.getSeatInfo())
        .performanceDate(dto.getPerformanceDate())
        .source(Source.MANUAL)
        .ticketImageUrl(null)
        .isVerified(false)
        .requestedAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }

  /** ✅ 단일 상세 Entity → DTO */
  public TicketDetailResponseDto toDetailResponseDto(UserTicketVerification entity) {
    return TicketDetailResponseDto.builder()
        .ticketId(entity.getTicketId())
        .performanceName(entity.getPerformanceName())
        .performanceId(
            entity.getPerformance() != null
                ? entity.getPerformance().getPerformanceId()
                : null
        )
        .placeId(
            entity.getPlace() != null
                ? entity.getPlace().getPlaceId()
                : null
        )
        .performanceDate(entity.getPerformanceDate())
        .seatInfo(entity.getSeatInfo())
        .placeName(entity.getPlaceName())
        .ticketImageUrl(entity.getTicketImageUrl())
        .isVerified(entity.getIsVerified())
        .requestedAt(entity.getRequestedAt())
        .updatedAt(entity.getUpdatedAt())
        .source(entity.getSource().name())
        .build();
  }

  /** ✅ 목록용 Entity → 단일 요약 DTO */
  public TicketSimpleResponseDto toSimpleResponseDto(UserTicketVerification entity) {
    return TicketSimpleResponseDto.builder()
        .ticketId(entity.getTicketId())
        .performanceName(entity.getPerformanceName())
        .performanceDate(entity.getPerformanceDate())
        .seatInfo(entity.getSeatInfo())
        .placeName(entity.getPlaceName())
        .build();
  }

  /** Entity 목록 → SimpleResponseDto List */
  public List<TicketSimpleResponseDto> toSimpleResponseDtoList(List<UserTicketVerification> list) {
    return list.stream()
        .map(this::toSimpleResponseDto)
        .collect(Collectors.toList());
  }
}
