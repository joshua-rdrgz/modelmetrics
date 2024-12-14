package com.modelmetrics.api.modelmetrics.base;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/** EmailTestBase. */
@SpringBootTest
public abstract class EmailTestBase {

  protected GreenMail greenMail;

  @Autowired private JavaMailSenderImpl mailSender;

  /** initializeEmail. */
  @BeforeEach
  public void initializeEmail() {
    greenMail = new GreenMail(ServerSetupTest.SMTP);
    greenMail.setUser("test@example.com", "password");
    greenMail.start();

    // Configure JavaMailSender for GreenMail
    mailSender.setHost("localhost");
    mailSender.setPort(greenMail.getSmtp().getPort());
    mailSender.setUsername("test@example.com");
    mailSender.setPassword("password");

    // Enable authentication
    mailSender.getJavaMailProperties().setProperty("mail.smtp.auth", "true");
  }

  @AfterEach
  public void tearDownEmail() {
    greenMail.stop();
  }
}
