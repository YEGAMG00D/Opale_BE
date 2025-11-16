package yegam.opale_be.domain.preference.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ZeroVectorUtil {

  private final ObjectMapper objectMapper;

  private static final int VECTOR_DIM = 1536;

  /** 1536차원 0-vector JSON 생성 */
  public String generateZeroVectorJson() {
    List<Double> zeros = Collections.nCopies(VECTOR_DIM, 0.0);
    try {
      return objectMapper.writeValueAsString(zeros);
    } catch (Exception e) {
      return "[]"; // fallback
    }
  }

  /** 파싱된 1536차원 0-vector 리스트 직접 반환 */
  public List<Double> generateZeroVector() {
    return Collections.nCopies(VECTOR_DIM, 0.0);
  }
}
