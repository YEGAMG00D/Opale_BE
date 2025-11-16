package yegam.opale_be.domain.recommendation.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import yegam.opale_be.global.exception.model.BaseErrorCode;

@Getter
@AllArgsConstructor
public enum RecommendationErrorCode implements BaseErrorCode {

  USER_VECTOR_NOT_FOUND("RECO_4001", "사용자 선호 벡터가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
  PERFORMANCE_VECTOR_NOT_FOUND("RECO_4002", "해당 공연의 임베딩 벡터가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
  VECTOR_PARSE_FAILED("RECO_4003", "임베딩 벡터를 파싱할 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  PINECONE_QUERY_FAILED("RECO_5001", "벡터 검색 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
