package com.modelmetrics.api.modelmetrics.dto.session;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.modelmetrics.api.modelmetrics.util.Money;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** SessionSummaryDto. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionSummaryDto {
  private UUID id;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private LocalDate date;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String projectName;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private BigDecimal hourlyRate;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Integer tasksCompleted;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private BigDecimal totalMinutesWorked;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Money grossEarnings;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Money taxAllocation;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Money netEarnings;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<EventDto> events;
}
