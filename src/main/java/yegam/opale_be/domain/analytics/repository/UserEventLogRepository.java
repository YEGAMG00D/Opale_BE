package yegam.opale_be.domain.analytics.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yegam.opale_be.domain.analytics.entity.UserEventLog;

import java.time.LocalDateTime;

@Repository
public interface UserEventLogRepository extends JpaRepository<UserEventLog, Long> {

  /**
   * 사용자 행동 로그 검색 (필터 조건 모두 Optional)
   *
   * - userId, eventType, targetType, targetId, 날짜 범위
   * - createdAt DESC 정렬은 Service에서 Pageable에 설정
   */
  @Query("""
      SELECT l
      FROM UserEventLog l
      WHERE (:userId IS NULL OR l.user.userId = :userId)
        AND (:eventType IS NULL OR l.eventType = :eventType)
        AND (:targetType IS NULL OR l.targetType = :targetType)
        AND (:targetId IS NULL OR l.targetId = :targetId)
        AND (:startAt IS NULL OR l.createdAt >= :startAt)
        AND (:endAt IS NULL OR l.createdAt <= :endAt)
      """)
  Page<UserEventLog> searchLogs(
      @Param("userId") Long userId,
      @Param("eventType") UserEventLog.EventType eventType,
      @Param("targetType") UserEventLog.TargetType targetType,
      @Param("targetId") String targetId,
      @Param("startAt") LocalDateTime startAt,
      @Param("endAt") LocalDateTime endAt,
      Pageable pageable
  );
}
