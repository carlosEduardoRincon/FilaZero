package br.com.filazero.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${filazero.cors.origin-patterns:http://localhost:*,http://127.0.0.1:*}")
    private String originPatterns;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] patterns =
                Arrays.stream(originPatterns.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toArray(String[]::new);
        registry.addMapping("/**")
                .allowedOriginPatterns(patterns.length > 0 ? patterns : new String[] {"http://localhost:*"})
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
