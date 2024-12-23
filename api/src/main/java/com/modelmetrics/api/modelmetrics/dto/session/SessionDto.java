package com.modelmetrics.api.modelmetrics.dto.session;

import com.modelmetrics.api.modelmetrics.annotations.valideventorder.ValidEventOrder;
import com.modelmetrics.api.modelmetrics.util.Money;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** SessionDto. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionDto {
  private UUID id;

  @NotNull(message = "Project name must not be null")
  private String projectName;

  @NotNull(message = "Hourly rate must not be null")
  @Positive
  private BigDecimal hourlyRate;

  @Valid
  @NotEmpty(message = "Events list must not be empty")
  @ValidEventOrder
  private List<EventDto> events;

  // Calculated properties
  private Integer tasksCompleted;
  private BigDecimal totalMinutesWorked;
  private Money grossEarnings;
  private Money taxAllocation;
  private Money netEarnings;
}
