package yegam.opale_be.domain.user.mapper;

import org.springframework.stereotype.Component;
import yegam.opale_be.domain.user.dto.response.UserResponseDto;
import yegam.opale_be.domain.user.dto.response.UserListResponseDto;
import yegam.opale_be.domain.user.dto.response.CheckNicknameResponseDto;
import yegam.opale_be.domain.user.entity.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * UserMapper
 * - User 엔티티 ↔ DTO 간 변환 담당
 */
@Component
public class UserMapper {

  /** 단일 사용자 Entity → UserResponseDto 변환 */
  public UserResponseDto toUserResponseDto(User user) {
    if (user == null) return null;

    return UserResponseDto.builder()
        .userId(user.getUserId())
        .email(user.getEmail())
        .nickname(user.getNickname())
        .name(user.getName())
        .birth(user.getBirth())
        .phone(user.getPhone())
        .address1(user.getAddress1())
        .address2(user.getAddress2())
        .role(user.getRole().name())
        .createdAt(user.getCreatedAt())
        .build();
  }

  /** 관리자용: 전체 사용자 목록 Entity List → UserListResponseDto List 변환 */
  public List<UserListResponseDto> toUserListResponseDto(List<User> users) {
    return users.stream()
        .map(user -> UserListResponseDto.builder()
            .userId(user.getUserId())
            .email(user.getEmail())
            .name(user.getName())
            .nickname(user.getNickname())
            .birth(user.getBirth())
            .role(user.getRole().name())
            .isDeleted(user.getIsDeleted())
            .createdAt(user.getCreatedAt())
            .build())
        .collect(Collectors.toList());
  }

  /** 닉네임 중복 확인 결과 → CheckNicknameResponseDto 변환 */
  public CheckNicknameResponseDto toCheckNicknameResponseDto(String nickname, boolean exists) {
    return CheckNicknameResponseDto.builder()
        .nickname(nickname)
        .available(!exists)
        .build();
  }
}
