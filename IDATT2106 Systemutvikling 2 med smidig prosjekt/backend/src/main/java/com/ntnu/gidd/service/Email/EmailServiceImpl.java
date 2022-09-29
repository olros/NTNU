package com.ntnu.gidd.service.Email;

import com.ntnu.gidd.model.Mail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Implementation of a email service
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

      @Autowired
      private JavaMailSender mailSender;

      @Autowired
      private SpringTemplateEngine templateEngine;

      /**
       * Method to send a mail
       * @param from
       * @param to
       * @param subject
       * @param content
       * @throws MessagingException
       */
      @Override
      public void sendEmail(String from, String to, String subject, String content) throws MessagingException {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            helper.setFrom(from);
            helper.setTo(to);
            helper.setText(content);
            helper.setSubject(subject);
            this.mailSender.send(mimeMessage);
      }

      /**
       * Method to send a mail by a mail object
       * @param mail
       * @throws MessagingException
       */
      @Override
      public void sendEmail(Mail mail) throws MessagingException {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

            String html = getHtmlContent(mail);

            helper.setTo(mail.getTo());
            helper.setFrom(mail.getFrom());
            helper.setSubject(mail.getSubject());
            helper.setText(html, true);

            mailSender.send(message);
      }

      /**
       * Helper method to get the html styling for the email
       * @param mail
       * @return
       */
      private String getHtmlContent(Mail mail) {
            Context context = new Context();
            context.setVariables(mail.getHtmlTemplate().getProps());
            return templateEngine.process(mail.getHtmlTemplate().getTemplate(), context);
      }
}
