package ntnu.idatt2105.user.service

import com.querydsl.core.types.Predicate
import ntnu.idatt2105.core.response.Response
import ntnu.idatt2105.reservation.service.ReserverService
import ntnu.idatt2105.security.dto.ForgotPassword
import ntnu.idatt2105.security.dto.MakeAdminDto
import ntnu.idatt2105.security.dto.ResetPasswordDto
import ntnu.idatt2105.user.dto.DetailedUserDto
import ntnu.idatt2105.user.dto.UserDto
import ntnu.idatt2105.user.dto.UserRegistrationDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.multipart.MultipartFile
import java.util.*

interface UserService : ReserverService {
    fun registerUser(user: UserRegistrationDto): UserDto
    fun registerUserBatch(file: MultipartFile): Response
    fun updateUser(id: UUID, user: UserDto): UserDto
    fun getUsers(pageable: Pageable, predicate: Predicate): Page<UserDto>
    fun forgotPassword(email: ForgotPassword)
    fun resetPassword(resetDto: ResetPasswordDto, id: UUID)
    fun <T> getUser(id: UUID, mapTo: Class<T>): T
    fun deleteUser(id: UUID): Response
    fun makeAdmin(user: MakeAdminDto): DetailedUserDto
    fun invalidateExpiredUsers()
}
