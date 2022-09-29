package ntnu.idatt2105.user.service

import io.github.serpro69.kfaker.Faker
import ntnu.idatt2105.core.config.ModelMapperConfig
import ntnu.idatt2105.core.exception.ApplicationException
import ntnu.idatt2105.core.mailer.MailService
import ntnu.idatt2105.factories.UserFactory
import ntnu.idatt2105.security.repository.PasswordResetTokenRepository
import ntnu.idatt2105.user.dto.DetailedUserDto
import ntnu.idatt2105.user.dto.UserDto
import ntnu.idatt2105.user.model.User
import ntnu.idatt2105.user.repository.RoleRepository
import ntnu.idatt2105.user.repository.UserRepository
import ntnu.idatt2105.util.JpaUtils
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.modelmapper.ModelMapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.*
import java.util.stream.Stream

@ExtendWith(MockitoExtension::class)
internal class UserServiceImplTest {

    private lateinit var userService: UserService

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var passwordEncoder: BCryptPasswordEncoder

    @Mock
    private lateinit var mailService: MailService

    @Mock
    private lateinit var passwordResetTokenRepository: PasswordResetTokenRepository

    @Mock
    private lateinit var roleRepository: RoleRepository

    private lateinit var modelMapper: ModelMapper

    private lateinit var userFactory: UserFactory

    private lateinit var defaultPageable: Pageable

    @BeforeEach
    fun setUp() {
        modelMapper = ModelMapperConfig().modelMapper()
        userFactory = UserFactory()
        userService = UserServiceImpl(
            userRepository = userRepository,
            modelMapper = modelMapper,
            passwordEncoder = passwordEncoder,
            mailService = mailService,
            passwordResetTokenRepository = passwordResetTokenRepository,
            roleRepository = roleRepository
        )

        defaultPageable = JpaUtils().getDefaultPageable()
    }

    @Test
    fun `test get users returns all users`() {
        val users = IntRange(1, 4).map { userFactory.`object` }
        val expectedPage: Page<User> = PageImpl(users, JpaUtils().getDefaultPageable(), users.size.toLong())
        val predicate = JpaUtils().getEmptyPredicate()
        `when`(userRepository.findAll(predicate, defaultPageable))
            .thenReturn(expectedPage)

        val actualUsers = userService.getUsers(defaultPageable, predicate)

        assertThat(actualUsers.size).isEqualTo(expectedPage.size)
        assertThat(actualUsers.content).doesNotContainNull()
    }

    @Test
    fun `test get user when user not found throws exception`() {
        `when`(userRepository.findById(any(UUID::class.java)))
            .thenThrow(ApplicationException.EntityNotFoundException("Ignored"))

        assertThatExceptionOfType(ApplicationException.EntityNotFoundException::class.java)
            .isThrownBy { userService.getUser(UUID.randomUUID(), UserDto::class.java) }
    }

    @Test
    fun `test get user when user exists returns the user`() {
        val user = userFactory.`object`
        `when`(userRepository.findById(user.id))
            .thenReturn(Optional.of(user))

        val actualUser = userService.getUser(user.id, UserDto::class.java)

        assertThat(actualUser.id).isEqualTo(user.id)
    }

    @ParameterizedTest
    @MethodSource("userDtoClassProvider")
    fun `test get user maps to correct dto class`(dtoClass: Class<*>) {
        val user = userFactory.`object`
        `when`(userRepository.findById(user.id))
            .thenReturn(Optional.of(user))

        val actualUser = userService.getUser(user.id, mapTo = dtoClass)

        assertThat(actualUser).isExactlyInstanceOf(dtoClass)
    }

    companion object {
        @JvmStatic
        private fun userDtoClassProvider() =
            Stream.of(
                Arguments.of(UserDto::class.java),
                Arguments.of(DetailedUserDto::class.java),
            )
    }

    @Test
    fun `test update user updates user with the given id`() {
        val user = userFactory.`object`
        `when`(userRepository.findById(user.id))
            .thenReturn(Optional.of(user))

        user.email = Faker().internet.email()

        `when`(userRepository.save(any(User::class.java)))
            .thenReturn(user)

        val updateUserRequest = modelMapper.map(user, UserDto::class.java)
        val actualUser = userService.updateUser(user.id, updateUserRequest)

        assertThat(actualUser.email).isEqualTo(user.email)
        verify(userRepository, times(1)).save(user)
    }

    @Test
    fun `test update user when not found throws exception`() {
        `when`(userRepository.findById(any(UUID::class.java)))
            .thenThrow(ApplicationException.EntityNotFoundException("Ignored"))

        val updateUserRequest = modelMapper.map(userFactory.`object`, UserDto::class.java)

        assertThatExceptionOfType(ApplicationException.EntityNotFoundException::class.java)
            .isThrownBy { userService.updateUser(UUID.randomUUID(), updateUserRequest) }
    }

    @Test
    fun `test delete user when user not found throws exception`() {
        `when`(userRepository.findById(any(UUID::class.java)))
            .thenThrow(ApplicationException.EntityNotFoundException("Ignored"))

        assertThatExceptionOfType(ApplicationException.EntityNotFoundException::class.java)
            .isThrownBy { userService.deleteUser(UUID.randomUUID()) }
    }

    @Test
    fun `test delete user when user exists deletes user with given id`() {
        val user = userFactory.`object`
        `when`(userRepository.findById(user.id))
            .thenReturn(Optional.of(user))

        userService.deleteUser(user.id)

        verify(userRepository, times(1)).delete(user)
    }
}
