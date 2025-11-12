package yegam.opale_be.domain.review.performance.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yegam.opale_be.domain.culture.performance.entity.Performance;
import yegam.opale_be.domain.culture.performance.repository.PerformanceRepository;
import yegam.opale_be.domain.review.common.ReviewType;
import yegam.opale_be.domain.review.performance.dto.request.PerformanceReviewRequestDto;
import yegam.opale_be.domain.review.performance.dto.response.PerformanceReviewResponseDto;
import yegam.opale_be.domain.review.performance.dto.response.PerformanceReviewListResponseDto;
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

  /** 공연별 리뷰 목록 조회 (비로그인 가능) */
  @Transactional(readOnly = true)
  public PerformanceReviewListResponseDto getReviewsByPerformance(String performanceId) {
    List<PerformanceReview> reviews = reviewRepository.findAllByPerformanceId(performanceId);
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

  /** 작성한 본인 리뷰 목록 조회 (타입 필터 포함) */
  @Transactional(readOnly = true)
  public PerformanceReviewListResponseDto getReviewsByUser(Long userId, ReviewType reviewType) {
    List<PerformanceReview> reviews;

    if (reviewType != null) {
      reviews = reviewRepository.findAllByUserIdAndType(userId, reviewType);
    } else {
      reviews = reviewRepository.findAllByUserId(userId);
    }

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
    List<PerformanceReview> reviews;

    if (reviewType != null) {
      reviews = reviewRepository.findAllByUserIdAndType(userId, reviewType);
    } else {
      reviews = reviewRepository.findAllByUserId(userId);
    }

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
    log.info("공연 리뷰 작성: userId={}, performanceId={}", userId, performance.getPerformanceId());
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

    return reviewMapper.toResponseDto(review);
  }

  /** 리뷰 삭제 (Soft Delete) */
  public void deleteReview(Long userId, Long reviewId) {
    PerformanceReview review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new CustomException(PerformanceReviewErrorCode.REVIEW_NOT_FOUND));

    if (!review.getUser().getUserId().equals(userId)) {
      throw new CustomException(PerformanceReviewErrorCode.REVIEW_ACCESS_DENIED);
    }

    review.setIsDeleted(true);
    review.setDeletedAt(LocalDateTime.now());
    log.info("공연 리뷰 삭제: reviewId={}, userId={}", reviewId, userId);
  }
}
