package com.ntnu.gidd.service.Email;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.ntnu.gidd.model.HtmlTemplate;
import com.ntnu.gidd.model.Mail;
import java.util.HashMap;
import java.util.Map;
import javax.mail.internet.MimeMessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mail.javamail.JavaMailSender;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class EmailServiceImplTest {

      @SpyBean
      private JavaMailSender mailSender;

      @Autowired
      @InjectMocks
      private EmailServiceImpl emailService;

      private Mail mail;

      @BeforeEach
      public void setUp() throws Exception {

            Map<String, Object> properties = new HashMap<>();
            properties.put("name", "Test Testesen");
            properties.put("activity", "Min test aktivitet");
            properties.put("url", "https://gidd-idatt2106.web.app/");

            mail = Mail.builder()
                  .from("baregidd@gmail.com")
                  .to("test@testersen.no")
                  .htmlTemplate(new HtmlTemplate("activity_closed", properties))
                  .subject("Activity closed")
                  .build();

            Mockito.doNothing().when(mailSender).send(any(MimeMessage.class));

      }

      @Test
      void testSendEmail() throws MessagingException {
            emailService.sendEmail(mail);
            verify(mailSender, times(1)).send(any(MimeMessage.class));
      }
}
