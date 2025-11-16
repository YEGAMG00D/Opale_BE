package yegam.opale_be.domain.preference.mapper;

import org.springframework.stereotype.Component;
import yegam.opale_be.domain.preference.dto.request.UserPreferenceVectorRequestDto;
import yegam.opale_be.domain.preference.dto.response.UserPreferenceVectorResponseDto;
import yegam.opale_be.domain.preference.entity.UserPreferenceVector;
import yegam.opale_be.domain.user.entity.User;

import java.time.format.DateTimeFormatter;

/**
 * UserPreferenceVector Mapper
 * - Entity <-> DTO 변환 전담
 */
@Component
public class UserPreferenceVectorMapper {

  private static final DateTimeFormatter TIME_FORMATTER =
      DateTimeFormatter.ISO_LOCAL_DATE_TIME;

  public UserPreferenceVector toEntity(User user, UserPreferenceVectorRequestDto dto) {
    return UserPreferenceVector.builder()
        .user(user)
        .userId(user.getUserId())
        .embeddingVector(dto.getEmbeddingVector())
        .build();
  }

  public void updateEntity(UserPreferenceVector entity, UserPreferenceVectorRequestDto dto) {
    entity.setEmbeddingVector(dto.getEmbeddingVector());
  }

  public UserPreferenceVectorResponseDto toResponseDto(UserPreferenceVector entity) {
    return UserPreferenceVectorResponseDto.builder()
        .userId(entity.getUserId())
        .embeddingVector(entity.getEmbeddingVector())
        .updatedAt(entity.getUpdatedAt() != null
            ? entity.getUpdatedAt().format(TIME_FORMATTER)
            : null)
        .build();
  }
}
