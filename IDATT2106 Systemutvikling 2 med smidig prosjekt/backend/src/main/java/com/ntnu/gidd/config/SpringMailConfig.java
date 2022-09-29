package com.ntnu.gidd.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.io.IOException;
import java.util.Properties;

@Configuration
public class SpringMailConfig{

      private static final String JAVA_MAIL_FILE = "classpath:mail/javamail.properties";

      @Autowired
      private ApplicationContext applicationContext;

      @Value("${spring.mail.host}")
      private String EMAIL_HOST;

      @Value("${spring.mail.port}")
      private int EMAIL_PORT;

      @Value("${spring.mail.username}")
      private String EMAIL_USERNAME;

      @Value("${spring.mail.password}")
      private String EMAIL_PASSWORD;


      @Bean
      public JavaMailSender mailSender() throws IOException {

            final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

            mailSender.setHost(EMAIL_HOST);
            mailSender.setPort(EMAIL_PORT);
            mailSender.setUsername(EMAIL_USERNAME);
            mailSender.setPassword(EMAIL_PASSWORD);

            final Properties javaMailProperties = new Properties();
            javaMailProperties.load(this.applicationContext.getResource(JAVA_MAIL_FILE).getInputStream());
            mailSender.setJavaMailProperties(javaMailProperties);

            return mailSender;
      }
}
