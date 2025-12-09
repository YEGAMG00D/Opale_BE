package yegam.opale_be.domain.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import yegam.opale_be.domain.reservation.dto.request.*;
import yegam.opale_be.domain.reservation.dto.response.*;
import yegam.opale_be.domain.reservation.service.ReservationService;
import yegam.opale_be.global.exception.CustomException;
import yegam.opale_be.global.exception.GlobalErrorCode;
import yegam.opale_be.global.response.BaseResponse;

/**
 * ReservationController
 *  - 티켓 인증 CRUD API
 */
@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservation", description = "티켓 인증 관리 API")
@RequiredArgsConstructor
public class ReservationController {

  private final ReservationService reservationService;

  /** 티켓 이미지 OCR */
  @Operation(summary = "티켓 이미지 OCR", description = "티켓 이미지에서 텍스트 정보를 OCR로 추출합니다.")
  @PostMapping(value = "/ocr", consumes = "multipart/form-data")
  public ResponseEntity<BaseResponse<TicketOcrResponseDto>> extractTicketInfo(
      @AuthenticationPrincipal Long userId,
      @RequestPart("file") MultipartFile file
  ) {
    if (userId == null) throw new CustomException(GlobalErrorCode.UNAUTHORIZED);

    TicketOcrResponseDto response = reservationService.extractTicketInfoByOcr(file);

    return ResponseEntity.ok(BaseResponse.success("OCR 추출 성공", response));
  }


  /** 티켓 등록 */
  @Operation(summary = "티켓 인증 등록", description = "사용자가 직접 입력한 예매 정보를 등록합니다.")
  @PostMapping
  public ResponseEntity<BaseResponse<TicketDetailResponseDto>> createTicket(
      @AuthenticationPrincipal Long userId,
      @RequestBody @Valid TicketCreateRequestDto dto
  ) {
    if (userId == null) throw new CustomException(GlobalErrorCode.UNAUTHORIZED);
    TicketDetailResponseDto response = reservationService.createTicket(userId, dto);
    return ResponseEntity.ok(BaseResponse.success("티켓 인증 등록 완료", response));
  }

  /** 티켓 수정 */
  @Operation(summary = "티켓 인증 수정", description = "등록된 티켓 인증 정보를 수정합니다.")
  @PatchMapping("/{ticketId}")
  public ResponseEntity<BaseResponse<TicketDetailResponseDto>> updateTicket(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long ticketId,
      @RequestBody @Valid TicketUpdateRequestDto dto
  ) {
    if (userId == null) throw new CustomException(GlobalErrorCode.UNAUTHORIZED);
    TicketDetailResponseDto response = reservationService.updateTicket(userId, ticketId, dto);
    return ResponseEntity.ok(BaseResponse.success("티켓 인증 수정 완료", response));
  }

  /** 티켓 삭제 */
  @Operation(summary = "티켓 인증 삭제", description = "등록된 티켓 인증 정보를 삭제합니다.")
  @DeleteMapping("/{ticketId}")
  public ResponseEntity<BaseResponse<String>> deleteTicket(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long ticketId
  ) {
    if (userId == null) throw new CustomException(GlobalErrorCode.UNAUTHORIZED);
    reservationService.deleteTicket(userId, ticketId);
    return ResponseEntity.ok(BaseResponse.success("티켓 인증 삭제 완료", null));
  }

  /** 단일 조회 */
  @Operation(summary = "티켓 인증 단일 조회", description = "티켓 인증 단건 정보를 조회합니다.")
  @GetMapping("/{ticketId}")
  public ResponseEntity<BaseResponse<TicketDetailResponseDto>> getTicket(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long ticketId
  ) {
    if (userId == null) throw new CustomException(GlobalErrorCode.UNAUTHORIZED);
    TicketDetailResponseDto response = reservationService.getTicket(userId, ticketId);
    return ResponseEntity.ok(BaseResponse.success("티켓 인증 조회 성공", response));
  }

  /** 목록 조회 */
  @Operation(summary = "티켓 인증 목록 조회", description = "사용자의 전체 티켓 인증 내역을 조회합니다.")
  @GetMapping("/list")
  public ResponseEntity<BaseResponse<TicketSimpleListResponseDto>> getTicketList(
      @AuthenticationPrincipal Long userId,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    if (userId == null) throw new CustomException(GlobalErrorCode.UNAUTHORIZED);
    TicketSimpleListResponseDto response = reservationService.getTicketList(userId, page, size);
    return ResponseEntity.ok(BaseResponse.success("티켓 인증 목록 조회 성공", response));
  }

  /** ✅ 상세 티켓 인증 목록 조회 */
  @Operation(summary = "티켓 인증 상세 목록 조회", description = "사용자의 전체 티켓 인증 내역을 상세 정보로 조회합니다.")
  @GetMapping("/list/detail")
  public ResponseEntity<BaseResponse<TicketDetailListResponseDto>> getTicketDetailList(
      @AuthenticationPrincipal Long userId,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    if (userId == null) throw new CustomException(GlobalErrorCode.UNAUTHORIZED);

    TicketDetailListResponseDto response =
        reservationService.getTicketDetailList(userId, page, size);

    return ResponseEntity.ok(
        BaseResponse.success("티켓 인증 상세 목록 조회 성공", response)
    );
  }


  @Operation(summary = "티켓 기반 리뷰 조회", description = "티켓 ID로 공연 리뷰 + 공연장 리뷰를 조회합니다.")
  @GetMapping("/{ticketId}/reviews")
  public ResponseEntity<BaseResponse<TicketReviewBundleResponseDto>> getTicketReviews(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long ticketId
  ) {
    if (userId == null) throw new CustomException(GlobalErrorCode.UNAUTHORIZED);

    TicketReviewBundleResponseDto response = reservationService.getTicketReviews(userId, ticketId);

    return ResponseEntity.ok(BaseResponse.success("티켓 리뷰 조회 성공", response));
  }



}
