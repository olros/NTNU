package ntnu.idatt2105.security.exception

class InvalidJwtToken(error: String?) : RuntimeException(error) {
    constructor() : this("Invalid Jwt token")
}
