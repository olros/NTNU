package ntnu.idatt2105.security.validator

interface TokenValidator {
    fun validate(jti: String)
}
