package yegam.opale_be.domain.user.service;

import java.util.List;
import java.util.stream.Collectors;
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

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  /* 전체 회원 목록 조회 (삭제된 회원 포함) */
  public List<UserResponseDto> getAllUsers() {
    List<User> userList = userRepository.findAll();

    if (userList.isEmpty()) {
      log.warn("현재 등록된 회원이 없습니다.");
    } else {
      log.info("전체 회원 조회 성공: {}명", userList.size());
    }

    return userList.stream()
        .map(userMapper::toUserResponseDto)
        .collect(Collectors.toList());
  }

  /* 특정 회원 상세 조회 */
  public UserResponseDto getUserById(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    String status = user.getIsDeleted() ? "탈퇴 회원" : "활성 회원";
    log.info("관리자 회원 상세 조회 [{}]: email={}, status={}",
        user.getId(), user.getEmail(), status);

    return userMapper.toUserResponseDto(user);
  }


}
