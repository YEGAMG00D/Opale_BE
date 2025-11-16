package yegam.opale_be.domain.analytics.mapper;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import yegam.opale_be.domain.analytics.dto.request.UserEventLogCreateRequestDto;
import yegam.opale_be.domain.analytics.dto.response.UserEventLogListResponseDto;
import yegam.opale_be.domain.analytics.dto.response.UserEventLogResponseDto;
import yegam.opale_be.domain.analytics.entity.UserEventLog;
import yegam.opale_be.domain.analytics.exception.AnalyticsErrorCode;
import yegam.opale_be.domain.user.entity.User;
import yegam.opale_be.global.exception.CustomException;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UserEventLogMapper
 * - UserEventLog Entity <-> DTO 변환 전담
 */
@Component
public class UserEventLogMapper {

  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormatter.ISO_LOCAL_DATE_TIME;

  /**
   * 로그 생성용 Entity 변환
   * - DTO + User + weight → UserEventLog Entity
   */
  public UserEventLog toEntity(User user, UserEventLogCreateRequestDto dto, int weight) {
    if (dto == null) {
      throw new CustomException(AnalyticsErrorCode.ANALYTICS_DATA_ACCESS_ERROR);
    }

    UserEventLog.EventType eventType = parseEventType(dto.getEventType());
    UserEventLog.TargetType targetType = parseTargetType(dto.getTargetType());

    return UserEventLog.builder()
        .user(user)
        .eventType(eventType)
        .targetType(targetType)
        .targetId(dto.getTargetId())
        .weight(weight)
        .build();
  }

  /**
   * 단일 로그 → 응답 DTO 변환
   */
  public UserEventLogResponseDto toResponseDto(UserEventLog log) {
    if (log == null) return null;

    return UserEventLogResponseDto.builder()
        .logId(log.getLogId())
        .userId(log.getUser() != null ? log.getUser().getUserId() : null)
        .eventType(log.getEventType() != null ? log.getEventType().name() : null)
        .targetType(log.getTargetType() != null ? log.getTargetType().name() : null)
        .targetId(log.getTargetId())
        .weight(log.getWeight())
        .createdAt(log.getCreatedAt() != null ? log.getCreatedAt().format(DATE_TIME_FORMATTER) : null)
        .build();
  }

  /**
   * Page<UserEventLog> → UserEventLogListResponseDto 변환
   */
  public UserEventLogListResponseDto toListResponseDto(Page<UserEventLog> page) {
    List<UserEventLogResponseDto> dtoList = page.getContent().stream()
        .map(this::toResponseDto)
        .collect(Collectors.toList());

    return UserEventLogListResponseDto.builder()
        .totalCount(page.getTotalElements())
        .currentPage(page.getNumber() + 1)
        .pageSize(page.getSize())
        .totalPages(page.getTotalPages())
        .hasNext(page.hasNext())
        .hasPrev(page.hasPrevious())
        .logs(dtoList)
        .build();
  }

  /**
   * 문자열 → EventType enum 변환
   */
  private UserEventLog.EventType parseEventType(String value) {
    if (value == null || value.isBlank()) {
      throw new CustomException(AnalyticsErrorCode.INVALID_EVENT_TYPE);
    }
    try {
      return UserEventLog.EventType.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new CustomException(AnalyticsErrorCode.INVALID_EVENT_TYPE);
    }
  }

  /**
   * 문자열 → TargetType enum 변환
   */
  private UserEventLog.TargetType parseTargetType(String value) {
    if (value == null || value.isBlank()) {
      throw new CustomException(AnalyticsErrorCode.INVALID_TARGET_TYPE);
    }
    try {
      return UserEventLog.TargetType.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new CustomException(AnalyticsErrorCode.INVALID_TARGET_TYPE);
    }
  }
}
