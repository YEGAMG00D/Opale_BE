package yegam.opale_be.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Value("${server.servlet.context-path:}")
  private String contextPath;

  @Bean
  public OpenAPI customOpenAPI() {
    Server localServer = new Server();
    localServer.setUrl(contextPath);
    localServer.setDescription("LocalServer");

    return new OpenAPI()
        .addServersItem(localServer)
        .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
        .components(new Components().addSecuritySchemes(
            "bearerAuth",
            new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
        ))
        .info(new Info()
            .title("Yegam : Opale - API 명세서")
            .version("1.0")
            .description("""
                ⚪ 공연 정보 공유 플랫폼: 회원 인증 관련 API
                
                ● Authorization 가이드
                - 로그인 후 발급받은 AccessToken을 전역 Authorize 창에 입력하세요.
                - 입력 시 'Bearer '를 제외한 순수 토큰 값만 입력 (예: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...)
                - AccessToken 만료 시 /api/auth/refresh 호출 시에는 헤더에 Refresh-Token을 추가하세요.
                - RefreshToken은 로그인 응답에서 받은 refreshToken을 그대로 사용합니다.
                """));
  }

  @Bean
  public GroupedOpenApi customGroupedOpenApi() {
    return GroupedOpenApi.builder()
        .group("api")
        .pathsToMatch("/**")
        .build();
  }
}
