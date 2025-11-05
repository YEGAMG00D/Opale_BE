package yegam.opale_be.domain.culture.performance.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yegam.opale_be.domain.culture.performance.dto.request.PerformanceSearchRequestDto;
import yegam.opale_be.domain.culture.performance.dto.response.detail.*;
import yegam.opale_be.domain.culture.performance.dto.response.list.*;
import yegam.opale_be.domain.culture.performance.entity.*;
import yegam.opale_be.domain.culture.performance.exception.PerformanceErrorCode;
import yegam.opale_be.domain.culture.performance.mapper.PerformanceMapper;
import yegam.opale_be.domain.culture.performance.repository.PerformanceRepository;
import yegam.opale_be.global.common.BasePerformanceListResponseDto;
import yegam.opale_be.global.exception.CustomException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceService {

  private final PerformanceRepository performanceRepository;
  private final PerformanceMapper performanceMapper;

  /** ✅ 공연 목록 조회 */
  public PerformanceListResponseDto getPerformanceList(PerformanceSearchRequestDto dto) {
    String genre = emptyToNull(dto.getGenre());
    String keyword = emptyToNull(dto.getKeyword());
    String area = emptyToNull(dto.getArea());
    String sortType = (dto.getSortType() == null || dto.getSortType().isBlank()) ? "최신" : dto.getSortType();

    int page = (dto.getPage() != null && dto.getPage() > 0) ? dto.getPage() - 1 : 0;
    int size = (dto.getSize() != null && dto.getSize() > 0) ? dto.getSize() : 20;
    PageRequest pageable = PageRequest.of(page, size);

    Page<Performance> performancePage = performanceRepository.search(genre, keyword, area, sortType, pageable);
    return performanceMapper.toPagedPerformanceListDto(performancePage);
  }

  /** ✅ 인기 공연 조회 */
  public PerformanceListResponseDto getTopPerformances() {
    List<Performance> performances = performanceRepository.findTop10ByOrderByUpdatedateDesc();
    return performanceMapper.toPerformanceListDto(performances);
  }

  /** ✅ 오늘 공연 조회 */
  public PerformanceListResponseDto getTodayPerformances(String type) {
    List<Performance> performances = performanceRepository.findPerformancesByTypeAndDate(type, LocalDate.now());
    return performanceMapper.toPerformanceListDto(performances);
  }

  /** ✅ 공연 기본 정보 조회 */
  public PerformanceBasicResponseDto getPerformanceBasic(String performanceId) {
    Performance performance = performanceRepository.findById(performanceId)
        .orElseThrow(() -> new CustomException(PerformanceErrorCode.PERFORMANCE_NOT_FOUND));
    return performanceMapper.toPerformanceBasicDto(performance);
  }

  // ---------------------------------------------------------------------
  // 🎭 상세 정보용 리스트 응답 (BaseListResponseDto 적용)
  // ---------------------------------------------------------------------

  /** ✅ 공연 예매처 목록 */
  public BasePerformanceListResponseDto<PerformanceRelationResponseDto> getPerformanceRelations(String performanceId) {
    Performance p = performanceRepository.findByIdWithRelations(performanceId)
        .orElseThrow(() -> new CustomException(PerformanceErrorCode.PERFORMANCE_NOT_FOUND));
    List<PerformanceRelationResponseDto> list = p.getPerformanceRelations().stream()
        .map(performanceMapper::toPerformanceRelationDto)
        .collect(Collectors.toList());
    return performanceMapper.toBaseListResponse(p, list);
  }

  /** ✅ 공연 영상 목록 */
  public BasePerformanceListResponseDto<PerformanceVideoResponseDto> getPerformanceVideos(String performanceId) {
    Performance p = performanceRepository.findByIdWithVideos(performanceId)
        .orElseThrow(() -> new CustomException(PerformanceErrorCode.PERFORMANCE_NOT_FOUND));
    List<PerformanceVideoResponseDto> list = p.getPerformanceVideos().stream()
        .map(performanceMapper::toPerformanceVideoDto)
        .collect(Collectors.toList());
    return performanceMapper.toBaseListResponse(p, list);
  }

  /** ✅ 공연 수집 이미지 목록 */
  public BasePerformanceListResponseDto<PerformanceImageResponseDto> getPerformanceImages(String performanceId) {
    Performance p = performanceRepository.findByIdWithImages(performanceId)
        .orElseThrow(() -> new CustomException(PerformanceErrorCode.PERFORMANCE_NOT_FOUND));
    List<PerformanceImageResponseDto> list = p.getPerformanceImages().stream()
        .map(performanceMapper::toPerformanceImageDto)
        .collect(Collectors.toList());
    return performanceMapper.toBaseListResponse(p, list);
  }

  /** ✅ 공연 소개 이미지 목록 */
  public BasePerformanceListResponseDto<PerformanceInfoImageResponseDto> getPerformanceInfoImages(String performanceId) {
    Performance p = performanceRepository.findByIdWithInfoImages(performanceId)
        .orElseThrow(() -> new CustomException(PerformanceErrorCode.PERFORMANCE_NOT_FOUND));
    List<PerformanceInfoImageResponseDto> list = p.getPerformanceInfoImages().stream()
        .sorted((a, b) -> a.getOrderIndex().compareTo(b.getOrderIndex()))
        .map(img -> PerformanceInfoImageResponseDto.builder()
            .imageUrl(img.getImageUrl())
            .orderIndex(img.getOrderIndex())
            .build())
        .collect(Collectors.toList());
    return performanceMapper.toBaseListResponse(p, list);
  }

  /** ✅ 공연 예매 정보 조회 */
  public PerformanceDetailResponseDto getPerformanceBooking(String performanceId) {
    Performance p = performanceRepository.findById(performanceId)
        .orElseThrow(() -> new CustomException(PerformanceErrorCode.PERFORMANCE_NOT_FOUND));
    return performanceMapper.toPerformanceDetailDto(
        p,
        p.getPerformanceImages(),
        p.getPerformanceRelations(),
        p.getPerformanceVideos()
    );
  }

  private String emptyToNull(String s) {
    return (s == null || s.isBlank()) ? null : s;
  }
}
