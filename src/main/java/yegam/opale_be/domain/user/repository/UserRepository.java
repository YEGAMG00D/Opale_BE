package yegam.opale_be.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import yegam.opale_be.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  /** 이메일로 사용자 조회 */
  Optional<User> findByEmail(String email);

  /** 이메일 중복 검사 */
  boolean existsByEmail(String email);

  /** 닉네임 중복 검사 */
  boolean existsByNickname(String nickname);

  /** 탈퇴하지 않은 사용자만 조회 */
  @Query("SELECT u FROM User u WHERE u.isDeleted = false")
  List<User> findAllActiveUsers();

  /** 전체 회원 목록 (관리자용) */
  @Query("SELECT u FROM User u ORDER BY u.createdAt DESC")
  List<User> findAllUsersOrderByCreatedAt();
}
