package yegam.opale_be.domain.reservation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yegam.opale_be.domain.reservation.entity.UserTicketVerification;

import java.util.Optional;

@Repository
public interface UserTicketVerificationRepository extends JpaRepository<UserTicketVerification, Long> {

  /** 1) 단일 조회 (Service가 그대로 호출하도록 유지) */
  Optional<UserTicketVerification> findByTicketIdAndUser_UserId(Long ticketId, Long userId);

  /** 2) Soft Delete 적용 단일 조회 */
  Optional<UserTicketVerification> findByTicketIdAndUser_UserIdAndIsDeletedFalse(Long ticketId, Long userId);

  /** 3) Soft Delete 제외 + 최신순 목록 조회 */
  Page<UserTicketVerification> findByUser_UserIdAndIsDeletedFalseOrderByRequestedAtDesc(
      Long userId, Pageable pageable);

  /** 4) 서비스에서 사용하는 alias 메서드 */
  default Page<UserTicketVerification> findAllActiveByUser(Long userId, Pageable pageable) {
    return findByUser_UserIdAndIsDeletedFalseOrderByRequestedAtDesc(userId, pageable);
  }

  /** 5) 공연 리뷰 작성 가능 티켓 (Soft Delete 제외 + 최신순 1개) */
  Optional<UserTicketVerification>
  findTop1ByUser_UserIdAndPerformance_PerformanceIdAndIsDeletedFalseOrderByRequestedAtDesc(
      Long userId,
      String performanceId
  );

  /** 6) 공연장 리뷰 작성 가능 티켓 (Soft Delete 제외 + 최신순 1개) */
  Optional<UserTicketVerification>
  findTop1ByUser_UserIdAndPlace_PlaceIdAndIsDeletedFalseOrderByRequestedAtDesc(
      Long userId,
      String placeId
  );
}
