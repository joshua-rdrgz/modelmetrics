package com.modelmetrics.api.modelmetrics.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modelmetrics.api.modelmetrics.base.AuthTestBase;
import com.modelmetrics.api.modelmetrics.dto.session.EventDto;
import com.modelmetrics.api.modelmetrics.dto.session.SessionDto;
import com.modelmetrics.api.modelmetrics.entity.Event;
import com.modelmetrics.api.modelmetrics.entity.User;
import com.modelmetrics.api.modelmetrics.entity.session.Session;
import com.modelmetrics.api.modelmetrics.helper.session.EventType;
import com.modelmetrics.api.modelmetrics.repository.session.SessionRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/** SessionControllerTest. */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
public class SessionControllerTest extends AuthTestBase {

  @Autowired private SessionRepository sessionRepository;

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Nested
  @DisplayName("POST /api/v1/sessions")
  class CreateSession {

    @Test
    @DisplayName("Should create a session successfully")
    void shouldCreateSessionSuccessfully() throws Exception {
      SessionDto validSessionDto = createValidSessionDto();

      mockMvc
          .perform(
              post("/api/v1/sessions")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(validSessionDto))
                  .cookie(getTokenCookie()))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.data.id").exists());
    }

    @Test
    @DisplayName("Should return unauthorized when not authenticated")
    void shouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
      SessionDto validSessionDto = createValidSessionDto();

      mockMvc
          .perform(
              post("/api/v1/sessions")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(validSessionDto)))
          .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @MethodSource("invalidSessionDtoProvider")
    @DisplayName("Should return bad request for invalid session data")
    void shouldReturnBadRequestForInvalidSessionData(
        SessionDto invalidSessionDto, String fieldName, String expectedError) throws Exception {
      mockMvc
          .perform(
              post("/api/v1/sessions")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(invalidSessionDto))
                  .cookie(getTokenCookie()))
          .andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> invalidSessionDtoProvider() {
      return Stream.of(
          Arguments.of(new SessionDto(), "projectName", "Project name must not be null"),
          Arguments.of(
              SessionDto.builder().projectName("Test").build(),
              "hourlyRate",
              "Hourly rate must not be null"),
          Arguments.of(
              SessionDto.builder().projectName("Test").hourlyRate(BigDecimal.valueOf(50)).build(),
              "events",
              "Events list must not be empty"));
    }
  }

  @Nested
  @DisplayName("PUT /api/v1/sessions/{id}")
  class UpdateSession {

    @Test
    @DisplayName("Should update a session successfully")
    void shouldUpdateSessionSuccessfully() throws Exception {
      Session session = createAndSaveSession(getUser());
      SessionDto updatedSessionDto = createValidSessionDto();
      updatedSessionDto.setId(session.getId());

      mockMvc
          .perform(
              put("/api/v1/sessions/" + session.getId())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(updatedSessionDto))
                  .cookie(getTokenCookie()))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data.id").value(session.getId().toString()));
    }

    @Test
    @DisplayName("Should return forbidden when user is not authorized for session")
    void shouldReturnForbiddenWhenUserNotAuthorized() throws Exception {
      User otherUser = createAndSaveUser("other@example.com");
      Session session = createAndSaveSession(otherUser);
      SessionDto updatedSessionDto = createValidSessionDto();
      updatedSessionDto.setId(session.getId());

      mockMvc
          .perform(
              put("/api/v1/sessions/" + session.getId())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(updatedSessionDto))
                  .cookie(getTokenCookie()))
          .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return not found when session does not exist")
    void shouldReturnNotFoundWhenSessionDoesNotExist() throws Exception {
      UUID nonExistentId = UUID.randomUUID();
      SessionDto updatedSessionDto = createValidSessionDto();
      updatedSessionDto.setId(nonExistentId);

      mockMvc
          .perform(
              put("/api/v1/sessions/" + nonExistentId)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(updatedSessionDto))
                  .cookie(getTokenCookie()))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("DELETE /api/v1/sessions/{id}")
  class DeleteSession {

    @Test
    @DisplayName("Should delete a session successfully")
    void shouldDeleteSessionSuccessfully() throws Exception {
      Session session = createAndSaveSession(getUser());

      mockMvc
          .perform(delete("/api/v1/sessions/" + session.getId()).cookie(getTokenCookie()))
          .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return forbidden when user is not authorized for session")
    void shouldReturnForbiddenWhenUserNotAuthorized() throws Exception {
      User otherUser = createAndSaveUser("other@example.com");
      Session session = createAndSaveSession(otherUser);

      mockMvc
          .perform(delete("/api/v1/sessions/" + session.getId()).cookie(getTokenCookie()))
          .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return not found when session does not exist")
    void shouldReturnNotFoundWhenSessionDoesNotExist() throws Exception {
      UUID nonExistentId = UUID.randomUUID();

      mockMvc
          .perform(delete("/api/v1/sessions/" + nonExistentId).cookie(getTokenCookie()))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("GET /api/v1/sessions/{id}")
  class GetSessionById {

    @Test
    @DisplayName("Should get a session successfully")
    void shouldGetSessionSuccessfully() throws Exception {
      Session session = createAndSaveSession(getUser());

      mockMvc
          .perform(get("/api/v1/sessions/" + session.getId()).cookie(getTokenCookie()))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data.id").value(session.getId().toString()));
    }

    @Test
    @DisplayName("Should return forbidden when user is not authorized for session")
    void shouldReturnForbiddenWhenUserNotAuthorized() throws Exception {
      User otherUser = createAndSaveUser("other@example.com");
      Session session = createAndSaveSession(otherUser);

      mockMvc
          .perform(get("/api/v1/sessions/" + session.getId()).cookie(getTokenCookie()))
          .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return not found when session does not exist")
    void shouldReturnNotFoundWhenSessionDoesNotExist() throws Exception {
      UUID nonExistentId = UUID.randomUUID();

      mockMvc
          .perform(get("/api/v1/sessions/" + nonExistentId).cookie(getTokenCookie()))
          .andExpect(status().isNotFound());
    }
  }

  private SessionDto createValidSessionDto() {
    List<EventDto> events =
        Arrays.asList(
            new EventDto(EventType.START.name(), 1000L),
            new EventDto(EventType.BREAK.name(), 2000L),
            new EventDto(EventType.RESUME.name(), 3000L),
            new EventDto(EventType.TASKCOMPLETE.name(), 4000L),
            new EventDto(EventType.FINISH.name(), 5000L));

    return SessionDto.builder()
        .projectName("Test Project")
        .hourlyRate(new BigDecimal("50.00"))
        .events(events)
        .build();
  }

  private Session createAndSaveSession(User owner) {
    Session session =
        Session.builder()
            .projectName("Test Project")
            .hourlyRate(new BigDecimal("50.00"))
            .user(owner)
            .events(new ArrayList<>())
            .build();

    session
        .getEvents()
        .addAll(
            Arrays.asList(
                Event.builder().type(EventType.START).timestamp(1000L).session(session).build(),
                Event.builder().type(EventType.BREAK).timestamp(2000L).session(session).build(),
                Event.builder().type(EventType.RESUME).timestamp(3000L).session(session).build(),
                Event.builder()
                    .type(EventType.TASKCOMPLETE)
                    .timestamp(4000L)
                    .session(session)
                    .build(),
                Event.builder().type(EventType.FINISH).timestamp(5000L).session(session).build()));

    return sessionRepository.save(session);
  }
}
