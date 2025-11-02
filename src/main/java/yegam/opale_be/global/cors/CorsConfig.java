package yegam.opale_be.global.cors;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class CorsConfig {

  private final CorsProperties corsProperties;

  @Bean
  public UrlBasedCorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    configuration.setAllowedOrigins(corsProperties.getAllowedOrigins());
    configuration.setAllowedMethods(corsProperties.getAllowedMethods());
    configuration.setAllowedHeaders(corsProperties.getAllowedHeaders());
    configuration.setAllowCredentials(corsProperties.isAllowCredentials());
    configuration.setExposedHeaders(corsProperties.getAllowedHeaders());

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
