package yegam.opale_be.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yegam.opale_be.domain.user.dto.response.UserResponseDto;
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

  /** 전체 회원 목록 조회 (삭제 포함) */
  public List<UserResponseDto> getAllUsers() {
    List<User> users = userRepository.findAllUsersOrderByCreatedAt();
    log.info("관리자 전체 회원 조회: {}명", users.size());

    return users.stream()
        .map(userMapper::toUserResponseDto)
        .toList();
  }

  /** 특정 회원 상세 조회 */
  public UserResponseDto getUserById(Long userId) {
    return userRepository.findById(userId)
        .map(user -> {
          log.info("관리자 회원 상세 조회: id={}, email={}, status={}",
              user.getUserId(), user.getEmail(), user.getIsDeleted() ? "탈퇴" : "활성");
          return userMapper.toUserResponseDto(user);
        })
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
  }
}
