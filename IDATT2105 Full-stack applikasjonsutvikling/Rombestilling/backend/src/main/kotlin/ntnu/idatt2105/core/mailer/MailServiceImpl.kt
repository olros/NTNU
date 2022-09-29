package ntnu.idatt2105.core.mailer

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class MailServiceImpl(
    private val emailSender: JavaMailSender
) : MailService {
    override fun sendMail(mail: Mail) {
        val message = SimpleMailMessage()
        message.setTo(mail.to)
        message.setFrom(mail.from)
        message.setSubject(mail.subject)
        mail.htmlTemplate.props.get(2)?.let { message.setText(it) }
        try {
            emailSender.send(message)
        } catch (ex: Exception) {
            TODO("LOG")
        }
    }
}
