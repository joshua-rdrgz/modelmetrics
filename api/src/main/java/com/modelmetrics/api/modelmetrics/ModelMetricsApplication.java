package com.modelmetrics.api.modelmetrics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/** DataAnnotationLogsApplication. */
@SpringBootApplication
@EnableScheduling
public class ModelMetricsApplication {

  public static void main(String[] args) {
    SpringApplication.run(ModelMetricsApplication.class, args);
  }
}
