package com.ntnu.gidd.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.ntnu.gidd.config.PasswordEncoderConfig;
import com.ntnu.gidd.config.JwtConfiguration;
import com.ntnu.gidd.controller.request.LoginRequest;
import com.ntnu.gidd.factories.UserFactory;
import com.ntnu.gidd.model.User;
import com.ntnu.gidd.repository.UserRepository;
import com.ntnu.gidd.security.config.JWTConfig;
import com.ntnu.gidd.security.config.WebSecurity;
import com.ntnu.gidd.service.token.RefreshTokenService;
import com.ntnu.gidd.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {WebSecurity.class, PasswordEncoderConfig.class, JwtConfiguration.class})
@ActiveProfiles("test")
public class AuthenticationTest {
	
	private static final String URI = "/auth/login";
	private static final String password = "password123";
	
	@MockBean
	private UserRepository userRepository;

	@MockBean
	private RefreshTokenService refreshTokenService;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private BCryptPasswordEncoder encoder;

	@Autowired
	private JWTConfig jwtConfig;
	
	private JwtUtil jwtUtil;
	
	private User testUser;
	
	@BeforeEach
	public void setup() throws Exception {
		jwtUtil = new JwtUtil(jwtConfig);

		testUser = new UserFactory().getObject();
		testUser.setPassword(encoder.encode(password));

		when(userRepository.findByEmail(testUser.getEmail()))
				.thenReturn(Optional.of(testUser));
	}
	
	/**
	 * Test that you can login with a user that does exist
	 *
	 * @throws Exception
	 */
	@Test
	public void testLoginWithCorrectCredentialsReturnsJwtToken() throws Exception {
		LoginRequest loginRequest = new LoginRequest(testUser.getEmail(), password);
		String loginJson = objectMapper.writeValueAsString(loginRequest);
		
		MvcResult mvcResult = mockMvc.perform(post(URI)
				.contentType(MediaType.APPLICATION_JSON)
				.content(loginJson))
				.andExpect(status().isOk())
				.andReturn();
		
		String token = mvcResult.getResponse().getHeader(jwtConfig.getHeader());
		
		assertThat(token).isNotNull();
	}
	
	/**
	 * Test that the jwt returned is created for the user logging in.
	 */
	@Test
	public void testLoginReturnsJwtTokenWithCorrectContent() throws Exception {
		LoginRequest loginRequest = new LoginRequest(testUser.getEmail(), password);
		String loginJson = objectMapper.writeValueAsString(loginRequest);
		
		MvcResult mvcResult = mockMvc.perform(post(URI)
				.contentType(MediaType.APPLICATION_JSON)
				.content(loginJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").isNotEmpty())
				.andExpect(jsonPath("$.refreshToken").isNotEmpty())
				.andReturn();

		String token = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.token");
		String actualEmail = jwtUtil.getEmailFromToken(token);
		
		assertThat(actualEmail).isEqualTo(testUser.getEmail());
	}
	
	/**
	 * Tests that you cannot login with a user that does not exist
	 */
	@Test
	public void testLoginWithoutARegisteredUserReturnsUnauthorized() throws Exception {
		LoginRequest loginRequest = new LoginRequest("feil", "bruker");
		String loginJson = objectMapper.writeValueAsString(loginRequest);
		
		mockMvc.perform(post(URI)
				.contentType(MediaType.APPLICATION_JSON)
				.content(loginJson))
				.andExpect(status().isUnauthorized());
	}
	
	/**
	 * Test that attempting to login with incorrect password returns 401 Unauthorized.
	 */
	@Test
	public void testLoginWhenPasswordIsIncorrectReturnsUnauthorized() throws Exception {
		String incorrectPassword = "wrongpassword";
		testUser.setPassword(encoder.encode(incorrectPassword));
		
		LoginRequest loginRequest = new LoginRequest(testUser.getEmail(), incorrectPassword);
		String loginJson = objectMapper.writeValueAsString(loginRequest);
		
		when(userRepository.findByEmail(testUser.getEmail()))
				.thenReturn(Optional.empty());
		
		mockMvc.perform(post(URI)
				.contentType(MediaType.APPLICATION_JSON)
				.content(loginJson))
				.andExpect(status().isUnauthorized());
	}
	
	/**
	 * Test that attempting to login with incorrect email returns 401 Unauthorized.
	 */
	@Test
	public void testLoginWithWrongEmailReturnsUnauthorized() throws Exception {
		LoginRequest loginRequest = new LoginRequest("feil", password);
		String loginJson = objectMapper.writeValueAsString(loginRequest);
		
		mockMvc.perform(post((URI))
				.contentType(MediaType.APPLICATION_JSON)
				.content(loginJson))
				.andExpect(status().isUnauthorized());
		
	}
	
	@Test
	@Disabled
	public void testLoginWithCorrectCredentialsAndCanAccessContent() throws Exception {
		LoginRequest loginRequest = new LoginRequest(testUser.getEmail(), password);
		String loginJson = objectMapper.writeValueAsString(loginRequest);
		
		MvcResult response = mockMvc.perform(post((URI))
				.contentType(MediaType.APPLICATION_JSON)
				.content(loginJson))
				.andExpect(status().isOk())
				.andReturn();
		
		String token = response.getResponse().getHeader(jwtConfig.getHeader());
		assert token != null;
		
		mockMvc.perform(get("/api/activities/")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", token))
				.andExpect(status().isOk());
	}
}
