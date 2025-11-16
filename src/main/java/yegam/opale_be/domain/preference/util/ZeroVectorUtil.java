package yegam.opale_be.domain.preference.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ZeroVectorUtil {

  @Value("${pinecone.dimension}")
  private int dimension;

  private final ObjectMapper objectMapper;

  /** 1536차원 0-vector 리스트 생성 */
  public List<Double> generateZeroVector() {
    return Collections.nCopies(dimension, 0.0);
  }

  /** JSON 문자열 형태의 0-vector 저장값 */
  public String generateZeroVectorJson() {
    try {
      return objectMapper.writeValueAsString(generateZeroVector());
    } catch (JsonProcessingException e) {
      return "[]";
    }
  }
}
