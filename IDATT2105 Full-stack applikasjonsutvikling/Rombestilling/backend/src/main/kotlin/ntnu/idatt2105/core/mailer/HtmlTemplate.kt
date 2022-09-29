package ntnu.idatt2105.core.mailer

class HtmlTemplate(
    private val template: String,
    val props: Map<Int, String>
)
