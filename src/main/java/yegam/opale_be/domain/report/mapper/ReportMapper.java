package yegam.opale_be.domain.report.mapper;

import yegam.opale_be.domain.report.dto.request.ReportCreateRequestDto;
import yegam.opale_be.domain.report.dto.response.ReportDetailResponseDto;
import yegam.opale_be.domain.report.dto.response.ReportSummaryResponseDto;
import yegam.opale_be.domain.report.entity.Report;
import yegam.opale_be.domain.report.entity.ReportStatus;

import java.util.List;
import java.util.stream.Collectors;

public class ReportMapper {

  /**
   * 신고 생성 요청 DTO → 엔티티
   */
  public static Report toEntity(ReportCreateRequestDto dto, Long reporterId) {
    if (dto == null) return null;

    return Report.builder()
        .reporterId(reporterId)
        .targetUserId(dto.getTargetUserId())
        .targetType(dto.getTargetType())
        .targetId(dto.getTargetId())
        .reason(dto.getReason())
        .detail(dto.getDetail())
        .status(ReportStatus.PENDING)   // 기본값: 처리 대기
        .build();
  }

  /**
   * 엔티티 → 신고 목록용 요약 DTO
   */
  public static ReportSummaryResponseDto toSummaryDto(Report report) {
    if (report == null) return null;

    return ReportSummaryResponseDto.builder()
        .reportId(report.getReportId())
        .reporterId(report.getReporterId())
        .targetUserId(report.getTargetUserId())
        .targetType(report.getTargetType())
        .targetId(report.getTargetId())
        .reason(report.getReason())
        .status(report.getStatus())
        .createdAt(report.getCreatedAt())
        .build();
  }

  /**
   * 엔티티 리스트 → 신고 목록용 요약 DTO 리스트
   */
  public static List<ReportSummaryResponseDto> toSummaryDtoList(List<Report> reports) {
    if (reports == null) return List.of();
    return reports.stream()
        .map(ReportMapper::toSummaryDto)
        .collect(Collectors.toList());
  }

  /**
   * 엔티티 → 신고 상세 DTO
   */
  public static ReportDetailResponseDto toDetailDto(Report report) {
    if (report == null) return null;

    return ReportDetailResponseDto.builder()
        .reportId(report.getReportId())
        .reporterId(report.getReporterId())
        .targetUserId(report.getTargetUserId())
        .targetType(report.getTargetType())
        .targetId(report.getTargetId())
        .reason(report.getReason())
        .detail(report.getDetail())
        .status(report.getStatus())
        .adminMemo(report.getAdminMemo())
        .createdAt(report.getCreatedAt())
        .updatedAt(report.getUpdatedAt())
        .build();
  }
}
