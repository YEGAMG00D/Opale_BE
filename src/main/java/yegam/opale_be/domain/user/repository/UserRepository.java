package yegam.opale_be.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import yegam.opale_be.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);

  boolean existsByNickname(String nickname);

  /** 전체 회원 목록 (ADMIN 포함) */
  @Query("SELECT u FROM User u ORDER BY u.createdAt DESC")
  List<User> findAllUsersOrderByCreatedAt();

  /** ADMIN 제외 전체 회원 목록 */
  @Query("SELECT u FROM User u WHERE u.role <> 'ADMIN' ORDER BY u.createdAt DESC")
  List<User> findAllUsersExceptAdminOrderByCreatedAt();

  /** 탈퇴하지 않은 사용자만 조회 */
  @Query("SELECT u FROM User u WHERE u.isDeleted = false")
  List<User> findAllActiveUsers();
}
