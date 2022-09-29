package com.ntnu.gidd.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.ntnu.gidd.controller.request.LoginRequest;
import com.ntnu.gidd.dto.User.UserPasswordForgotDto;
import com.ntnu.gidd.dto.User.UserPasswordResetDto;
import com.ntnu.gidd.dto.User.UserPasswordUpdateDto;
import com.ntnu.gidd.factories.UserFactory;
import com.ntnu.gidd.model.PasswordResetToken;
import com.ntnu.gidd.model.RefreshToken;
import com.ntnu.gidd.model.User;
import com.ntnu.gidd.repository.PasswordResetTokenRepository;
import com.ntnu.gidd.repository.RefreshTokenRepository;
import com.ntnu.gidd.repository.UserRepository;
import com.ntnu.gidd.security.UserDetailsImpl;
import com.ntnu.gidd.security.config.JWTConfig;
import com.ntnu.gidd.security.token.JwtRefreshToken;
import com.ntnu.gidd.security.token.JwtToken;
import com.ntnu.gidd.security.token.TokenFactory;
import com.ntnu.gidd.service.Email.EmailServiceImpl;
import com.ntnu.gidd.util.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.mail.internet.MimeMessage;
import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = MOCK)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthenticationControllerTest {
	
	private static final String URI = "/auth/";
	private static final String password = "password123";
	
	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	
	@Autowired
	private PasswordResetTokenRepository passwordResetTokenRepository;

	@SpyBean
	private JavaMailSender mailSender;

	@Autowired
	@InjectMocks
	private EmailServiceImpl emailService;

	@Autowired
	private TokenFactory tokenFactory;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private JWTConfig jwtConfig;
	
	private User user;
	
	private RefreshToken refreshToken;
	
	private String rawRefreshToken;
	
	private String rawAccessToken;
	
	@BeforeEach
	void setUp() throws Exception {
		user = new UserFactory().getObject();
		user.setPassword(encoder.encode(password));
		
		user = userRepository.save(user);
		
		LoginRequest loginRequest = new LoginRequest(user.getEmail(), password);
		String loginJson = objectMapper.writeValueAsString(loginRequest);
		
		MvcResult mvcResult = mvc.perform(post(URI + "login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(loginJson))
				.andReturn();
		
		rawAccessToken = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.token");
		rawRefreshToken = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.refreshToken");
		JwtRefreshToken jwtRefreshToken = jwtUtil.parseToken(this.rawRefreshToken)
				.get();
		refreshToken = RefreshToken.builder()
				.jti(UUID.fromString(jwtRefreshToken.getJti()))
				.isValid(true)
				.build();
		refreshTokenRepository.save(refreshToken);

		Mockito.doNothing().when(mailSender).send(any(MimeMessage.class));
	}
	
	@AfterEach
	void cleanup() {
		passwordResetTokenRepository.deleteAll();
	}
	
	/**
	 * Test that a new access token is returned when authorizing with a valid refresh token.
	 */
	@Test
	void testRefreshTokenWithValidRefreshTokenReturnsNewToken() throws Exception {
		MvcResult mvcResult = mvc.perform(get(URI + "refresh-token/")
				.header(jwtConfig.getHeader(), jwtConfig.getPrefix() + rawRefreshToken))
				.andExpect(jsonPath("$.token").isNotEmpty())
				.andExpect(jsonPath("$.refreshToken").isNotEmpty())
				.andReturn();
		
		String newToken = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.token");
		String actualEmail = jwtUtil.getEmailFromToken(newToken);
		
		assertThat(actualEmail).isEqualTo(user.getEmail());
	}
	
	/**
	 * Test that a new access token is not allowed when authorizing with an access token.
	 */
	@Test
	void testRefreshTokenWithAccessTokenIsNotAllowed() throws Exception {
		mvc.perform(get(URI + "refresh-token/")
				.header(jwtConfig.getHeader(), jwtConfig.getPrefix() + rawAccessToken))
				.andExpect(status().isBadRequest());
	}
	
	/**
	 * Test that Http 401 is returned when attempting to refresh tokens when the token is invalid.
	 */
	@Test
	void testRefreshTokenWhenRefreshTokenIsInvalidReturnsHttp401() throws Exception {
		refreshToken.setValid(false);
		refreshTokenRepository.save(refreshToken);
		
		mvc.perform(get(URI + "refresh-token/")
				.header(jwtConfig.getHeader(), jwtConfig.getPrefix() + rawRefreshToken))
				.andExpect(status().isUnauthorized());
	}
	
	/**
	 * Test that Http 401 is returned when the refresh token does not exist.
	 */
	@Test
	void testRefreshTokenWhenRefreshTokenIsNotFoundReturnsHttp401() throws Exception {
		UserDetailsImpl userDetails = UserDetailsImpl.builder()
				.id(user.getId())
				.email(user.getEmail())
				.build();
		JwtToken unknownToken = tokenFactory.createRefreshToken(userDetails);
		
		mvc.perform(get(URI + "refresh-token/")
				.header(jwtConfig.getHeader(), jwtConfig.getPrefix() + unknownToken.getToken()))
				.andExpect(status().isUnauthorized());
	}
	
	/**
	 * Test that reusing a refresh token is not valid.
	 */
	@Test
	void testRefreshTokenWithReusedRefreshTokenReturnsHttp401() throws Exception {
		mvc.perform(get(URI + "refresh-token/")
				.header(jwtConfig.getHeader(), jwtConfig.getPrefix() + rawRefreshToken));
		
		mvc.perform(get(URI + "refresh-token/")
				.header(jwtConfig.getHeader(), jwtConfig.getPrefix() + rawRefreshToken))
				.andExpect(status().isUnauthorized());
	}
	
	/**
	 * Test that reusing an old refresh token invalidates the chain of refresh tokens.
	 */
	@Test
	void testRefreshTokenWithReusedRefreshTokenInvalidatesSubsequentTokens() throws Exception {
		MvcResult mvcResult = mvc.perform(get(URI + "refresh-token/")
				.header(jwtConfig.getHeader(),
						jwtConfig.getPrefix() + rawRefreshToken))
				.andReturn();
		
		String newRawRefreshToken = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.refreshToken");
		
		mvc.perform(get(URI + "refresh-token/")
				.header(jwtConfig.getHeader(),
						jwtConfig.getPrefix() + newRawRefreshToken));
		
		JwtRefreshToken jwtRefreshToken = jwtUtil.parseToken(newRawRefreshToken)
				.get();
		RefreshToken oldRefreshToken = refreshTokenRepository.findById(refreshToken.getJti())
				.get();
		RefreshToken newRefreshToken = refreshTokenRepository.findById(UUID.fromString(jwtRefreshToken.getJti()))
				.get();
		
		assertThat(oldRefreshToken.isValid()).isFalse();
		assertThat(newRefreshToken.isValid()).isFalse();
	}
	
	/**
	 * Verifies that you can change password if you have the correct token
	 *
	 * @throws Exception
	 */
	@Test
	public void testChangePasswordWithToken() throws Exception {
		LoginRequest loginRequest = new LoginRequest(user.getEmail(), "19newPassword");
		String loginJson = objectMapper.writeValueAsString(loginRequest);
		UserPasswordUpdateDto update = new UserPasswordUpdateDto(password, "19newPassword");

		UserDetails userDetails = UserDetailsImpl.builder().email(user.getEmail()).build();
		mvc.perform(post("/auth/change-password/")
				.contentType(MediaType.APPLICATION_JSON).with(user(userDetails))
				.content(objectMapper.writeValueAsString(update))
				.header(jwtConfig.getHeader(), jwtConfig.getPrefix() + rawAccessToken))
				.andExpect(status().isOk());
		
		mvc.perform(post(URI + "login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(loginJson))
				.andExpect(status().isOk());
	}
	
	/**
	 * Verifies that you cannot change password without a valid token
	 *
	 * @throws Exception
	 */
	@Test
	public void testChangePasswordWithoutTokenFails() throws Exception {
		UserPasswordUpdateDto update = new UserPasswordUpdateDto(password, "19newPassword");
		mvc.perform(post("/auth/change-password/")
				.contentType(MediaType.APPLICATION_JSON)
				.header(jwtConfig.getHeader(), jwtConfig.getPrefix() + rawRefreshToken))
				.andExpect(status().isBadRequest());
	}
	
	/**
	 * Tests that you cannot change the password if you submit wrong current password
	 *
	 * @throws Exception
	 */
	@Test
	public void testChangePasswordWithWrongOldPasswordFails() throws Exception {
		LoginRequest loginRequest = new LoginRequest(user.getEmail(), "newPassword");
		String loginJson = objectMapper.writeValueAsString(loginRequest);
		UserPasswordUpdateDto update = new UserPasswordUpdateDto("newPassword", "19newPassword");
		
		
		mvc.perform(post("/auth/change-password/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(update))
				.header(jwtConfig.getHeader(), jwtConfig.getPrefix() + rawRefreshToken))
				.andExpect(status().isNotAcceptable());
		
		mvc.perform(post(URI + "login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(loginJson))
				.andExpect(status().isUnauthorized());
	}
	
	@Test
	public void testForgotPasswordWithCorrectEmailReturns200() throws Exception {
		UserPasswordForgotDto email = new UserPasswordForgotDto();
		email.setEmail(user.getEmail());
		
		assertEquals(passwordResetTokenRepository.findAll().size(), 0);
		mvc.perform(post(URI + "forgot-password/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(email)))
				.andExpect(status().isOk());
		assertEquals(passwordResetTokenRepository.findAll().size(), 1);
		
	}
	
	@Test
	public void testForgotPasswordWithIncorrectEmailFails() throws Exception {
		UserPasswordForgotDto email = new UserPasswordForgotDto();
		email.setEmail("test@test.no");
		
		assertEquals(passwordResetTokenRepository.findAll().size(), 0);
		
		mvc.perform(post(URI + "forgot-password/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(email)))
				.andExpect(status().isNotFound());
		assertEquals(passwordResetTokenRepository.findAll().size(), 0);
	}
	
	@Test
	public void testValidatePasswordWithValidToken() throws Exception {
		PasswordResetToken token = new PasswordResetToken();
		token.setUser(user);
		UUID uuid = passwordResetTokenRepository.save(token).getId();
		
		UserPasswordResetDto dto = new UserPasswordResetDto();
		String newPassword = "newPassword123";
		dto.setNewPassword(newPassword);
		dto.setEmail(user.getEmail());
		
		mvc.perform(post(URI + "reset-password/" + uuid.toString() + "/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isOk());
		
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setEmail(user.getEmail());
		loginRequest.setPassword(newPassword);
		
		mvc.perform(post(URI + "login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
				.andExpect(status().isOk());
	}
	
	@Test
	public void testValidatePasswordWithInvalidToken() throws Exception {
		UUID randomId = UUID.randomUUID();
		
		UserPasswordResetDto dto = new UserPasswordResetDto();
		dto.setNewPassword(user.getPassword());
		dto.setEmail(user.getEmail());
		
		mvc.perform(post(URI + "reset-password/" + randomId.toString() + "/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isNotFound());
	}
	
	@Test
	public void testVaildatePasswordCannotLoginWithOldPassword() throws Exception {
		PasswordResetToken token = new PasswordResetToken();
		token.setUser(user);
		UUID uuid = passwordResetTokenRepository.save(token).getId();
		
		UserPasswordResetDto dto = new UserPasswordResetDto();
		String newPassword = "newPassword123";
		dto.setNewPassword(newPassword);
		dto.setEmail(user.getEmail());
		
		mvc.perform(post(URI + "reset-password/" + uuid.toString() + "/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isOk());
		
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setEmail(user.getEmail());
		loginRequest.setPassword(user.getPassword());
		
		mvc.perform(post(URI + "login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
				.andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void testValidatePasswordWithValidTokenMultipleTimes() throws Exception {
		PasswordResetToken token = new PasswordResetToken();
		token.setUser(user);
		UUID uuid = passwordResetTokenRepository.save(token).getId();
		
		PasswordResetToken token2 = new PasswordResetToken();
		token2.setUser(user);
		UUID uuid2 = passwordResetTokenRepository.save(token2).getId();
		
		PasswordResetToken token3 = new PasswordResetToken();
		token3.setUser(user);
		UUID uuid3 = passwordResetTokenRepository.save(token3).getId();
		
		String newPassword = "newPassword123";
		String newPassword2 = "newPassword1234";
		String newPassword3 = "newPassword12345";
		
		UserPasswordResetDto dto = new UserPasswordResetDto();
		dto.setNewPassword(newPassword);
		dto.setEmail(user.getEmail());
		
		mvc.perform(post(URI + "reset-password/" + uuid.toString() + "/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isOk());
		
		dto.setNewPassword(newPassword2);
		
		mvc.perform(post(URI + "reset-password/" + uuid2.toString() + "/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isOk());
		
		dto.setNewPassword(newPassword3);
		
		mvc.perform(post(URI + "reset-password/" + uuid3.toString() + "/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isOk());
		
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setEmail(user.getEmail());
		loginRequest.setPassword(newPassword3);
		
		mvc.perform(post(URI + "login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
				.andExpect(status().isOk());
	}
}