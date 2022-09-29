package ntnu.idatt2105.core.mailer

class Mail(
    val from: String,
    val to: String,
    val subject: String,
    val htmlTemplate: HtmlTemplate
)
