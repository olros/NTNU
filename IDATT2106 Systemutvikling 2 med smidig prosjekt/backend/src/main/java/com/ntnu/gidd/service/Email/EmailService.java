package com.ntnu.gidd.service.Email;

import com.ntnu.gidd.model.Mail;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

@Service
public interface EmailService{
      void sendEmail(Mail mail) throws MessagingException;
      void sendEmail(String from, String to, String subject, String message) throws MessagingException;
}
