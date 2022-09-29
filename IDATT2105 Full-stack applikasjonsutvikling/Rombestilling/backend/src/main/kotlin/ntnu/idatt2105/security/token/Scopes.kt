package ntnu.idatt2105.security.token

enum class Scopes {
    REFRESH_TOKEN;

    fun scope(): String {
        return "ROLE_$name"
    }
}
