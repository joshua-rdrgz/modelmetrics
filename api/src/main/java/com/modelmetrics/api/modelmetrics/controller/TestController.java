package com.modelmetrics.api.modelmetrics.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** TestController. */
@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class TestController {

  @GetMapping
  public String testEndpoint() {
    return "this is a protected test endpoint.  You made it!";
  }

  @GetMapping("/public")
  public String publicTestEndpoint() {
    return "this is a public test endpoint.";
  }
}
