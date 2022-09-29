package ntnu.idatt2105.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.JsonPath
import ntnu.idatt2105.core.config.PasswordEncoderConfig
import ntnu.idatt2105.factories.UserFactory
import ntnu.idatt2105.security.config.JWTConfig
import ntnu.idatt2105.security.config.WebSecurity
import ntnu.idatt2105.security.dto.LoginRequest
import ntnu.idatt2105.security.service.RefreshTokenService
import ntnu.idatt2105.user.model.User
import ntnu.idatt2105.user.repository.UserRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest
@ContextConfiguration(classes = [WebSecurity::class, PasswordEncoderConfig::class, JWTConfig::class, JwtUtil::class])
class AuthenticationTest {

    @MockBean
    private lateinit var userRepository: UserRepository

    @MockBean
    private lateinit var refreshTokenService: RefreshTokenService

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var encoder: BCryptPasswordEncoder

    @Autowired
    private lateinit var jwtConfig: JWTConfig

    @Autowired
    private lateinit var jwtUtil: JwtUtil

    private lateinit var user: User

    companion object {
        private const val URI = "/auth/login"
        private const val password = "password123"
    }

    @BeforeEach
    fun setup() {
        user = UserFactory().`object`
        user.password = encoder.encode(password)

        Mockito.`when`(userRepository.findByEmail(user.email))
            .thenReturn(user)
    }

    @Test
    fun `test that logging in with correct credentials returns access token `() {
        val loginRequest = LoginRequest(user.email, password)
        val loginJson = objectMapper.writeValueAsString(loginRequest)

        val mvcResult = mockMvc.perform(
            MockMvcRequestBuilders.post(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

        val token = mvcResult.response.getHeader(jwtConfig.header)

        Assertions.assertThat(token).isNotNull
    }

    @Test
    fun `test that logging in with correct credentials returns access token with correct content`() {
        val loginRequest = LoginRequest(user.email, password)
        val loginJson = objectMapper.writeValueAsString(loginRequest)

        val mvcResult = mockMvc.perform(
            MockMvcRequestBuilders.post(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.token").isNotEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").isNotEmpty)
            .andReturn()

        val token: String = JsonPath.read(mvcResult.response.contentAsString, "$.token")
        val actualEmail = jwtUtil.getEmailFromToken(token)

        Assertions.assertThat(actualEmail).isEqualTo(user.email)
    }

    @Test
    fun `test that attemting to login without being registered returns http 401`() {
        val loginRequest = LoginRequest("feil", "bruker")
        val loginJson = objectMapper.writeValueAsString(loginRequest)

        mockMvc.perform(
            MockMvcRequestBuilders.post(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson)
        )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `test that attempting to login with incorrect password returns http 401`() {
        val incorrectPassword = "wrongpassword"
        user.password = encoder.encode(incorrectPassword)

        val loginRequest = LoginRequest(user.email, incorrectPassword)
        val loginJson = objectMapper.writeValueAsString(loginRequest)

        Mockito.`when`(userRepository.findByEmail(user.email))
            .thenReturn(null)

        mockMvc.perform(
            MockMvcRequestBuilders.post(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson)
        )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `test that attempting to login with incorrect email returns 401 Unauthorized`() {
        val loginRequest = LoginRequest("feil", password)
        val loginJson = objectMapper.writeValueAsString(loginRequest)

        mockMvc.perform(
            MockMvcRequestBuilders.post(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson)
        )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }
}
