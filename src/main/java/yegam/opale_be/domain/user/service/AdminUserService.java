package yegam.opale_be.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yegam.opale_be.domain.user.dto.response.AdminUserListResponseDto;
import yegam.opale_be.domain.user.dto.response.AdminUserResponseDto;
import yegam.opale_be.domain.user.entity.User;
import yegam.opale_be.domain.user.exception.UserErrorCode;
import yegam.opale_be.domain.user.mapper.UserMapper;
import yegam.opale_be.domain.user.repository.UserRepository;
import yegam.opale_be.global.exception.CustomException;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  /** 전체 회원 목록 조회 (탈퇴 포함) */
  public AdminUserListResponseDto getAllUsers() {
    List<User> users = userRepository.findAllUsersOrderByCreatedAt();
    long totalCount = users.size();

    log.info("관리자 전체 회원 조회: 총 {}명", totalCount);

    return AdminUserListResponseDto.builder()
        .totalCount(totalCount)
        .users(userMapper.toAdminUserResponseDtoList(users))
        .build();
  }

  /** 특정 회원 상세 조회 */
  public AdminUserResponseDto getUserById(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    log.info("관리자 회원 상세 조회: id={}, email={}, status={}",
        user.getUserId(), user.getEmail(), user.getIsDeleted() ? "탈퇴" : "활성");

    return userMapper.toAdminUserResponseDto(user);
  }
}
