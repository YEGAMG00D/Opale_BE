package yegam.opale_be.domain.review.performance.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yegam.opale_be.domain.culture.performance.entity.Performance;
import yegam.opale_be.domain.culture.performance.repository.PerformanceRepository;
import yegam.opale_be.domain.review.common.ReviewType;
import yegam.opale_be.domain.review.performance.dto.request.PerformanceReviewRequestDto;
import yegam.opale_be.domain.review.performance.dto.response.PerformanceReviewListResponseDto;
import yegam.opale_be.domain.review.performance.dto.response.PerformanceReviewResponseDto;
import yegam.opale_be.domain.review.performance.entity.PerformanceReview;
import yegam.opale_be.domain.review.performance.exception.PerformanceReviewErrorCode;
import yegam.opale_be.domain.review.performance.mapper.PerformanceReviewMapper;
import yegam.opale_be.domain.review.performance.repository.PerformanceReviewRepository;
import yegam.opale_be.domain.user.entity.User;
import yegam.opale_be.domain.user.repository.UserRepository;
import yegam.opale_be.global.exception.CustomException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PerformanceReviewService {

  private final PerformanceReviewRepository reviewRepository;
  private final PerformanceRepository performanceRepository;
  private final UserRepository userRepository;
  private final PerformanceReviewMapper reviewMapper;

  /** 단일 공연 리뷰 조회 */
  @Transactional(readOnly = true)
  public PerformanceReviewResponseDto getReview(Long reviewId) {
    PerformanceReview review = reviewRepository.findById(reviewId)
        .filter(r -> !r.getIsDeleted())
        .orElseThrow(() -> new CustomException(PerformanceReviewErrorCode.REVIEW_NOT_FOUND));
    return reviewMapper.toResponseDto(review);
  }

  /** 공연별 리뷰 목록 조회 */
  @Transactional(readOnly = true)
  public PerformanceReviewListResponseDto getReviewsByPerformance(String performanceId, ReviewType reviewType) {
    List<PerformanceReview> reviews = (reviewType != null)
        ? reviewRepository.findAllByPerformanceIdAndType(performanceId, reviewType)
        : reviewRepository.findAllByPerformanceId(performanceId);

    return PerformanceReviewListResponseDto.builder()
        .totalCount(reviews.size())
        .currentPage(1)
        .pageSize(reviews.size())
        .totalPages(1)
        .hasNext(false)
        .hasPrev(false)
        .reviews(reviewMapper.toResponseDtoList(reviews))
        .build();
  }

  /** 작성한 본인 리뷰 목록 조회 */
  @Transactional(readOnly = true)
  public PerformanceReviewListResponseDto getReviewsByUser(Long userId, ReviewType reviewType) {
    List<PerformanceReview> reviews = (reviewType != null)
        ? reviewRepository.findAllByUserIdAndType(userId, reviewType)
        : reviewRepository.findAllByUserId(userId);

    return PerformanceReviewListResponseDto.builder()
        .totalCount(reviews.size())
        .currentPage(1)
        .pageSize(reviews.size())
        .totalPages(1)
        .hasNext(false)
        .hasPrev(false)
        .reviews(reviewMapper.toResponseDtoList(reviews))
        .build();
  }

  /** 특정 회원의 공연 리뷰 목록 조회 (비로그인 가능) */
  @Transactional(readOnly = true)
  public PerformanceReviewListResponseDto getReviewsByUserPublic(Long userId, ReviewType reviewType) {
    List<PerformanceReview> reviews = (reviewType != null)
        ? reviewRepository.findAllByUserIdAndType(userId, reviewType)
        : reviewRepository.findAllByUserId(userId);

    return PerformanceReviewListResponseDto.builder()
        .totalCount(reviews.size())
        .currentPage(1)
        .pageSize(reviews.size())
        .totalPages(1)
        .hasNext(false)
        .hasPrev(false)
        .reviews(reviewMapper.toResponseDtoList(reviews))
        .build();
  }

  /** 리뷰 작성 */
  public PerformanceReviewResponseDto createReview(Long userId, PerformanceReviewRequestDto dto) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(PerformanceReviewErrorCode.REVIEW_ACCESS_DENIED));
    Performance performance = performanceRepository.findById(dto.getPerformanceId())
        .orElseThrow(() -> new CustomException(PerformanceReviewErrorCode.PERFORMANCE_NOT_FOUND));

    PerformanceReview review = PerformanceReview.builder()
        .user(user)
        .performance(performance)
        .title(dto.getTitle())
        .contents(dto.getContents())
        .rating(dto.getRating())
        .reviewType(dto.getReviewType())
        .isDeleted(false)
        .build();

    reviewRepository.save(review);

    // ✅ 공연 평균 평점 갱신
    updatePerformanceAverageRating(performance.getPerformanceId());

    log.info("공연 리뷰 작성 완료: userId={}, performanceId={}", userId, performance.getPerformanceId());
    return reviewMapper.toResponseDto(review);
  }

  /** 리뷰 수정 */
  public PerformanceReviewResponseDto updateReview(Long userId, Long reviewId, PerformanceReviewRequestDto dto) {
    PerformanceReview review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new CustomException(PerformanceReviewErrorCode.REVIEW_NOT_FOUND));

    if (!review.getUser().getUserId().equals(userId)) {
      throw new CustomException(PerformanceReviewErrorCode.REVIEW_ACCESS_DENIED);
    }

    review.setTitle(dto.getTitle());
    review.setContents(dto.getContents());
    review.setRating(dto.getRating());
    review.setReviewType(dto.getReviewType());

    // ✅ 공연 평균 평점 갱신
    updatePerformanceAverageRating(review.getPerformance().getPerformanceId());

    return reviewMapper.toResponseDto(review);
  }

  /** 리뷰 삭제 */
  public void deleteReview(Long userId, Long reviewId) {
    PerformanceReview review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new CustomException(PerformanceReviewErrorCode.REVIEW_NOT_FOUND));

    if (!review.getUser().getUserId().equals(userId)) {
      throw new CustomException(PerformanceReviewErrorCode.REVIEW_ACCESS_DENIED);
    }

    review.setIsDeleted(true);
    review.setDeletedAt(LocalDateTime.now());

    // ✅ 공연 평균 평점 갱신
    updatePerformanceAverageRating(review.getPerformance().getPerformanceId());

    log.info("공연 리뷰 삭제 완료: reviewId={}, userId={}", reviewId, userId);
  }

  /** ✅ 공연 평균 평점 갱신 로직 */
  private void updatePerformanceAverageRating(String performanceId) {
    Double avg = reviewRepository.calculateAverageRating(performanceId);
    Performance performance = performanceRepository.findById(performanceId)
        .orElseThrow(() -> new CustomException(PerformanceReviewErrorCode.PERFORMANCE_NOT_FOUND));

    if (avg == null) avg = 0.0; // 리뷰가 없을 때 0 처리
    log.info("🎭 공연 평균 평점 갱신: performanceId={}, newAvg={}", performanceId, avg);

    // ★ 엔티티에 rating 필드가 있다면 아래 코드 활성화:
    // performance.setRating(avg);
    // performanceRepository.save(performance);
  }
}
