package yegam.opale_be.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import yegam.opale_be.global.exception.model.BaseErrorCode;
import yegam.opale_be.global.response.BaseResponse;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  /** ✅ 커스텀 예외 */
  @ExceptionHandler(CustomException.class)
  public ResponseEntity<BaseResponse<Object>> handleCustomException(CustomException ex) {
    BaseErrorCode errorCode = ex.getErrorCode();
    log.warn("[CustomException] {}", ex.getMessage());
    return ResponseEntity.status(errorCode.getStatus())
        .body(BaseResponse.error(errorCode.getStatus().value(), ex.getMessage()));
  }

  /** ✅ Validation 실패 */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<BaseResponse<Object>> handleValidationException(MethodArgumentNotValidException ex) {
    String errorMessages = ex.getBindingResult().getFieldErrors().stream()
        .map(e -> String.format("[%s] %s", e.getField(), e.getDefaultMessage()))
        .collect(Collectors.joining(" / "));
    log.warn("Validation 오류: {}", errorMessages);
    return ResponseEntity.badRequest().body(BaseResponse.error(400, errorMessages));
  }

  /** ✅ 잘못된 HTTP Method */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<BaseResponse<Object>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
    log.warn("잘못된 HTTP Method 요청: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
        .body(BaseResponse.error(HttpStatus.METHOD_NOT_ALLOWED.value(), "지원하지 않는 HTTP Method입니다."));
  }

  /** ✅ 파라미터 누락 */
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<BaseResponse<Object>> handleMissingParam(MissingServletRequestParameterException ex) {
    log.warn("필수 파라미터 누락: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(BaseResponse.error(HttpStatus.BAD_REQUEST.value(), "요청에 필요한 파라미터가 누락되었습니다."));
  }

  /** ✅ JSON 형식 오류 */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<BaseResponse<Object>> handleInvalidJson(HttpMessageNotReadableException ex) {
    log.warn("잘못된 JSON 형식 요청: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(BaseResponse.error(HttpStatus.BAD_REQUEST.value(), "잘못된 JSON 형식의 요청입니다."));
  }

  /** ✅ 잘못된 URL */
  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<BaseResponse<Object>> handleNotFoundUrl(NoHandlerFoundException ex) {
    log.warn("잘못된 URL 요청: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(BaseResponse.error(HttpStatus.NOT_FOUND.value(), "요청한 URL은 존재하지 않습니다."));
  }

  /** ✅ DB 접근 오류 (예: null ID 접근) */
  @ExceptionHandler(InvalidDataAccessApiUsageException.class)
  public ResponseEntity<BaseResponse<Object>> handleInvalidDataAccess(InvalidDataAccessApiUsageException ex) {
    log.warn("DB 접근 오류 발생: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(BaseResponse.error(HttpStatus.UNAUTHORIZED.value(), "로그인이 필요합니다. (잘못된 사용자 접근 또는 토큰 없음)"));
  }

  /** ✅ 서버 내부 오류 */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<BaseResponse<Object>> handleGeneralException(Exception ex) {
    log.error("🚨 서버 내부 오류 발생", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(BaseResponse.error(500, "예상치 못한 서버 오류가 발생했습니다."));
  }
}
