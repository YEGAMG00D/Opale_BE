package yegam.opale_be.domain.reservation.service;

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
import yegam.opale_be.domain.reservation.dto.request.*;
import yegam.opale_be.domain.reservation.dto.response.*;
import yegam.opale_be.domain.reservation.entity.UserTicketVerification;
import yegam.opale_be.domain.reservation.exception.ReservationErrorCode;
import yegam.opale_be.domain.reservation.mapper.ReservationMapper;
import yegam.opale_be.domain.reservation.repository.UserTicketVerificationRepository;
import yegam.opale_be.domain.user.entity.User;
import yegam.opale_be.domain.user.repository.UserRepository;
import yegam.opale_be.global.exception.CustomException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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



  /** 티켓 이미지 OCR → 텍스트 추출 */
  public TicketOcrResponseDto extractTicketInfoByOcr(MultipartFile file) {

    // 1) OCR 전용 서비스 호출 (GPT Vision)
    Map<String, String> result = ocrService.extractFromImage(file);

    // 2) 날짜 파싱
    LocalDateTime performanceDate = null;
    try {
      if (result.get("performanceDate") != null) {
        performanceDate = LocalDateTime.parse(result.get("performanceDate"));
      }
    } catch (Exception e) {
      log.warn("❌ OCR 날짜 파싱 실패: {}", result.get("performanceDate"));
    }

    // 3) DTO 변환 후 리턴
    return TicketOcrResponseDto.builder()
        .performanceName(result.get("performanceName"))
        .performanceDate(performanceDate)
        .seatInfo(result.get("seatInfo"))
        .placeName(result.get("placeName"))
        .build();
  }




  /** ✅ 티켓 등록 */
  public TicketDetailResponseDto createTicket(Long userId, TicketCreateRequestDto dto) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(ReservationErrorCode.INVALID_TICKET_DATA));

    LocalDate performanceDateOnly = dto.getPerformanceDate() != null
        ? dto.getPerformanceDate().toLocalDate()
        : null;

    // ✅ 공연명 + 날짜 기반 상연중 공연 매칭 (없으면 null)
    Performance performance = performanceRepository
        .findFirstByTitleAndDateRange(dto.getPerformanceName(), performanceDateOnly)
        .orElse(null);

    // ✅ 공연장명 일부 일치 검색
    Place place = placeRepository
        .findFirstByNameContainingIgnoreCase(dto.getPlaceName())
        .orElse(null);

    UserTicketVerification ticket = reservationMapper.toEntity(dto, user, performance, place);
    ticketRepository.save(ticket);

    log.info("🎟️ 티켓 등록 완료: ticketId={}, userId={}, performance={}, place={}",
        ticket.getTicketId(), userId,
        performance != null ? performance.getTitle() : "null",
        place != null ? place.getName() : "null");

    return reservationMapper.toDetailResponseDto(ticket);
  }

  /** ✅ 티켓 수정 */
  public TicketDetailResponseDto updateTicket(Long userId, Long ticketId, TicketUpdateRequestDto dto) {
    UserTicketVerification ticket = ticketRepository.findByTicketIdAndUser_UserId(ticketId, userId)
        .orElseThrow(() -> new CustomException(ReservationErrorCode.TICKET_NOT_FOUND));

    LocalDate performanceDateOnly = dto.getPerformanceDate() != null
        ? dto.getPerformanceDate().toLocalDate()
        : null;

    Performance performance = performanceRepository
        .findFirstByTitleAndDateRange(dto.getPerformanceName(), performanceDateOnly)
        .orElse(null);

    Place place = placeRepository
        .findFirstByNameContainingIgnoreCase(dto.getPlaceName())
        .orElse(null);


    ticket.setPerformanceName(dto.getPerformanceName());
    ticket.setSeatInfo(dto.getSeatInfo());
    ticket.setPerformanceDate(dto.getPerformanceDate());
    ticket.setPlaceName(dto.getPlaceName());
    ticket.setPerformance(performance);
    ticket.setPlace(place);
    ticket.setUpdatedAt(LocalDateTime.now());

    log.info("📝 티켓 수정 완료: ticketId={}, userId={}", ticket.getTicketId(), userId);
    return reservationMapper.toDetailResponseDto(ticket);
  }

  /** ✅ 티켓 삭제 */
  public void deleteTicket(Long userId, Long ticketId) {
    UserTicketVerification ticket = ticketRepository.findByTicketIdAndUser_UserId(ticketId, userId)
        .orElseThrow(() -> new CustomException(ReservationErrorCode.TICKET_NOT_FOUND));

    ticketRepository.delete(ticket);
    log.info("🗑️ 티켓 삭제 완료: ticketId={}, userId={}", ticketId, userId);
  }

  /** ✅ 단일 티켓 조회 */
  @Transactional(readOnly = true)
  public TicketDetailResponseDto getTicket(Long userId, Long ticketId) {
    UserTicketVerification ticket = ticketRepository.findByTicketIdAndUser_UserId(ticketId, userId)
        .orElseThrow(() -> new CustomException(ReservationErrorCode.TICKET_NOT_FOUND));

    return reservationMapper.toDetailResponseDto(ticket);
  }

  /** ✅ 티켓 목록 조회 */
  @Transactional(readOnly = true)
  public TicketSimpleListResponseDto getTicketList(Long userId, int page, int size) {
    PageRequest pageable = PageRequest.of(page - 1, size);
    Page<UserTicketVerification> ticketPage =
        ticketRepository.findAllByUser_UserIdOrderByRequestedAtDesc(userId, pageable);

    List<TicketSimpleResponseDto> ticketList =
        reservationMapper.toSimpleResponseDtoList(ticketPage.getContent());

    return TicketSimpleListResponseDto.builder()
        .totalCount(ticketPage.getTotalElements())
        .currentPage(page)
        .pageSize(size)
        .totalPages(ticketPage.getTotalPages())
        .hasNext(ticketPage.hasNext())
        .hasPrev(ticketPage.hasPrevious())
        .tickets(ticketList)
        .build();
  }
}
