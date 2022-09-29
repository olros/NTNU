package ntnu.idatt2105

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class Backend

fun main(args: Array<String>) {
	runApplication<Backend>(*args)
}
