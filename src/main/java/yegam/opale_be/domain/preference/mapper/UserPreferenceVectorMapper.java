package yegam.opale_be.domain.preference.mapper;

import org.springframework.stereotype.Component;
import yegam.opale_be.domain.preference.dto.request.UserPreferenceVectorRequestDto;
import yegam.opale_be.domain.preference.dto.response.UserPreferenceVectorResponseDto;
import yegam.opale_be.domain.preference.entity.UserPreferenceVector;
import yegam.opale_be.domain.user.entity.User;

import java.time.format.DateTimeFormatter;

@Component
public class UserPreferenceVectorMapper {

  private static final DateTimeFormatter TIME_FORMATTER =
      DateTimeFormatter.ISO_LOCAL_DATE_TIME;

  /** 생성용 */
  public UserPreferenceVector toEntity(User user, UserPreferenceVectorRequestDto dto) {
    return UserPreferenceVector.builder()
        .user(user)                        // MapsId → PK 자동매핑
        .embeddingVector(dto.getEmbeddingVector())
        .build();
  }

  /** 수정용 */
  public void updateEntity(UserPreferenceVector entity, UserPreferenceVectorRequestDto dto) {
    entity.setEmbeddingVector(dto.getEmbeddingVector());
  }

  /** 응답 DTO */
  public UserPreferenceVectorResponseDto toResponseDto(UserPreferenceVector entity) {
    return UserPreferenceVectorResponseDto.builder()
        .userPreferenceVectorId(entity.getUserPreferenceVectorId())
        .embeddingVector(entity.getEmbeddingVector())
        .updatedAt(entity.getUpdatedAt() != null
            ? entity.getUpdatedAt().format(TIME_FORMATTER)
            : null)
        .build();
  }
}
