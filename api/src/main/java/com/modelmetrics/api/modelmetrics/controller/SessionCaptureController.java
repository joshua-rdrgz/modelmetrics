package com.modelmetrics.api.modelmetrics.controller;

import com.modelmetrics.api.modelmetrics.dto.session.SessionCaptureDto;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** SessionCaptureController. */
@RestController
@RequestMapping("/api/v1/session")
public class SessionCaptureController {

  private static final Logger logger = LoggerFactory.getLogger(SessionCaptureController.class);

  @PostMapping("/capture")
  public void captureSessionData(@Valid @RequestBody SessionCaptureDto sessionData) {
    // Log the received session data
    logger.info("Received session data: {}", sessionData);
  }
}
