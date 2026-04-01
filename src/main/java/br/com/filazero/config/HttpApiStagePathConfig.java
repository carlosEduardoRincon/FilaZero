package br.com.filazero.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class HttpApiStagePathConfig implements WebMvcConfigurer {

  @Value("${filazero.http-api.stage-prefix:}")
  private String stagePrefix;

  @Override
  public void configurePathMatch(PathMatchConfigurer configurer) {
    if (!StringUtils.hasText(stagePrefix)) {
      return;
    }
    String prefix = stagePrefix.startsWith("/") ? stagePrefix : "/" + stagePrefix;
    configurer.addPathPrefix(prefix, HandlerTypePredicate.forAnnotation(RestController.class));
  }
}
