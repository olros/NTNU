package ntnu.idatt2105.user.dto

import com.opencsv.bean.CsvBindByName
import java.time.LocalDate
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class UserRegistrationDto(
    @get:NotBlank(message = "Field must not be blank")
    @CsvBindByName(column = "firstName")
    val firstName: String,
    @get:NotBlank(message = "Field must not be blank")
    @CsvBindByName(column = "surname")
    val surname: String,
    @get:NotBlank(message = "Field must not be blank")
    @get:Email(message = "Please provide a valid email")
    @CsvBindByName(column = "email")
    val email: String,
    @get:NotBlank(message = "Field must not be blank")
    @CsvBindByName(column = "phoneNumber")
    val phoneNumber: String,
    var expirationDate: LocalDate = LocalDate.now().plusYears(1),
) {
    constructor() : this("", "", "", "",)
}
