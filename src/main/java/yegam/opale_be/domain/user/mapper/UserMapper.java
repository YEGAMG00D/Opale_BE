package yegam.opale_be.domain.user.mapper;

import org.springframework.stereotype.Component;
import yegam.opale_be.domain.user.dto.response.UserResponseDto;
import yegam.opale_be.domain.user.entity.User;

@Component
public class UserMapper {

  public UserResponseDto toUserResponseDto(User user) {
    if (user == null) {
      return null;
    }

    return UserResponseDto.builder()
        .id(user.getId())
        .email(user.getEmail())
        .nickname(user.getNickname())
        .name(user.getName())
        .birth(user.getBirth())
        .build();
  }


}
