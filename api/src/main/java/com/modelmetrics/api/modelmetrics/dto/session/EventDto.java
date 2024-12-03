package com.modelmetrics.api.modelmetrics.dto.session;

import com.modelmetrics.api.modelmetrics.annotations.valideventtype.ValidEventType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** EventDto. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {

  @NotNull(message = "Event type must not be null")
  @ValidEventType(message = "Invalid event type")
  private String type;

  @NotNull(message = "Event timestamp must not be null")
  @Positive
  private Long timestamp;
}
