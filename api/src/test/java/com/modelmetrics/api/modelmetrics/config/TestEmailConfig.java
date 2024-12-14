package com.modelmetrics.api.modelmetrics.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/** TestEmailConfig. */
@Configuration
@Profile("test")
public class TestEmailConfig {

  @Bean
  JavaMailSender javaMailSender() {
    return new JavaMailSenderImpl();
  }
}
