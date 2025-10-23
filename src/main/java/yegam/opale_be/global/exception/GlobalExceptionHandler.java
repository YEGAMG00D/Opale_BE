package yegam.opale_be.global.exception;

import yegam.opale_be.global.exception.model.BaseErrorCode;
import yegam.opale_be.global.response.BaseResponse;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  // 커스텀 예외
  @ExceptionHandler(CustomException.class)
  public ResponseEntity<BaseResponse<Object>> handleCustomException(CustomException ex) {
    BaseErrorCode errorCode = ex.getErrorCode();
    log.error("Custom 오류 발생: {}", ex.getMessage());
    return ResponseEntity
        .status(errorCode.getStatus())
        .body(BaseResponse.error(errorCode.getStatus().value(), ex.getMessage()));
  }

  // Validation 실패
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<BaseResponse<Object>> handleValidationException(
      MethodArgumentNotValidException ex) {
    String errorMessages =
        ex.getBindingResult().getFieldErrors().stream()
            .map(e -> String.format("[%s] %s", e.getField(), e.getDefaultMessage()))
            .collect(Collectors.joining(" / "));
    log.warn("Validation 오류 발생: {}", errorMessages);
    return ResponseEntity.badRequest().body(BaseResponse.error(400, errorMessages));
  }

  // 잘못된 HTTP Method 사용 처리
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<BaseResponse<Object>> handleMethodNotSuppoertedException(
      HttpRequestMethodNotSupportedException ex){
    log.warn("지원하지 않는 HTTP Method: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
        .body(BaseResponse.error(HttpStatus.METHOD_NOT_ALLOWED.value(), "지원하지 않는 HTTP Method입니다."));
  }


  // Param이 없음.
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<BaseResponse<Object>> handleParamNotSuppoertedException(
      MissingServletRequestParameterException ex){
    log.warn("필요한 Param 누락: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(BaseResponse.error(HttpStatus.BAD_REQUEST.value(), "Param 값이 누락되었습니다."));
  }

  // 클라이언트 request 데이터 잘못
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<BaseResponse<Object>> handleRequestException(
      HttpMessageNotReadableException ex){
    log.warn("클라이언트 request 데이터 에러 발생: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(BaseResponse.error(HttpStatus.BAD_REQUEST.value(), "클라이언트 측으로부터 잘못된 형식의 request가 전달되었습니다."));
  }


  // 잘못된 엔드포인트 지정
  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<BaseResponse<Object>> handleNotFoundURLException(
      NoHandlerFoundException ex){
    log.warn("잘못된 엔드포인트 지정: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(BaseResponse.error(HttpStatus.NOT_FOUND.value(), "요청한 URL은 존재하지 않습니다."));
  }



  // 예상치 못한 예외
  @ExceptionHandler(Exception.class)
  public ResponseEntity<BaseResponse<Object>> handleException(Exception ex) {
    log.error("Server 오류 발생: ", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(BaseResponse.error(500, "예상치 못한 서버 오류가 발생했습니다."));
  }
}
