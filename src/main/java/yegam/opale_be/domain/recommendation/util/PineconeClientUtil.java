package yegam.opale_be.domain.recommendation.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import yegam.opale_be.domain.recommendation.exception.RecommendationErrorCode;
import yegam.opale_be.global.exception.CustomException;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class PineconeClientUtil {

  @Value("${pinecone.api-key}")
  private String apiKey;

  @Value("${pinecone.host}")
  private String host;

  @Value("${pinecone.dimension}")
  private int dimension;

  private final ObjectMapper objectMapper;
  private final RestTemplate restTemplate = new RestTemplate();

  /**
   * Pinecone query 호출 — 최신 API 규격 대응
   */
  public List<PineconeMatch> query(List<Double> vector, int topK) {

    if (vector == null || vector.isEmpty()) {
      throw new CustomException(RecommendationErrorCode.VECTOR_PARSE_FAILED);
    }

    if (vector.size() != dimension) {
      log.warn("⚠️ Pinecone vector dimension mismatch. expected={}, actual={}", dimension, vector.size());
    }

    String url = host + "/query";   // Pinecone 최신 API endpoint

    // ★ namespace 제거 (2025 API에서 필수 변경사항)
    Map<String, Object> body = new HashMap<>();
    body.put("vector", vector);
    body.put("topK", topK);
    body.put("includeValues", false);
    body.put("includeMetadata", false);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Api-Key", apiKey);

    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

    try {
      ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

      if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
        log.error("❌ Pinecone query failed. status={}, body={}",
            response.getStatusCode(), response.getBody());
        throw new CustomException(RecommendationErrorCode.PINECONE_QUERY_FAILED);
      }

      JsonNode root = objectMapper.readTree(response.getBody());
      JsonNode matchesNode = root.get("matches");

      if (matchesNode == null || !matchesNode.isArray()) {
        log.warn("⚠️ Pinecone returned no matches");
        return List.of();
      }

      List<PineconeMatch> matches = new ArrayList<>();
      for (JsonNode node : matchesNode) {
        String id = node.get("id").asText();
        double score = node.get("score").asDouble();
        matches.add(new PineconeMatch(id, score));
      }

      return matches;

    } catch (Exception e) {
      log.error("❌ Pinecone query exception", e);
      throw new CustomException(RecommendationErrorCode.PINECONE_QUERY_FAILED);
    }
  }
}
