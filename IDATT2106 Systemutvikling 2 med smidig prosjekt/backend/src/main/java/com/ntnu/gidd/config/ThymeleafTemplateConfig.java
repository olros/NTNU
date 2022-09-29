package com.ntnu.gidd.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;


@Configuration
public class ThymeleafTemplateConfig {

      @Bean
      public SpringTemplateEngine springTemplateEngine() {
            SpringTemplateEngine templateEngine = new SpringTemplateEngine();
            templateEngine.setTemplateResolver(thymeleafTemplateResolver());
            return templateEngine;
      }

      @Bean
      public SpringResourceTemplateResolver thymeleafTemplateResolver(){
            SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
            templateResolver.setPrefix("classpath:/mail/templates/");
            templateResolver.setSuffix(".html");
            templateResolver.setTemplateMode("HTML");
            return templateResolver;
      }

      @Bean
      public ThymeleafViewResolver thymeleafViewResolver() {
            ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
            viewResolver.setTemplateEngine(springTemplateEngine());
            return viewResolver;
      }
}
