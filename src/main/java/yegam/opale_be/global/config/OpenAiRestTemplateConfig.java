package yegam.opale_be.global.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class OpenAiRestTemplateConfig {

  @Value("${openai.api-key}")
  private String apiKey;

  @Value("${openai.project-id}")
  private String projectId;


  @Bean(name = "openAiRestTemplate")
  public RestTemplate openAiRestTemplate(RestTemplateBuilder builder) {

    ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
      request.getHeaders().add("Authorization", "Bearer " + apiKey);
//      request.getHeaders().add("OpenAI-Project", projectId);
//      request.getHeaders().add("Content-Type", "application/json");
      return execution.execute(request, body);
    };

    return builder
        .additionalInterceptors(interceptor)
        .build();
  }
}
