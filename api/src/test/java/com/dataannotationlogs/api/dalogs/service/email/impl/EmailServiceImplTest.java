package com.dataannotationlogs.api.dalogs.service.email.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.dataannotationlogs.api.dalogs.base.EmailTestBase;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.modelmetrics.api.modelmetrics.entity.User;
import com.modelmetrics.api.modelmetrics.service.email.EmailService;
import jakarta.mail.internet.MimeMessage;
import java.util.UUID;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/** EmailServiceImplTest. */
@SpringBootTest
@ActiveProfiles("test")
public class EmailServiceImplTest extends EmailTestBase {

  @Autowired private EmailService emailService;

  @Value("${app.frontend.url}")
  private String frontendUrl;

  @Test
  public void testSendAccountVerificationEmail() throws Exception {
    User user = new User();
    user.setId(UUID.randomUUID());
    user.setEmail("test@example.com");
    String token = "testToken";

    emailService.sendAccountVerificationEmail(user, token);

    MimeMessage receivedMessage = greenMail.getReceivedMessages()[0];
    Document doc = Jsoup.parse(GreenMailUtil.getBody(receivedMessage));

    assertThat(receivedMessage.getSubject()).isEqualTo("Thanks for joining!");
    assertThat(doc.select("h1").first().text()).isEqualTo("Thanks for joining!");
    assertThat(doc.select("a").first().attr("href"))
        .contains(frontendUrl + "/verify")
        .contains("token=" + token)
        .contains("userId=" + user.getId());
  }

  @Test
  public void testSendEmailChangeEmail() throws Exception {
    String email = "newemail@example.com";
    String token = "testToken";
    UUID userId = UUID.randomUUID();

    emailService.sendEmailChangeEmail(email, token, userId);

    MimeMessage receivedMessage = greenMail.getReceivedMessages()[0];
    Document doc = Jsoup.parse(GreenMailUtil.getBody(receivedMessage));

    assertThat(receivedMessage.getSubject()).isEqualTo("Change Your Email Address");
    assertThat(doc.select("h1").first().text()).isEqualTo("Change Your Email Address");
    assertThat(doc.select("a").first().attr("href"))
        .contains(frontendUrl + "/verify-email-change")
        .contains("token=" + token)
        .contains("userId=" + userId);
  }

  @Test
  public void testSendPasswordResetOtpEmail() throws Exception {
    String email = "test@example.com";
    String otp = "123456";

    emailService.sendPasswordResetOtpEmail(email, otp);

    MimeMessage receivedMessage = greenMail.getReceivedMessages()[0];
    Document doc = Jsoup.parse(GreenMailUtil.getBody(receivedMessage));

    assertThat(receivedMessage.getSubject()).isEqualTo("Your Password Reset Code: " + otp);
    assertThat(doc.select("h1").first().text()).isEqualTo("Your Password Reset Code: " + otp);
    assertThat(doc.select("h2").first().text()).isEqualTo(otp);
  }

  @Test
  public void testSendAccountDeletedEmail() throws Exception {
    String email = "test@example.com";

    emailService.sendAccountDeletedEmail(email);

    MimeMessage receivedMessage = greenMail.getReceivedMessages()[0];
    Document doc = Jsoup.parse(GreenMailUtil.getBody(receivedMessage));

    assertThat(receivedMessage.getSubject()).isEqualTo("Account Deleted");
    assertThat(doc.select("h1").first().text()).isEqualTo("Account Deleted");
    assertThat(doc.select("a").first().attr("href")).isEqualTo(frontendUrl + "/register");
  }

  @Test
  public void testSendAccountVerificationReminderEmail() throws Exception {
    String email = "test@example.com";

    emailService.sendAccountVerificationReminderEmail(email);

    MimeMessage receivedMessage = greenMail.getReceivedMessages()[0];
    Document doc = Jsoup.parse(GreenMailUtil.getBody(receivedMessage));

    assertThat(receivedMessage.getSubject()).isEqualTo("Account Verification Reminder");
    assertThat(doc.select("h1").first().text()).isEqualTo("Account Verification Reminder");
    assertThat(doc.select("a").first().attr("href"))
        .isEqualTo(frontendUrl + "/resend-verification");
  }
}
