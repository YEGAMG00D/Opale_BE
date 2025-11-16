package yegam.opale_be.domain.recommendation.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import yegam.opale_be.domain.recommendation.exception.RecommendationErrorCode;
import yegam.opale_be.global.exception.CustomException;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EmbeddingVectorUtil {

  private final ObjectMapper objectMapper;

  /**
   * JSON 문자열("[0.1, 0.2, ...]") -> List<Double>
   */
  public List<Double> parseToList(String json) {
    if (json == null || json.isBlank()) {
      throw new CustomException(RecommendationErrorCode.VECTOR_PARSE_FAILED);
    }
    try {
      Double[] arr = objectMapper.readValue(json, Double[].class);
      return Arrays.asList(arr);
    } catch (JsonProcessingException e) {
      throw new CustomException(RecommendationErrorCode.VECTOR_PARSE_FAILED);
    }
  }
}
