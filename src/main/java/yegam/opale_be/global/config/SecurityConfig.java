package yegam.opale_be.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import yegam.opale_be.global.security.JwtAuthenticationFilter;
import yegam.opale_be.global.security.handler.CustomAccessDeniedHandler;
import yegam.opale_be.global.security.handler.CustomAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final UrlBasedCorsConfigurationSource corsConfigurationSource;
  private final CustomAccessDeniedHandler accessDeniedHandler;
  private final CustomAuthenticationEntryPoint authenticationEntryPoint;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource))
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            // 1) OPTIONS 허용 → multipart preflight 문제 해결
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

            // 2) OCR 엔드포인트 허용 → Swagger/Postman 테스트 가능
            .requestMatchers(HttpMethod.POST, "/api/reservations/ocr").permitAll()


            // Swagger, 공개 엔드포인트
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
            .requestMatchers(
                "/api/auth/login",
                "/api/users",
                "/api/users/check-duplicate",
                "/api/users/check-nickname",
                "/api/email/send",
                "/api/email/verify",
                "/health",
                "/api/performances/**",
                "/api/places/**",
                "/api/discounts/**",
                "/ws/**"
            ).permitAll()

            // 공연 상세 수집 이미지, 수집 영상
            .requestMatchers("/api/admin/performances/**").permitAll()

            // 배너 관련
            .requestMatchers(
                "/api/admin/banners/**",
                "/api/banners/main",
                "/api/main-performance-banners",
                "/api/admin/main-performance-banners/**"
            ).permitAll()
            
            // 임시 비밀번호 발급
            .requestMatchers(HttpMethod.POST, "/api/users/password/reset").permitAll()

            // 공연 리뷰 공개 엔드포인트 (비로그인 가능)
            .requestMatchers(HttpMethod.GET, "/api/reviews/performances/{reviewId}").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/reviews/performances/performance/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/reviews/performances/user/**").permitAll()

            // 공연장 리뷰 공개 엔드포인트 (비로그인 가능)
            .requestMatchers(HttpMethod.GET, "/api/reviews/places/{reviewId}").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/reviews/places/place/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/reviews/places/user/**").permitAll()

            // 관심(좋아요) 조회(GET)은 비로그인 허용
            .requestMatchers(HttpMethod.GET, "/api/favorites/**").permitAll()

            // 오픈 채팅방(public) + 메시지 조회 허용
            .requestMatchers(HttpMethod.GET, "/api/chat/rooms/public/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/chat/rooms/public/performance/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/chat/messages/**").permitAll()

            // 채팅방 목록(GET)도 허용
            .requestMatchers(HttpMethod.GET, "/api/chat/rooms").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/chat/rooms/search").permitAll()

            // 추천 API (비로그인 허용)
            .requestMatchers(HttpMethod.GET,
                "/api/recommendations/popular",
                "/api/recommendations/latest",
                "/api/recommendations/genre",
                "/api/recommendations/popular/places",
                "/api/recommendations/popular/chatrooms",
                "/api/recommendations/performance/**"
            ).permitAll()

            // 개인화 추천은 로그인 필요
            .requestMatchers(HttpMethod.GET,
                "/api/recommendations/user",
                "/api/recommendations/user/**"
            ).authenticated()


            // 나머지 채팅 관련 요청은 로그인 필요
            .requestMatchers("/api/chat/**").authenticated()

            // 나머지 요청도 기본적으로 인증 필요
            .anyRequest().authenticated()
        )

        // 인증 & 인가 실패 핸들러 연결
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(authenticationEntryPoint) // 401
            .accessDeniedHandler(accessDeniedHandler) // 403
        )

        // JWT 필터 등록
        .addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }
}
