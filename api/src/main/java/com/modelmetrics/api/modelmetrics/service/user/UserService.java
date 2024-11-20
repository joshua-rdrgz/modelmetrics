package com.modelmetrics.api.modelmetrics.service.user;

import com.modelmetrics.api.modelmetrics.dto.email.EmailResetRequest;
import com.modelmetrics.api.modelmetrics.dto.email.EmailResetVerificationRequest;
import com.modelmetrics.api.modelmetrics.dto.password.PasswordChangeRequest;
import com.modelmetrics.api.modelmetrics.dto.password.PasswordResetVerifyRequest;
import com.modelmetrics.api.modelmetrics.dto.response.EntityChangeResponse;
import com.modelmetrics.api.modelmetrics.dto.user.UserDto;
import com.modelmetrics.api.modelmetrics.entity.User;

/** UserService. */
public interface UserService {

  UserDto getCurrentUser(User user);

  UserDto updateCurrentUser(User user, UserDto userDto);

  EntityChangeResponse sendEmailResetToken(User user, EmailResetRequest emailReset);

  EntityChangeResponse changeEmail(EmailResetVerificationRequest emailResetVerification);

  EntityChangeResponse cancelEmailResetToken(User user);

  EntityChangeResponse sendPasswordResetOtp(User user);

  EntityChangeResponse verifyPasswordResetOtp(User user, PasswordResetVerifyRequest request);

  EntityChangeResponse changePassword(User user, PasswordChangeRequest request);
}
