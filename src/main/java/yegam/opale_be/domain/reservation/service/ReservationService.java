package yegam.opale_be.domain.reservation.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import yegam.opale_be.domain.culture.performance.entity.Performance;
import yegam.opale_be.domain.culture.performance.repository.PerformanceRepository;
import yegam.opale_be.domain.place.entity.Place;
import yegam.opale_be.domain.place.repository.PlaceRepository;
import yegam.opale_be.domain.reservation.dto.request.TicketCreateRequestDto;
import yegam.opale_be.domain.reservation.dto.request.TicketUpdateRequestDto;
import yegam.opale_be.domain.reservation.dto.response.TicketDetailListResponseDto;
import yegam.opale_be.domain.reservation.dto.response.TicketDetailResponseDto;
import yegam.opale_be.domain.reservation.dto.response.TicketOcrResponseDto;
import yegam.opale_be.domain.reservation.dto.response.TicketReviewBundleResponseDto;
import yegam.opale_be.domain.reservation.dto.response.TicketSimpleListResponseDto;
import yegam.opale_be.domain.reservation.dto.response.TicketSimpleResponseDto;
import yegam.opale_be.domain.reservation.entity.UserTicketVerification;
import yegam.opale_be.domain.reservation.exception.ReservationErrorCode;
import yegam.opale_be.domain.reservation.mapper.ReservationMapper;
import yegam.opale_be.domain.reservation.repository.UserTicketVerificationRepository;
import yegam.opale_be.domain.review.performance.entity.PerformanceReview;
import yegam.opale_be.domain.review.performance.mapper.PerformanceReviewMapper;
import yegam.opale_be.domain.review.performance.repository.PerformanceReviewRepository;
import yegam.opale_be.domain.review.place.entity.PlaceReview;
import yegam.opale_be.domain.review.place.mapper.PlaceReviewMapper;
import yegam.opale_be.domain.review.place.repository.PlaceReviewRepository;
import yegam.opale_be.domain.user.entity.User;
import yegam.opale_be.domain.user.repository.UserRepository;
import yegam.opale_be.global.exception.CustomException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

  private final UserTicketVerificationRepository ticketRepository;
  private final UserRepository userRepository;
  private final PerformanceRepository performanceRepository;
  private final PlaceRepository placeRepository;
  private final ReservationMapper reservationMapper;
  private final OcrService ocrService;

  private final PerformanceReviewRepository performanceReviewRepository;
  private final PlaceReviewRepository placeReviewRepository;

  private final PerformanceReviewMapper performanceReviewMapper;
  private final PlaceReviewMapper placeReviewMapper;

  /** 티켓 이미지 OCR → 텍스트 추출 */
  public TicketOcrResponseDto extractTicketInfoByOcr(MultipartFile file) {
    return ocrService.extractFromImage(file);
  }


  /** 🔥 티켓 등록 */
  public TicketDetailResponseDto createTicket(Long userId, TicketCreateRequestDto dto) {

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(ReservationErrorCode.INVALID_TICKET_DATA));

    // ✅ 1) 공연 ID 우선
    Performance performance = null;
    if (dto.getPerformanceId() != null && !dto.getPerformanceId().isBlank()) {
      performance = performanceRepository
          .findById(dto.getPerformanceId())
          .orElse(null);
    }

    // ✅ 2) ID 없으면 기존 name + 날짜 매칭
    if (performance == null && dto.getPerformanceName() != null) {
      LocalDate performanceDateOnly =
          dto.getPerformanceDate() != null ? dto.getPerformanceDate().toLocalDate() : null;

      performance = performanceRepository
          .findFirstByTitleAndDateRange(dto.getPerformanceName(), performanceDateOnly)
          .orElse(null);
    }

    // ✅ 3) 공연장 ID 우선
    Place place = null;
    if (dto.getPlaceId() != null && !dto.getPlaceId().isBlank()) {
      place = placeRepository
          .findById(dto.getPlaceId())
          .orElse(null);
    }

    // ✅ 4) ID 없으면 name 기반 검색
    if (place == null && dto.getPlaceName() != null && !dto.getPlaceName().isBlank()) {
      place = placeRepository
          .findFirstByNameContainingIgnoreCase(dto.getPlaceName())
          .orElse(null);
    }

    // ✅ 5) 그래도 없으면 performance에서 추론
    if (place == null && performance != null) {
      place = performance.getPlace();
    }

    // ✅ 좌석 포맷 보정
    dto.setSeatInfo(normalizeSeatInfo(dto.getSeatInfo()));

    UserTicketVerification ticket =
        reservationMapper.toEntity(dto, user, performance, place);

    ticket.setIsVerified(false);
    ticket.setRequestedAt(LocalDateTime.now());
    ticket.setUpdatedAt(LocalDateTime.now());

    ticketRepository.save(ticket);

    return reservationMapper.toDetailResponseDto(ticket);
  }


  /** 🔥 티켓 수정 */
  public TicketDetailResponseDto updateTicket(Long userId, Long ticketId, TicketUpdateRequestDto dto) {

    UserTicketVerification ticket = ticketRepository
        .findByTicketIdAndUser_UserId(ticketId, userId)
        .orElseThrow(() -> new CustomException(ReservationErrorCode.TICKET_NOT_FOUND));

    // ✅ 공연 ID 우선
    Performance performance = null;
    if (dto.getPerformanceId() != null && !dto.getPerformanceId().isBlank()) {
      performance = performanceRepository
          .findById(dto.getPerformanceId())
          .orElse(null);
    }

    if (performance == null && dto.getPerformanceName() != null) {
      LocalDate performanceDateOnly =
          dto.getPerformanceDate() != null ? dto.getPerformanceDate().toLocalDate() : null;

      performance = performanceRepository
          .findFirstByTitleAndDateRange(dto.getPerformanceName(), performanceDateOnly)
          .orElse(null);
    }

    // ✅ 공연장 ID 우선
    Place place = null;
    if (dto.getPlaceId() != null && !dto.getPlaceId().isBlank()) {
      place = placeRepository
          .findById(dto.getPlaceId())
          .orElse(null);
    }

    if (place == null && dto.getPlaceName() != null && !dto.getPlaceName().isBlank()) {
      place = placeRepository
          .findFirstByNameContainingIgnoreCase(dto.getPlaceName())
          .orElse(null);
    }

    if (place == null && performance != null) {
      place = performance.getPlace();
    }

    // ✅ 값 반영 (null 허용)
    ticket.setPerformanceName(dto.getPerformanceName());
    ticket.setPerformanceDate(dto.getPerformanceDate());
    ticket.setSeatInfo(normalizeSeatInfo(dto.getSeatInfo()));
    ticket.setPlaceName(dto.getPlaceName());
    ticket.setPerformance(performance); // ✅ null 가능
    ticket.setPlace(place);             // ✅ null 가능
    ticket.setUpdatedAt(LocalDateTime.now());

    return reservationMapper.toDetailResponseDto(ticket);
  }


  /** 티켓 삭제 */
  public void deleteTicket(Long userId, Long ticketId) {
    UserTicketVerification ticket = ticketRepository.findByTicketIdAndUser_UserId(ticketId, userId)
        .orElseThrow(() -> new CustomException(ReservationErrorCode.TICKET_NOT_FOUND));

    ticketRepository.delete(ticket);
    log.info("🗑️ 티켓 삭제 완료: ticketId={}, userId={}", ticketId, userId);
  }

  /** 단일 조회 */
  @Transactional(readOnly = true)
  public TicketDetailResponseDto getTicket(Long userId, Long ticketId) {
    UserTicketVerification ticket = ticketRepository.findByTicketIdAndUser_UserId(ticketId, userId)
        .orElseThrow(() -> new CustomException(ReservationErrorCode.TICKET_NOT_FOUND));

    return reservationMapper.toDetailResponseDto(ticket);
  }

  /** 목록 조회 */
  @Transactional(readOnly = true)
  public TicketSimpleListResponseDto getTicketList(Long userId, int page, int size) {

    PageRequest pageable = PageRequest.of(page - 1, size);

    Page<UserTicketVerification> ticketPage =
        ticketRepository.findAllByUser_UserIdOrderByRequestedAtDesc(userId, pageable);

    List<TicketSimpleResponseDto> tickets =
        reservationMapper.toSimpleResponseDtoList(ticketPage.getContent());

    return TicketSimpleListResponseDto.builder()
        .totalCount(ticketPage.getTotalElements())
        .currentPage(page)
        .pageSize(size)
        .totalPages(ticketPage.getTotalPages())
        .hasNext(ticketPage.hasNext())
        .hasPrev(ticketPage.hasPrevious())
        .tickets(tickets)
        .build();
  }

  /** ✅ 상세 티켓 인증 목록 조회 */
  @Transactional(readOnly = true)
  public TicketDetailListResponseDto getTicketDetailList(Long userId, int page, int size) {

    PageRequest pageable = PageRequest.of(page - 1, size);

    Page<UserTicketVerification> ticketPage =
        ticketRepository.findAllByUser_UserIdOrderByRequestedAtDesc(userId, pageable);

    return reservationMapper.toDetailListResponseDto(ticketPage, page, size);
  }




  @Transactional(readOnly = true)
  public TicketReviewBundleResponseDto getTicketReviews(Long userId, Long ticketId) {

    // 1) 티켓이 실제 사용자 것인지 검증
    UserTicketVerification ticket = ticketRepository
        .findByTicketIdAndUser_UserId(ticketId, userId)
        .orElseThrow(() -> new CustomException(ReservationErrorCode.TICKET_NOT_FOUND));

    // 2) 공연 리뷰 검색
    PerformanceReview performanceReview = performanceReviewRepository
        .findByTicket_TicketId(ticketId)
        .orElse(null);

    // 3) 공연장 리뷰 검색
    PlaceReview placeReview = placeReviewRepository
        .findByTicket_TicketId(ticketId)
        .orElse(null);

    return TicketReviewBundleResponseDto.builder()
        .ticketId(ticketId)
        .performanceReview(
            performanceReview != null ? performanceReviewMapper.toResponseDto(performanceReview) : null
        )
        .placeReview(
            placeReview != null ? placeReviewMapper.toResponseDto(placeReview) : null
        )
        .build();
  }

  // ✅ 좌석 정보 통일 포맷: "다 11열 4번" → "다 11열-4번"
  private String normalizeSeatInfo(String seatInfo) {
    if (seatInfo == null) return null;

    String trimmed = seatInfo.trim();
    if (trimmed.isEmpty()) return null;

    java.util.regex.Pattern p =
        java.util.regex.Pattern.compile("^(.*?)(\\d+번)\\s*$");
    java.util.regex.Matcher m = p.matcher(trimmed);

    if (m.matches()) {
      return m.group(1).trim() + "-" + m.group(2).trim();
    }

    return trimmed;
  }



}
