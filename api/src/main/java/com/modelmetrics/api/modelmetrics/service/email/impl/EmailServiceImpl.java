package com.modelmetrics.api.modelmetrics.service.email.impl;

import com.modelmetrics.api.modelmetrics.entity.User;
import com.modelmetrics.api.modelmetrics.service.email.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/** EmailServiceImpl. */
@Service
public class EmailServiceImpl implements EmailService {

  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

  @Value("${app.frontend.url}")
  private String frontendUrl;

  public EmailServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine) {
    this.mailSender = mailSender;
    this.templateEngine = templateEngine;
  }

  @Override
  public void sendAccountVerificationEmail(User user, String token) {
    Context context = new Context();
    context.setVariable(
        "verificationUrl", frontendUrl + "/verify?token=" + token + "&userId=" + user.getId());

    String htmlContent = processTemplate("email/account-verification", context);
    sendHtmlMessage(user.getEmail(), "Thanks for joining!", htmlContent);
  }

  @Override
  public void sendEmailChangeEmail(String email, String token, UUID userId) {
    Context context = new Context();
    context.setVariable(
        "changeUrl", frontendUrl + "/verify-email-change?token=" + token + "&userId=" + userId);

    String htmlContent = processTemplate("email/email-change", context);
    sendHtmlMessage(email, "Change Your Email Address", htmlContent);
  }

  @Override
  public void sendPasswordResetOtpEmail(String email, String otp) {
    Context context = new Context();
    context.setVariable("otp", otp);

    String htmlContent = processTemplate("email/password-reset", context);
    sendHtmlMessage(email, "Your Password Reset Code: " + otp, htmlContent);
  }

  @Override
  public void sendAccountDeletedEmail(String email) {
    Context context = new Context();
    context.setVariable("frontendUrl", frontendUrl);

    String htmlContent = processTemplate("email/account-deleted", context);
    sendHtmlMessage(email, "Account Deleted", htmlContent);
  }

  @Override
  public void sendAccountVerificationReminderEmail(String email) {
    Context context = new Context();
    context.setVariable("frontendUrl", frontendUrl);

    String htmlContent = processTemplate("email/account-verification-reminder", context);
    sendHtmlMessage(email, "Account Verification Reminder", htmlContent);
  }

  private String processTemplate(String templateName, Context context) {
    return templateEngine.process(templateName, context);
  }

  private void sendHtmlMessage(String to, String subject, String htmlContent) {
    MimeMessage message = mailSender.createMimeMessage();
    try {
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(htmlContent, true);

      ClassPathResource logoResource = new ClassPathResource("static/images/logo-black.svg");
      helper.addInline("logo", logoResource);

      mailSender.send(message);
    } catch (MessagingException e) {
      System.out.println("Error sending email: " + e.getMessage());
    }
  }
}
