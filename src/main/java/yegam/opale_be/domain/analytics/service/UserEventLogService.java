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

  /**
   * 이벤트 타입별 기본 가중치 설정
   * - VIEW: 1
   * - FAVORITE: 3
   * - BOOKED: 5
   * - REVIEW_WRITE: 10
   */
  private static final Map<UserEventLog.EventType, Integer> DEFAULT_WEIGHTS = Map.of(
      UserEventLog.EventType.VIEW, 1,
      UserEventLog.EventType.FAVORITE, 3,
      UserEventLog.EventType.BOOKED, 5,
      UserEventLog.EventType.REVIEW_WRITE, 10
  );

  /**
   * 사용자 행동 로그 생성
   *
   * @param userId 인증된 사용자 ID (@AuthenticationPrincipal)
   * @param dto    이벤트 정보
   * @return 생성된 로그 응답 DTO
   */
  @Transactional
  public UserEventLogResponseDto createUserEventLog(Long userId, UserEventLogCreateRequestDto dto) {
    // 1) 사용자 검증
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    // 2) 이벤트 타입 파싱 (Mapper 내부에서 검증)
    UserEventLog.EventType eventType = parseEventType(dto.getEventType());

    // 3) weight 결정 (DTO에 없으면 기본 가중치 사용)
    int weight = determineWeight(eventType, dto.getWeight());

    // 4) Entity 생성 & 저장
    UserEventLog entity = userEventLogMapper.toEntity(user, dto, weight);
    UserEventLog saved = userEventLogRepository.save(entity);

    log.info("✅ 사용자 행동 로그 생성: userId={}, eventType={}, targetType={}, targetId={}, weight={}",
        userId, eventType, dto.getTargetType(), dto.getTargetId(), weight);

    // 5) 응답 DTO 변환
    return userEventLogMapper.toResponseDto(saved);
  }

  /**
   * 사용자 행동 로그 검색 (필터 + 페이징)
   *
   * @param dto 검색 조건 + 페이징 정보
   * @return 로그 목록 + 페이징 DTO
   */
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

    // 날짜 파싱
    LocalDateTime startAt = null;
    LocalDateTime endAt = null;
    if (dto.getStartDate() != null && !dto.getStartDate().isBlank()) {
      LocalDate startDate = LocalDate.parse(dto.getStartDate());
      startAt = startDate.atStartOfDay();
    }
    if (dto.getEndDate() != null && !dto.getEndDate().isBlank()) {
      LocalDate endDate = LocalDate.parse(dto.getEndDate());
      // 하루 끝까지 포함 (23:59:59)
      endAt = endDate.atTime(23, 59, 59);
    }

    // 날짜 범위 유효성 체크
    if (startAt != null && endAt != null && startAt.isAfter(endAt)) {
      throw new CustomException(AnalyticsErrorCode.INVALID_DATE_RANGE);
    }

    // 페이징: 1부터 시작 → PageRequest는 0부터 시작
    int page = (dto.getPage() != null && dto.getPage() > 0) ? dto.getPage() - 1 : 0;
    int size = (dto.getSize() != null && dto.getSize() > 0) ? dto.getSize() : 20;

    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

    // 검색
    var pageResult = userEventLogRepository.searchLogs(
        userId, eventType, targetType, targetId, startAt, endAt, pageable
    );

    return userEventLogMapper.toListResponseDto(pageResult);
  }

  // =========================================================
  // 내부 유틸 메서드
  // =========================================================

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

  /**
   * 가중치 결정 로직
   * - DTO에서 명시한 weight가 있으면 우선 사용
   * - 없으면 DEFAULT_WEIGHTS에서 조회
   * - 그래도 없으면 1로 기본값
   */
  private int determineWeight(UserEventLog.EventType eventType, Integer requestedWeight) {
    if (requestedWeight != null) {
      return requestedWeight;
    }
    return DEFAULT_WEIGHTS.getOrDefault(eventType, 1);
  }
}
