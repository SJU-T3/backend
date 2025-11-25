package com.example.demo.config;
import com.example.demo.auth.jwt.JwtAuthenticationFilter;
import com.example.demo.auth.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring Security 설정 클래스입니다.
 * JWT 기반 인증을 사용하며, CSRF 비활성화, 세션 비활성화, CORS 설정,
 * 그리고 JWT 필터 등록 및 경로별 권한 설정을 담당합니다.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // CORS는 아래 'corsConfigurer' Bean에서 설정하므로, 여기서는 기본 설정만 활성화
                .cors(cors -> {})
                // 1. JWT 기반 인증이므로 CSRF 보호 비활성화
                .csrf(csrf -> csrf.disable())
                // 2. HTTP Basic 인증, Form Login 비활성화
                .httpBasic( httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable())
                // 3. 세션 대신 JWT를 사용하므로 세션을 STATELESS로 설정
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 4. 요청별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 카카오 인증 및 모든 Swagger 경로는 인증 없이 접근 허용
                        .requestMatchers("/auth/**").permitAll()
                        // Swagger 관련 경로 모두 허용
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
                        // 그 외 나머지 요청은 인증 필수
                        .anyRequest().authenticated()
                )

                // 5. JWT 필터 등록: UsernamePasswordAuthenticationFilter 이전에 실행
                .addFilterBefore(
                        // JwtTokenProvider를 사용하여 필터 인스턴스를 생성
                        new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    /**
     * CORS (Cross-Origin Resource Sharing) 설정을 위한 Bean입니다.
     * 프론트엔드 환경에서 백엔드 API에 접근할 수 있도록 허용합니다.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // 모든 경로에 대해
                        .allowedOrigins(
                                "http://localhost:3000",          // 로컬 개발
                                "https://nottoday-front.vercel.app", // 프론트 배포 주소 (예시)
                                "http://13.125.17.236",           // 운영 서버 IP
                                "http://13.125.17.236:8080"       // 운영 서버 IP:포트
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        // 인증 정보를 포함한 요청(JWT, 쿠키 등)을 허용
                        .allowCredentials(true);
            }
        };
    }
}