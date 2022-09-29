package ntnu.idatt2105.core.config

import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

/**
 * Load custom properties
 */
@Component
@PropertySource("classpath:custom.properties")
class PropertiesConfig(val env: Environment) {

    fun getConfigValue(configKey: String?): String? {
        return env.getProperty(configKey!!)
    }
}
