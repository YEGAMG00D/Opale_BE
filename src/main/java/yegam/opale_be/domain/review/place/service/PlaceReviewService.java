package yegam.opale_be.domain.review.place.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yegam.opale_be.domain.place.entity.Place;
import yegam.opale_be.domain.place.repository.PlaceRepository;
import yegam.opale_be.domain.reservation.entity.UserTicketVerification;
import yegam.opale_be.domain.reservation.repository.UserTicketVerificationRepository;
import yegam.opale_be.domain.review.common.ReviewType;
import yegam.opale_be.domain.review.place.dto.request.PlaceReviewRequestDto;
import yegam.opale_be.domain.review.place.dto.response.PlaceReviewListResponseDto;
import yegam.opale_be.domain.review.place.dto.response.PlaceReviewResponseDto;
import yegam.opale_be.domain.review.place.entity.PlaceReview;
import yegam.opale_be.domain.review.place.exception.PlaceReviewErrorCode;
import yegam.opale_be.domain.review.place.mapper.PlaceReviewMapper;
import yegam.opale_be.domain.review.place.repository.PlaceReviewRepository;
import yegam.opale_be.domain.user.entity.User;
import yegam.opale_be.domain.user.repository.UserRepository;
import yegam.opale_be.global.exception.CustomException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PlaceReviewService {

  private final PlaceReviewRepository reviewRepository;
  private final PlaceRepository placeRepository;
  private final UserRepository userRepository;
  private final UserTicketVerificationRepository ticketRepository;
  private final PlaceReviewMapper reviewMapper;

  /** 단건 조회 */
  @Transactional(readOnly = true)
  public PlaceReviewResponseDto getReview(Long reviewId) {
    PlaceReview review = reviewRepository.findById(reviewId)
        .filter(r -> !r.getIsDeleted())
        .orElseThrow(() -> new CustomException(PlaceReviewErrorCode.REVIEW_NOT_FOUND));
    return reviewMapper.toResponseDto(review);
  }

  /** 공연장별 리뷰 목록 */
  @Transactional(readOnly = true)
  public PlaceReviewListResponseDto getReviewsByPlace(String placeId, ReviewType reviewType) {
    List<PlaceReview> reviews = (reviewType != null)
        ? reviewRepository.findAllByPlaceIdAndType(placeId, reviewType)
        : reviewRepository.findAllByPlaceId(placeId);

    return PlaceReviewListResponseDto.builder()
        .totalCount(reviews.size())
        .currentPage(1)
        .pageSize(reviews.size())
        .totalPages(1)
        .hasNext(false)
        .hasPrev(false)
        .reviews(reviewMapper.toResponseDtoList(reviews))
        .build();
  }

  /** 본인 리뷰 목록 */
  @Transactional(readOnly = true)
  public PlaceReviewListResponseDto getReviewsByUser(Long userId, ReviewType reviewType) {
    List<PlaceReview> reviews = (reviewType != null)
        ? reviewRepository.findAllByUserIdAndType(userId, reviewType)
        : reviewRepository.findAllByUserId(userId);

    return PlaceReviewListResponseDto.builder()
        .totalCount(reviews.size())
        .currentPage(1)
        .pageSize(reviews.size())
        .totalPages(1)
        .hasNext(false)
        .hasPrev(false)
        .reviews(reviewMapper.toResponseDtoList(reviews))
        .build();
  }

  /** 특정 회원 리뷰 목록 (비로그인 가능) */
  @Transactional(readOnly = true)
  public PlaceReviewListResponseDto getReviewsByUserPublic(Long userId, ReviewType reviewType) {
    return getReviewsByUser(userId, reviewType);
  }

  /** 리뷰 작성 */
  public PlaceReviewResponseDto createReview(Long userId, PlaceReviewRequestDto dto) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(PlaceReviewErrorCode.REVIEW_ACCESS_DENIED));

    Place place = placeRepository.findById(dto.getPlaceId())
        .orElseThrow(() -> new CustomException(PlaceReviewErrorCode.PLACE_NOT_FOUND));

    UserTicketVerification ticket = ticketRepository
        .findFirstByUser_UserIdAndPlace_PlaceId(userId, dto.getPlaceId())
        .orElseThrow(() -> new CustomException(PlaceReviewErrorCode.TICKET_REQUIRED));

    PlaceReview review = PlaceReview.builder()
        .user(user)
        .place(place)
        .ticket(ticket)
        .title(dto.getTitle())
        .contents(dto.getContents())
        .rating(dto.getRating())
        .reviewType(dto.getReviewType())
        .isDeleted(false)
        .build();

    reviewRepository.save(review);
    updatePlaceAverageRating(place.getPlaceId());

    return reviewMapper.toResponseDto(review);
  }

  /** 리뷰 수정 */
  public PlaceReviewResponseDto updateReview(Long userId, Long reviewId, PlaceReviewRequestDto dto) {

    PlaceReview review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new CustomException(PlaceReviewErrorCode.REVIEW_NOT_FOUND));

    if (!review.getUser().getUserId().equals(userId)) {
      throw new CustomException(PlaceReviewErrorCode.REVIEW_ACCESS_DENIED);
    }

    review.setTitle(dto.getTitle());
    review.setContents(dto.getContents());
    review.setRating(dto.getRating());
    review.setReviewType(dto.getReviewType());

    updatePlaceAverageRating(review.getPlace().getPlaceId());

    return reviewMapper.toResponseDto(review);
  }

  /** ✅ ✅ ✅ 리뷰 삭제 (물리 삭제로 변경) */
  public void deleteReview(Long userId, Long reviewId) {

    PlaceReview review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new CustomException(PlaceReviewErrorCode.REVIEW_NOT_FOUND));

    if (!review.getUser().getUserId().equals(userId)) {
      throw new CustomException(PlaceReviewErrorCode.REVIEW_ACCESS_DENIED);
    }

    String placeId = review.getPlace().getPlaceId();

    reviewRepository.delete(review);   // ✅ 물리 삭제

    updatePlaceAverageRating(placeId);
  }

  /** 평균 평점 갱신 */
  private void updatePlaceAverageRating(String placeId) {
    Double avg = reviewRepository.calculateAverageRating(placeId);
    Place place = placeRepository.findById(placeId)
        .orElseThrow(() -> new CustomException(PlaceReviewErrorCode.PLACE_NOT_FOUND));

    if (avg == null) avg = 0.0;
    place.setRating(avg);
    placeRepository.save(place);
  }
}
