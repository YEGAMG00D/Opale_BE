package yegam.opale_be.domain.reservation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yegam.opale_be.domain.reservation.entity.UserTicketVerification;

import java.util.Optional;

@Repository
public interface UserTicketVerificationRepository extends JpaRepository<UserTicketVerification, Long> {

  Optional<UserTicketVerification> findByTicketIdAndUser_UserId(Long ticketId, Long userId);

  Page<UserTicketVerification> findAllByUser_UserIdOrderByRequestedAtDesc(Long userId, Pageable pageable);
}
