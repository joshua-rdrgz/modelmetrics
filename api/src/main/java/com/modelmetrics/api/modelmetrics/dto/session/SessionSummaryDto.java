package com.modelmetrics.api.modelmetrics.dto.session;

import com.modelmetrics.api.modelmetrics.util.Money;
import java.time.LocalDate;
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
  private LocalDate date;
  private String projectName;
  private Money grossEarnings;
}
