package yegam.opale_be.domain.review.performance.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yegam.opale_be.domain.culture.performance.entity.Performance;
import yegam.opale_be.domain.culture.performance.repository.PerformanceRepository;
import yegam.opale_be.domain.favorite.review.repository.FavoritePerformanceReviewRepository;
import yegam.opale_be.domain.reservation.entity.UserTicketVerification;
import yegam.opale_be.domain.reservation.repository.UserTicketVerificationRepository;
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
  private final UserTicketVerificationRepository ticketRepository;
  private final PerformanceReviewMapper reviewMapper;
  private final FavoritePerformanceReviewRepository favoritePerformanceReviewRepository;

  /** 단건 조회 */
  @Transactional(readOnly = true)
  public PerformanceReviewResponseDto getReview(Long reviewId) {

    PerformanceReview review = reviewRepository
        .findByPerformanceReviewIdAndIsDeletedFalse(reviewId)
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

  /** 본인 리뷰 목록 조회 */
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

  /** 특정 회원 리뷰 목록 조회(비로그인 가능) */
  @Transactional(readOnly = true)
  public PerformanceReviewListResponseDto getReviewsByUserPublic(Long userId, ReviewType reviewType) {
    return getReviewsByUser(userId, reviewType);
  }

  /** 리뷰 작성 */
  public PerformanceReviewResponseDto createReview(Long userId, PerformanceReviewRequestDto dto) {

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(PerformanceReviewErrorCode.REVIEW_ACCESS_DENIED));

    Performance performance = performanceRepository.findById(dto.getPerformanceId())
        .orElseThrow(() -> new CustomException(PerformanceReviewErrorCode.PERFORMANCE_NOT_FOUND));

    ReviewType type = dto.getReviewType();
    UserTicketVerification ticket = null;

    if (type == ReviewType.EXPECTATION) {
      ticket = null;
    }

    else if (type == ReviewType.AFTER || type == ReviewType.PLACE) {

      ticket = ticketRepository
          .findTop1ByUser_UserIdAndPerformance_PerformanceIdAndIsDeletedFalseOrderByRequestedAtDesc(
              userId,
              dto.getPerformanceId()
          )
          .orElseThrow(() -> new CustomException(PerformanceReviewErrorCode.TICKET_REQUIRED));

      reviewRepository.findByTicket_TicketId(ticket.getTicketId())
          .ifPresent(r -> {
            throw new CustomException(PerformanceReviewErrorCode.ALREADY_REVIEWED);
          });
    }

    PerformanceReview review = PerformanceReview.builder()
        .user(user)
        .performance(performance)
        .ticket(ticket)
        .title(dto.getTitle())
        .contents(dto.getContents())
        .rating(dto.getRating())
        .reviewType(type)
        .isDeleted(false)
        .build();

    reviewRepository.save(review);

    updatePerformanceAverageRating(performance.getPerformanceId());

    return reviewMapper.toResponseDto(review);
  }

  /** 리뷰 수정 */
  public PerformanceReviewResponseDto updateReview(Long userId, Long reviewId, PerformanceReviewRequestDto dto) {

    PerformanceReview review = reviewRepository
        .findByPerformanceReviewIdAndIsDeletedFalse(reviewId)
        .orElseThrow(() -> new CustomException(PerformanceReviewErrorCode.REVIEW_NOT_FOUND));

    if (!review.getUser().getUserId().equals(userId)) {
      throw new CustomException(PerformanceReviewErrorCode.REVIEW_ACCESS_DENIED);
    }

    ReviewType type = dto.getReviewType();

    if (type == ReviewType.EXPECTATION) {
      review.setTicket(null);
    }

    else {
      UserTicketVerification ticket =
          ticketRepository.findTop1ByUser_UserIdAndPerformance_PerformanceIdAndIsDeletedFalseOrderByRequestedAtDesc(
                  userId,
                  review.getPerformance().getPerformanceId()
              )
              .orElseThrow(() -> new CustomException(PerformanceReviewErrorCode.TICKET_REQUIRED));

      review.setTicket(ticket);
    }

    review.setTitle(dto.getTitle());
    review.setContents(dto.getContents());
    review.setRating(dto.getRating());
    review.setReviewType(type);

    updatePerformanceAverageRating(review.getPerformance().getPerformanceId());

    return reviewMapper.toResponseDto(review);
  }

  /** 리뷰 삭제 */
  public void deleteReview(Long userId, Long reviewId) {

    PerformanceReview review = reviewRepository
        .findByPerformanceReviewIdAndIsDeletedFalse(reviewId)
        .orElseThrow(() -> new CustomException(PerformanceReviewErrorCode.REVIEW_NOT_FOUND));

    if (!review.getUser().getUserId().equals(userId)) {
      throw new CustomException(PerformanceReviewErrorCode.REVIEW_ACCESS_DENIED);
    }

    String performanceId = review.getPerformance().getPerformanceId();

    favoritePerformanceReviewRepository.softDeleteByReviewId(reviewId);

    review.setIsDeleted(true);
    review.setDeletedAt(LocalDateTime.now());

    updatePerformanceAverageRating(performanceId);
  }

  /** 평균 평점 갱신 */
  private void updatePerformanceAverageRating(String performanceId) {
    Double avg = reviewRepository.calculateAverageRating(performanceId);
    Performance performance = performanceRepository.findById(performanceId)
        .orElseThrow(() -> new CustomException(PerformanceReviewErrorCode.PERFORMANCE_NOT_FOUND));

    if (avg == null) avg = 0.0;
    performance.setRating(avg);
    performanceRepository.save(performance);
  }
}
