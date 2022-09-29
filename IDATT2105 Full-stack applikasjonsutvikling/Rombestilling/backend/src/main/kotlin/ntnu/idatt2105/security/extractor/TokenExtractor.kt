package ntnu.idatt2105.security.extractor

interface TokenExtractor {
    fun extract(payload: String): String
}
