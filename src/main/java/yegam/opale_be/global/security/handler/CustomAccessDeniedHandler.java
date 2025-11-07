package yegam.opale_be.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import yegam.opale_be.global.exception.GlobalErrorCode;
import yegam.opale_be.global.response.BaseResponse;

import java.io.IOException;

/**
 * 인증은 되었지만 권한이 부족할 때 발생 (403)
 */
@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
      throws IOException {

    log.warn("권한 부족 (403) - URL: {}", request.getRequestURI());

    GlobalErrorCode errorCode = GlobalErrorCode.FORBIDDEN;
    BaseResponse<Object> body = BaseResponse.error(
        errorCode.getStatus().value(),
        errorCode.getMessage()
    );

    response.setStatus(errorCode.getStatus().value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");
    new ObjectMapper().writeValue(response.getWriter(), body);
  }
}
