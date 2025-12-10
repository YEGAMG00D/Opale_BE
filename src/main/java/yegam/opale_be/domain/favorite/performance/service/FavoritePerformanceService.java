package yegam.opale_be.domain.favorite.performance.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yegam.opale_be.domain.culture.performance.entity.Performance;
import yegam.opale_be.domain.culture.performance.exception.PerformanceErrorCode;
import yegam.opale_be.domain.culture.performance.repository.PerformanceRepository;
import yegam.opale_be.domain.favorite.performance.dto.response.FavoritePerformanceResponseDto;
import yegam.opale_be.domain.favorite.performance.entity.FavoritePerformance;
import yegam.opale_be.domain.favorite.performance.mapper.FavoritePerformanceMapper;
import yegam.opale_be.domain.favorite.performance.repository.FavoritePerformanceRepository;
import yegam.opale_be.domain.user.entity.User;
import yegam.opale_be.domain.user.exception.UserErrorCode;
import yegam.opale_be.domain.user.repository.UserRepository;
import yegam.opale_be.global.exception.CustomException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FavoritePerformanceService {

  private final FavoritePerformanceRepository favoritePerformanceRepository;
  private final PerformanceRepository performanceRepository;
  private final UserRepository userRepository;
  private final FavoritePerformanceMapper favoritePerformanceMapper;

  /** ⭐ 공연 관심 토글 (soft delete 사용 안 함) */
  public boolean toggleFavorite(Long userId, String performanceId) {

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    Performance performance = performanceRepository.findById(performanceId)
        .orElseThrow(() -> new CustomException(PerformanceErrorCode.PERFORMANCE_NOT_FOUND));

    FavoritePerformance favorite = favoritePerformanceRepository
        .findByUser_UserIdAndPerformance_PerformanceId(userId, performanceId)
        .orElse(null);

    // 신규 생성
    if (favorite == null) {
      favoritePerformanceRepository.save(
          FavoritePerformance.builder()
              .user(user)
              .performance(performance)
              .isLiked(true)
              .isDeleted(false)
              .build()
      );
      log.info("💖 공연 관심 등록 userId={}, performanceId={}", userId, performanceId);
      return true;
    }

    // soft delete 된 데이터 → 복구 후 좋아요 ON
    if (favorite.getIsDeleted()) {
      favorite.setIsDeleted(false);
      favorite.setDeletedAt(null);
      favorite.setIsLiked(true);
      log.info("♻️ 공연 관심 soft-delete 복구 userId={}, performanceId={}", userId, performanceId);
      return true;
    }

    // 일반 토글
    boolean newState = !favorite.getIsLiked();
    favorite.setIsLiked(newState);
    log.info("🔁 공연 관심 토글 userId={}, performanceId={}, now={}", userId, performanceId, newState);

    return newState;
  }

  /** 단건 조회 */
  @Transactional(readOnly = true)
  public boolean isLiked(Long userId, String performanceId) {
    if (userId == null) return false;

    return favoritePerformanceRepository
        .existsByUser_UserIdAndPerformance_PerformanceIdAndIsLikedTrue(userId, performanceId);
  }

  /** performanceId 목록 */
  @Transactional(readOnly = true)
  public List<String> getFavoritePerformanceIds(Long userId) {
    if (userId == null) return List.of();
    return favoritePerformanceRepository.findLikedPerformanceIdsByUserId(userId);
  }

  /** 마이페이지 목록 */
  @Transactional(readOnly = true)
  public List<FavoritePerformanceResponseDto> getFavoritePerformances(Long userId) {

    userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    List<Performance> likedPerformances =
        favoritePerformanceRepository.findLikedPerformancesByUserId(userId);

    if (likedPerformances.isEmpty()) return List.of();

    return favoritePerformanceMapper.toResponseDtoList(likedPerformances);
  }
}
