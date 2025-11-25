package com.example.demo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import java.util.List;

/**
 * Springdoc OpenAPI (Swagger) 설정을 위한 Configuration 클래스입니다.
 * JWT 인증 스키마와 다중 서버 URL을 포함하도록 설정합니다.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI(){
        // 1. JWT Bearer Token 인증 방식을 정의하는 보안 스키마를 설정합니다.
        SecurityScheme bearerTokenScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP) // HTTP 기반 인증
                .scheme("bearer")               // 스키마 타입: bearer
                .bearerFormat("JWT")            // 포맷: JWT (JSON Web Token)
                .in(SecurityScheme.In.HEADER)   // 토큰을 HTTP 헤더를 통해 전달
                .name(HttpHeaders.AUTHORIZATION); // 헤더 이름: Authorization

        // 2. 서버 환경(로컬 및 운영)을 정의합니다.
        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("로컬 개발 서버");
        Server prodServer = new Server()
                // 두 번째 코드에서 지정했던 운영 서버 URL을 반영했습니다.
                .url("http://13.125.17.236:8080")
                .description("운영 (Production) 서버");

        // 3. 최종 OpenAPI 객체를 생성하고, 정보, 보안 스키마, 서버 목록을 추가합니다.
        return new OpenAPI()
                .info(new Info()
                        .title("NotToday") // API 제목 설정
                        .version("1.0")
                        .description("카카오 소셜 로그인 기반의 소비 지연 가계부 웹/앱"))
                .components(new Components()
                        // 정의한 보안 스키마를 "bearerAuth"라는 이름으로 Components에 추가
                        .addSecuritySchemes("bearerAuth", bearerTokenScheme))
                // 서버 목록 추가
                .servers(List.of(localServer, prodServer));
    }
}