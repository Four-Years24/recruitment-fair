package com.recruitment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI recruitmentFairOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("校园招聘会管理系统 API")
                        .version("1.0.0")
                        .description("Spring Boot + MyBatis + JWT 认证")
                        .contact(new Contact()
                                .name("Four-Years24")
                                .url("https://github.com/Four-Years24/recruitment-fair")))
                .schemaRequirement("Bearer",
                        new SecurityScheme()
                                .name("Bearer")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("登录接口返回的 token，格式: Bearer <token>"));
    }
}
