package yegam.opale_be.domain.email.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yegam.opale_be.domain.email.entity.VerificationCode;

import java.util.Optional;

@Repository
public interface EmailRepository extends JpaRepository<VerificationCode, Long> {
  Optional<VerificationCode> findByEmail(String email);
  void deleteByEmail(String email);
}
