package yegam.opale_be.domain.review.performance.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yegam.opale_be.domain.culture.performance.entity.Performance;
import yegam.opale_be.domain.culture.performance.repository.PerformanceRepository;
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

  /** 단건 조회 */
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

    // ✅ EXPECTATION(기대평) → 티켓 없어도 됨 (DB에도 NULL 저장)
    if (type == ReviewType.EXPECTATION) {
      ticket = null;
    }

    // ✅ AFTER(후기) 또는 PLACE → 반드시 티켓 필요
    else if (type == ReviewType.AFTER || type == ReviewType.PLACE) {

      if (dto.getTicketId() == null) {
        throw new CustomException(PerformanceReviewErrorCode.TICKET_REQUIRED);
      }

      // ✅ 티켓 조회
      ticket = ticketRepository.findById(dto.getTicketId())
          .orElseThrow(() -> new CustomException(PerformanceReviewErrorCode.TICKET_REQUIRED));

      // ✅ 티켓 소유자 확인
      if (!ticket.getUser().getUserId().equals(userId)) {
        throw new CustomException(PerformanceReviewErrorCode.REVIEW_ACCESS_DENIED);
      }

      // ✅ 티켓의 공연과 리뷰 공연이 일치하는지 확인
      if (!ticket.getPerformance().getPerformanceId().equals(dto.getPerformanceId())) {
        throw new CustomException(PerformanceReviewErrorCode.TICKET_REQUIRED);
      }

      // ✅ 같은 티켓으로 후기 중복 작성 방지
      reviewRepository.findByTicket_TicketId(ticket.getTicketId())
          .ifPresent(r -> {
            throw new CustomException(PerformanceReviewErrorCode.ALREADY_REVIEWED);
          });
    }

    PerformanceReview review = PerformanceReview.builder()
        .user(user)
        .performance(performance)
        .ticket(ticket) // ✅ EXPECTATION이면 NULL, AFTER면 반드시 값 존재
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

    PerformanceReview review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new CustomException(PerformanceReviewErrorCode.REVIEW_NOT_FOUND));

    if (!review.getUser().getUserId().equals(userId)) {
      throw new CustomException(PerformanceReviewErrorCode.REVIEW_ACCESS_DENIED);
    }

    ReviewType type = dto.getReviewType();

    // ✅ EXPECTATION → 티켓 강제 NULL
    if (type == ReviewType.EXPECTATION) {
      review.setTicket(null);
    }

    // ✅ AFTER + PLACE → 티켓 필수 + 소유자 + 공연 매칭 검증
    else {

      if (dto.getTicketId() == null) {
        throw new CustomException(PerformanceReviewErrorCode.TICKET_REQUIRED);
      }

      UserTicketVerification ticket = ticketRepository.findById(dto.getTicketId())
          .orElseThrow(() -> new CustomException(PerformanceReviewErrorCode.TICKET_REQUIRED));

      // ✅ 티켓 소유자 확인
      if (!ticket.getUser().getUserId().equals(userId)) {
        throw new CustomException(PerformanceReviewErrorCode.REVIEW_ACCESS_DENIED);
      }

      // ✅ 수정 시에도 공연 일치 검증
      if (!ticket.getPerformance().getPerformanceId()
          .equals(review.getPerformance().getPerformanceId())) {
        throw new CustomException(PerformanceReviewErrorCode.TICKET_REQUIRED);
      }

      review.setTicket(ticket);
    }

    review.setTitle(dto.getTitle());
    review.setContents(dto.getContents());
    review.setRating(dto.getRating());
    review.setReviewType(type);

    updatePerformanceAverageRating(review.getPerformance().getPerformanceId());

    return reviewMapper.toResponseDto(review);
  }



  /** ✅ ✅ ✅ 리뷰 삭제 (물리 삭제로 변경) */
  public void deleteReview(Long userId, Long reviewId) {

    PerformanceReview review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new CustomException(PerformanceReviewErrorCode.REVIEW_NOT_FOUND));

    if (!review.getUser().getUserId().equals(userId)) {
      throw new CustomException(PerformanceReviewErrorCode.REVIEW_ACCESS_DENIED);
    }

    String performanceId = review.getPerformance().getPerformanceId();

    reviewRepository.delete(review);   // ✅ 물리 삭제

    updatePerformanceAverageRating(performanceId);
  }

  /** 평균 갱신 */
  private void updatePerformanceAverageRating(String performanceId) {
    Double avg = reviewRepository.calculateAverageRating(performanceId);
    Performance performance = performanceRepository.findById(performanceId)
        .orElseThrow(() -> new CustomException(PerformanceReviewErrorCode.PERFORMANCE_NOT_FOUND));

    if (avg == null) avg = 0.0;
    performance.setRating(avg);
    performanceRepository.save(performance);
  }
}
