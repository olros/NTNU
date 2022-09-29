package ntnu.idatt2105.user.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.serpro69.kfaker.Faker
import ntnu.idatt2105.factories.RoleFactory
import ntnu.idatt2105.factories.UserFactory
import ntnu.idatt2105.security.config.JWTConfig
import ntnu.idatt2105.user.dto.UserRegistrationDto
import ntnu.idatt2105.user.model.RoleType
import ntnu.idatt2105.user.model.User
import ntnu.idatt2105.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.http.MediaType
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import java.util.*
import java.util.stream.Stream

@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerImplTest {

    private val URI = "/users/"

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var jwtConfig: JWTConfig

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userDetailsService: UserDetailsService

    @SpyBean
    private val mailSender: JavaMailSender? = null

    private lateinit var user: User

    private lateinit var adminUser: User

    private val userFactory: UserFactory = UserFactory()

    private lateinit var userDetails: UserDetails

    private lateinit var adminUserDetails: UserDetails

    @BeforeEach
    fun setUp() {
        user = userFactory.`object`
        adminUser = userFactory.`object`
        adminUser = adminUser.copy(roles = mutableSetOf(
            RoleFactory().`object`,
            RoleFactory().getObject(RoleType.ADMIN)
        ))

        userRepository.saveAll(listOf(user, adminUser))

        userDetails = userDetailsService.loadUserByUsername(user.email)
        adminUserDetails = userDetailsService.loadUserByUsername(adminUser.email)

        Mockito.doNothing().`when`<JavaMailSender>(mailSender).send(
            ArgumentMatchers.any(
                SimpleMailMessage::class.java
            )
        )
    }

    @AfterEach
    fun cleanUp() {
        userRepository.deleteAll()
    }

    @Test
    @WithMockUser(value = "spring")
    fun `test get user details as admin returns correct user`() {
        mockMvc.perform(
            get("$URI{userId}/", user.id.toString())
                .with(user(adminUserDetails))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(user.id.toString()))
    }

    @Test
    @WithMockUser(value = "spring")
    fun `test list users as admin returns all users`() {
        mockMvc.perform(
            get(URI)
                .with(user(adminUserDetails))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content.[*].id", hasItem(user.id.toString())))
    }

    @Test
    @WithMockUser(value = "spring")
    fun `test list users as admin returns all users with serach filter on email`() {
        val length = user.email.length
        mockMvc.perform(
            get(URI)
                .param("search", user.email.substring(0, length - 1))
                .with(user(adminUserDetails))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content.[*].id", hasItem(user.id.toString())))
    }

    @Test
    @WithMockUser(value = "spring")
    fun `test list users as admin returns all users with serach filter on surname`() {
        val length = user.surname.length
        mockMvc.perform(
            get(URI)
                .param("search", user.surname.substring(0, length - 1))
                .with(user(adminUserDetails))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content.[*].id", hasItem(user.id.toString())))
    }

    @Test
    @WithMockUser(value = "spring")
    fun `test list users as admin returns all users with serach filter on firstName`() {
        val length = user.firstName.length
        mockMvc.perform(
            get(URI)
                .param("search", user.firstName.substring(0, length - 1))
                .with(user(adminUserDetails))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content.[*].id", hasItem(user.id.toString())))
    }

    @Test
    @WithMockUser(value = "spring")
    fun `test list users as admin returns all users with search filter on phoneNumber`() {
        val length = user.phoneNumber.length
        mockMvc.perform(
            get(URI)
                .param("search", user.phoneNumber.substring(0, length - 1))
                .with(user(adminUserDetails))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content.[*].id", hasItem(user.id.toString())))
    }

    @Test
    fun `test create user as admin when user exists fails`() {
        val existingUser = createUserRegistrationDto(email = user.email)

        mockMvc.perform(
            post(URI)
                .with(user(adminUserDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(existingUser))
        )
            .andExpect(status().isBadRequest)
            .andExpect(
                jsonPath("$.message").isNotEmpty
            )
    }

    @ParameterizedTest
    @MethodSource("provideValidEmails")
    fun `test create user with a valid email as admin returns the created user`(email: String) {
        val validUser = createUserRegistrationDto(email)

        mockMvc.perform(
            post(URI)
                .with(user(adminUserDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUser))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.firstName").value(validUser.firstName))
            .andExpect(jsonPath("$.errors.fields.password").doesNotExist())
            .andExpect(jsonPath("$.errors.fields.email").doesNotExist())
    }

    @ParameterizedTest
    @MethodSource("provideInvalidEmails")
    fun `test create user with invalid email as admin fails`(email: String) {
        val invalidUser = createUserRegistrationDto(email)

        mockMvc.perform(
            post(URI)
                .with(user(adminUserDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").isNotEmpty)
            .andExpect(jsonPath("$.errors.fields.email").exists())
    }

    @Test
    fun `test get me as admin returns the currently authenticated user`() {
        mockMvc.perform(
            get(URI + "me/")
                .with(user(adminUserDetails))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(adminUser.id.toString()))
    }

    @Test
    fun `test get me as user returns the currently authenticated user`() {
        mockMvc.perform(
            get(URI + "me/")
                .with(user(userDetails))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(user.id.toString()))
    }

    @Test
    fun `test update user as admin returns the updated user`() {
        user.surname = Faker().name.lastName()

        mockMvc.perform(
            put(URI + user.id.toString() + "/")
                .with(user(adminUserDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(user.id.toString()))
    }

    @Test
    fun `test update user as user returns the updated user`() {
        user.surname = Faker().name.lastName()

        mockMvc.perform(
            put(URI + user.id.toString() + "/")
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(user.id.toString()))
    }

    private fun createUserRegistrationDto(
        email: String,
        firstName: String = Faker().name.firstName(),
        surname: String = Faker().name.lastName(),
        phoneNumber: String = Faker().phoneNumber.phoneNumber()
    ): UserRegistrationDto =
        UserRegistrationDto(
            firstName = firstName,
            surname = surname,
            email = email,
            phoneNumber = phoneNumber,
            expirationDate = LocalDate.now().plusYears(1)
        )

    companion object {

        /**
         * Provide a stream of valid email arguments.
         */
        @JvmStatic
        private fun provideValidEmails(): Stream<Arguments> =
            Stream.of(
                Arguments.of("test123@mail.com"),
                Arguments.of("test1.testesen@mail.com"),
                Arguments.of("test_1234-testesen@mail.com")
            )

        /**
         * Provide a stream of invalid email arguments.
         */
        @JvmStatic
        private fun provideInvalidEmails(): Stream<Arguments> =
            Stream.of(
                Arguments.of("test123.no"),
                Arguments.of("test@"),
                Arguments.of("test@mail..com")
            )
    }

    @Test
    fun `test delete user as admin returns http ok`() {
        mockMvc.perform(
            delete("$URI{userId}/", user.id.toString())
                .with(user(adminUserDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").isNotEmpty)
    }

    @Test
    fun `test delete user as user returns http forbidden`() {
        mockMvc.perform(
            delete("$URI{userId}/", user.id.toString())
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))
        )
            .andExpect(status().isForbidden)
    }

    @Test
    fun `test delete user as admin when user does not exist returns http not found`() {
        mockMvc.perform(
            delete("$URI{userId}/", UUID.randomUUID())
                .with(user(adminUserDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `test delete user as admin deletes the user`() {
        mockMvc.perform(
            delete("$URI{userId}/", user.id.toString())
                .with(user(adminUserDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))
        )

        assertThat(userRepository.existsById(user.id)).isFalse
    }

    @Test
    fun `test batch create with single valid users`() {
        val file = MockMultipartFile(
            "file",
            "test.csv",
            "csv",
            "firstName,surname,email,phoneNumber,expirationDate\n Amber,Cunningham,amber.cunningham@codersee.com,888 888 888,2008-08-08\n".byteInputStream()
        )

        val size = userRepository.findAll().size
        mockMvc.perform(
            multipart(URI + "batch-users/").file(file)
                .with(user(adminUserDetails))
        ).andExpect(status().isCreated)

        assert(size + 1 == userRepository.findAll().size)
    }

    @Test
    fun `test batch create with valid and invalid users`() {
        val file = MockMultipartFile(
            "file",
            "test.csv",
            "csv",
            ("firstName,surname,email,phoneNumber,expirationDate\n " +
                    "Amber,Cunningham,amber.cunningham@codersee.com,888 888 888,2008-08-08\n" +
                    "Frankie,frankie.jackson@codersee.com,777 777 777,2007-07-07\n").byteInputStream()
        )

        mockMvc.perform(
            multipart(URI + "batch-users/").file(file)
                .with(user(adminUserDetails))
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `test batch create with multiple valid users`() {
        val file = MockMultipartFile(
            "file",
            "test.csv",
            "csv",
            ("firstName,surname,email,phoneNumber,expirationDate\n" +
                    "Amber,Cunningham,amber.cunningham@codersee.com,888 888 888,2008-08-08\n" +
                    "Frankie,Jackson,frankie.jackson@codersee.com,777 777 777,2007-07-07\n" +
                    "Jessie,Carey,jessie.carey@codersee.com,666 666 666,2006-06-06\n").byteInputStream()
        )

        val size = userRepository.findAll().size
        mockMvc.perform(
            multipart(URI + "batch-users/").file(file)
                .with(user(adminUserDetails))
        ).andExpect(status().isCreated)

        assert(size + 3 == userRepository.findAll().size)
    }

    @Test
    fun `test batch create with invalid users`() {
        val file = MockMultipartFile(
            "file",
            "test.csv",
            "csv",
            " ".byteInputStream()
        )

        val size = userRepository.findAll().size
        mockMvc.perform(
            multipart(URI + "batch-users/").file(file)
                .with(user(adminUserDetails))
        ).andExpect(status().isBadRequest)

        assert(size == userRepository.findAll().size)
    }
}
