package ntnu.idatt2105.core.exception

enum class ExceptionType(var value: String) {
    ENTITY_NOT_FOUND("not.found"),
    DUPLICATE_ENTITY("duplicate"),
    ENTITY_EXCEPTION("exception"),
    NOT_VALID("not.valid")
}
