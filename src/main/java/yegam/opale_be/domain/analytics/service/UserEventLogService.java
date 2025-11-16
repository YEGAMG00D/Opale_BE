package yegam.opale_be.domain.analytics.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import yegam.opale_be.domain.analytics.dto.request.UserEventLogCreateRequestDto;
import yegam.opale_be.domain.analytics.dto.request.UserEventLogSearchRequestDto;
import yegam.opale_be.domain.analytics.dto.response.UserEventLogListResponseDto;
import yegam.opale_be.domain.analytics.dto.response.UserEventLogResponseDto;
import yegam.opale_be.domain.analytics.entity.UserEventLog;
import yegam.opale_be.domain.analytics.exception.AnalyticsErrorCode;
import yegam.opale_be.domain.analytics.mapper.UserEventLogMapper;
import yegam.opale_be.domain.analytics.repository.UserEventLogRepository;
import yegam.opale_be.domain.culture.performance.repository.PerformanceRepository;
import yegam.opale_be.domain.place.repository.PlaceRepository;
import yegam.opale_be.domain.chat.room.repository.ChatRoomRepository;

import yegam.opale_be.domain.user.entity.User;
import yegam.opale_be.domain.user.exception.UserErrorCode;
import yegam.opale_be.domain.user.repository.UserRepository;
import yegam.opale_be.global.exception.CustomException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserEventLogService {

  private final UserEventLogRepository userEventLogRepository;
  private final UserEventLogMapper userEventLogMapper;
  private final UserRepository userRepository;

  private final PerformanceRepository performanceRepository;
  private final PlaceRepository placeRepository;
  private final ChatRoomRepository chatRoomRepository;

  private static final Map<UserEventLog.EventType, Integer> DEFAULT_WEIGHTS = Map.of(
      UserEventLog.EventType.VIEW, 1,
      UserEventLog.EventType.FAVORITE, 3,
      UserEventLog.EventType.BOOKED, 5,
      UserEventLog.EventType.REVIEW_WRITE, 10
  );

  /** ⭐ 사용자 행동 로그 생성 + 조회수 증가 */
  @Transactional
  public UserEventLogResponseDto createUserEventLog(Long userId, UserEventLogCreateRequestDto dto) {

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    UserEventLog.EventType eventType = parseEventType(dto.getEventType());
    int weight = determineWeight(eventType, dto.getWeight());

    UserEventLog entity = userEventLogMapper.toEntity(user, dto, weight);
    UserEventLog saved = userEventLogRepository.save(entity);

    log.info("🎯 로그 생성: user={}, type={}, targetType={}, targetId={}",
        userId, eventType, dto.getTargetType(), dto.getTargetId());

    // ========================
    // ⭐ 조회수 증가 로직
    // ========================
    if (eventType == UserEventLog.EventType.VIEW) {

      switch (dto.getTargetType().toUpperCase()) {

        case "PERFORMANCE" -> {
          performanceRepository.incrementViewCount(dto.getTargetId());
          log.info("📈 공연 조회수 +1 → {}", dto.getTargetId());
        }

        case "PLACE" -> {
          placeRepository.incrementViewCount(dto.getTargetId());
          log.info("📈 공연장 조회수 +1 → {}", dto.getTargetId());
        }

        case "CHATROOM" -> {
          chatRoomRepository.incrementVisitCount(Long.valueOf(dto.getTargetId()));
          log.info("📈 채팅방 방문수 +1 → {}", dto.getTargetId());
        }

        default -> log.warn("⚠ 알 수 없는 VIEW targetType={}", dto.getTargetType());
      }
    }

    return userEventLogMapper.toResponseDto(saved);
  }

  // ===========================
  // 검색 함수 및 내부 유틸들
  // ===========================

  public UserEventLogListResponseDto searchUserEventLogs(UserEventLogSearchRequestDto dto) {

    Long userId = dto.getUserId();
    UserEventLog.EventType eventType = null;
    if (dto.getEventType() != null && !dto.getEventType().isBlank()) {
      eventType = parseEventType(dto.getEventType());
    }

    UserEventLog.TargetType targetType = null;
    if (dto.getTargetType() != null && !dto.getTargetType().isBlank()) {
      targetType = parseTargetType(dto.getTargetType());
    }

    String targetId = (dto.getTargetId() != null && !dto.getTargetId().isBlank())
        ? dto.getTargetId() : null;

    LocalDateTime startAt = null;
    LocalDateTime endAt = null;

    if (dto.getStartDate() != null && !dto.getStartDate().isBlank()) {
      LocalDate start = LocalDate.parse(dto.getStartDate());
      startAt = start.atStartOfDay();
    }

    if (dto.getEndDate() != null && !dto.getEndDate().isBlank()) {
      LocalDate end = LocalDate.parse(dto.getEndDate());
      endAt = end.atTime(23, 59, 59);
    }

    if (startAt != null && endAt != null && startAt.isAfter(endAt)) {
      throw new CustomException(AnalyticsErrorCode.INVALID_DATE_RANGE);
    }

    Pageable pageable = PageRequest.of(
        dto.getPage() != null ? dto.getPage() - 1 : 0,
        dto.getSize() != null ? dto.getSize() : 20,
        Sort.by(Sort.Direction.DESC, "createdAt")
    );

    var result = userEventLogRepository.searchLogs(
        userId, eventType, targetType, targetId, startAt, endAt, pageable
    );

    return userEventLogMapper.toListResponseDto(result);
  }

  private UserEventLog.EventType parseEventType(String value) {
    try {
      return UserEventLog.EventType.valueOf(value.toUpperCase());
    } catch (Exception e) {
      throw new CustomException(AnalyticsErrorCode.INVALID_EVENT_TYPE);
    }
  }

  private UserEventLog.TargetType parseTargetType(String value) {
    try {
      return UserEventLog.TargetType.valueOf(value.toUpperCase());
    } catch (Exception e) {
      throw new CustomException(AnalyticsErrorCode.INVALID_TARGET_TYPE);
    }
  }

  private int determineWeight(UserEventLog.EventType eventType, Integer requested) {
    if (requested != null) return requested;
    return DEFAULT_WEIGHTS.getOrDefault(eventType, 1);
  }
}
