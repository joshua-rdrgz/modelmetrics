package com.dataannotationlogs.api.dalogs.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dataannotationlogs.api.dalogs.base.AuthTestBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modelmetrics.api.modelmetrics.dto.user.UserDto;
import com.modelmetrics.api.modelmetrics.entity.EmailResetToken;
import com.modelmetrics.api.modelmetrics.repository.emailresettoken.EmailResetTokenRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

/** UserControllerMeEndpointTest. */
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerMeEndpointTest extends AuthTestBase {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private EmailResetTokenRepository emailResetTokenRepository;
  @Autowired private PasswordEncoder passwordEncoder;

  @Test
  void getCurrentUser_whenLoggedIn_shouldReturnCurrentUser() throws Exception {
    MvcResult result =
        mockMvc
            .perform(get("/api/v1/users/me").cookie(getTokenCookie()))
            .andExpect(status().isOk())
            .andReturn();

    UserDto userDto =
        objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
    assertEquals(getUser().getFirstName(), userDto.getFirstName());
    assertEquals(getUser().getLastName(), userDto.getLastName());
    assertEquals(getUser().getEmail(), userDto.getEmail());
  }

  @Test
  void getCurrentUser_whenEmailResetTokenExists_shouldReturnUserWithPendingChange()
      throws Exception {
    String newEmail = "newemail@example.com";
    EmailResetToken emailResetToken =
        EmailResetToken.builder()
            .user(getUser())
            .token(passwordEncoder.encode("token"))
            .newEmail(newEmail)
            .expiryDate(LocalDateTime.now().plusMinutes(30))
            .build();
    emailResetTokenRepository.save(emailResetToken);

    MvcResult result =
        mockMvc
            .perform(get("/api/v1/users/me").cookie(getTokenCookie()))
            .andExpect(status().isOk())
            .andReturn();

    UserDto userDto =
        objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
    assertEquals(getUser().getFirstName(), userDto.getFirstName());
    assertEquals(getUser().getLastName(), userDto.getLastName());
    assertEquals(getUser().getEmail(), userDto.getEmail());
    assertEquals(true, userDto.isPendingEmailChange());
    assertEquals(newEmail, userDto.getPendingEmail());
  }

  @Test
  void getCurrentUser_whenNoEmailResetTokenExists_shouldReturnUserWithoutPendingChange()
      throws Exception {
    MvcResult result =
        mockMvc
            .perform(get("/api/v1/users/me").cookie(getTokenCookie()))
            .andExpect(status().isOk())
            .andReturn();

    UserDto userDto =
        objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
    assertEquals(getUser().getFirstName(), userDto.getFirstName());
    assertEquals(getUser().getLastName(), userDto.getLastName());
    assertEquals(getUser().getEmail(), userDto.getEmail());
    assertEquals(false, userDto.isPendingEmailChange());
    assertEquals(null, userDto.getPendingEmail());
  }

  @Test
  void updateCurrentUser_whenLoggedIn_shouldUpdateUser() throws Exception {
    UserDto userDto = UserDto.builder().firstName("Jane").lastName("Doe").build();

    MvcResult result =
        mockMvc
            .perform(
                put("/api/v1/users/me")
                    .cookie(getTokenCookie())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userDto)))
            .andExpect(status().isOk())
            .andReturn();

    UserDto updatedUserDto =
        objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
    assertEquals(userDto.getFirstName(), updatedUserDto.getFirstName());
    assertEquals(userDto.getLastName(), updatedUserDto.getLastName());
  }

  @Test
  void updateCurrentUser_whenEmailIsProvided_shouldNotUpdateEmail() throws Exception {
    UserDto userDto = UserDto.builder().email("jane@example.com").build();

    mockMvc
        .perform(
            put("/api/v1/users/me")
                .cookie(getTokenCookie())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateCurrentUser_whenNoEmailResetTokenExists_shouldReturnUserWithoutPendingChange()
      throws Exception {
    UserDto userDto = UserDto.builder().firstName("Jane").lastName("Doe").build();

    MvcResult result =
        mockMvc
            .perform(
                put("/api/v1/users/me")
                    .cookie(getTokenCookie())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userDto)))
            .andExpect(status().isOk())
            .andReturn();

    UserDto updatedUserDto =
        objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
    assertEquals(userDto.getFirstName(), updatedUserDto.getFirstName());
    assertEquals(userDto.getLastName(), updatedUserDto.getLastName());
    assertFalse(updatedUserDto.isPendingEmailChange());
    assertNull(updatedUserDto.getPendingEmail());
  }

  @Test
  void updateCurrentUser_whenEmailResetTokenExists_shouldReturnUserWithPendingChange()
      throws Exception {
    String newEmail = "newemail@example.com";
    EmailResetToken emailResetToken =
        EmailResetToken.builder()
            .user(getUser())
            .token(passwordEncoder.encode("token"))
            .newEmail(newEmail)
            .expiryDate(LocalDateTime.now().plusMinutes(30))
            .build();
    emailResetTokenRepository.save(emailResetToken);

    UserDto userDto = UserDto.builder().firstName("Jane").lastName("Doe").build();

    MvcResult result =
        mockMvc
            .perform(
                put("/api/v1/users/me")
                    .cookie(getTokenCookie())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userDto)))
            .andExpect(status().isOk())
            .andReturn();

    UserDto updatedUserDto =
        objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
    assertEquals(userDto.getFirstName(), updatedUserDto.getFirstName());
    assertEquals(userDto.getLastName(), updatedUserDto.getLastName());
    assertEquals(true, updatedUserDto.isPendingEmailChange());
    assertEquals(newEmail, updatedUserDto.getPendingEmail());
  }
}
