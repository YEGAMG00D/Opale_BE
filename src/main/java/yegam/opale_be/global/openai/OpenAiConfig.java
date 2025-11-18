package yegam.opale_be.global.openai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;

@Configuration
public class OpenAiConfig {

  @Value("${openai.api-key}")
  private String apiKey;

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder
        .additionalInterceptors((request, body, execution) -> {
          request.getHeaders().add("Authorization", "Bearer " + apiKey);
          request.getHeaders().add("Content-Type", "application/json");
          return execution.execute(request, body);
        })
        .build();
  }
}
