package com.modelmetrics.api.modelmetrics.dto.session;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** SessionCaptureDto. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionCaptureDto {

  @NotNull(message = "Project name must not be null")
  private String projectName;

  @NotNull(message = "Hourly rate must not be null")
  @Positive
  private Double hourlyRate;

  @Valid
  @NotEmpty(message = "Events list must not be empty")
  private List<EventDto> events;
}
