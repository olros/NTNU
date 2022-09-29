package com.ntnu.gidd.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntnu.gidd.Application;
import com.ntnu.gidd.config.ModelMapperConfig;
import com.ntnu.gidd.dto.User.UserRegistrationDto;
import com.ntnu.gidd.factories.UserFactory;
import com.ntnu.gidd.model.User;
import com.ntnu.gidd.repository.UserRepository;
import com.ntnu.gidd.security.UserDetailsImpl;
import com.ntnu.gidd.security.config.JWTConfig;
import com.ntnu.gidd.utils.StringRandomizer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.stream.Stream;

import static com.ntnu.gidd.utils.StringRandomizer.getRandomString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = MOCK)
@AutoConfigureMockMvc
//@ContextConfiguration(classes = {Application.class})
@ActiveProfiles("test")
public class UserControllerTest {
	
	private String URI = "/users/";
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private JWTConfig jwtConfig;
	
	@Autowired
	private UserRepository userRepository;
	
	private User user;
	
	private UserFactory userFactory = new UserFactory();
	
	private String firstName;
	
	private String surname;
	
	private LocalDate birthDate;

	private UserDetails userDetails;

	/**
	 * Setting up variables that is the same for all tests
	 */
	@BeforeEach
	public void setUp() throws Exception {
		user = userFactory.getObject();
		assert user != null;
		userRepository.save(user);
		firstName = "Test";
		surname = "Testersen";
		birthDate = LocalDate.now();

		userDetails = UserDetailsImpl.builder().id(user.getId())
				.email(user.getEmail())
				.build();
	}
	
	/**
	 * Cleans up the saved users after each test
	 */
	@AfterEach
	public void cleanUp(){
		userRepository.deleteAll();
	}

	/**
	 * Provides a stream of Valid emails to provide parameterized test
	 *
	 * @return Stream of valid emails
	 */
	private static Stream<Arguments> provideValidEmails() {
		return Stream.of(
				Arguments.of("test123@mail.com"),
				Arguments.of("test1.testesen@mail.com"),
				Arguments.of("test_1234-testesen@mail.com")
		);
	}
	
	/**
	 * Provides a stream of Invalid emails to provide parameterized test
	 *
	 * @return Stream of invalid emails
	 */
	private static Stream<Arguments> provideInvalidEmails() {
		return Stream.of(
				Arguments.of("test123.no"),
				Arguments.of("test@"),
				Arguments.of("test@mail..com")
		);
	}
	
	/**
	 * Test that you can create a user with valid input
	 *
	 * @throws Exception from post request
	 */
	@ParameterizedTest
	@MethodSource("provideValidEmails")
	public void testCreateUserWithValidEmailAndPassword(String email) throws Exception {
		String password = "ValidPassword123";
		
		UserRegistrationDto validUser = new UserRegistrationDto(firstName, surname, password, email, birthDate);
		
		mockMvc.perform(post(URI)
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(validUser)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.firstName").value(validUser.getFirstName()))
				.andExpect(jsonPath("$.data.password").doesNotExist())
				.andExpect(jsonPath("$.data.email").doesNotExist());
	}

	@Test
	@WithMockUser(value = "spring")
	public void testGetUserByUserId() throws Exception {

		User testUser = userRepository.save(userFactory.getObject());
		mockMvc.perform(get(URI + testUser.getId().toString()+ "/")
				.contentType(MediaType.APPLICATION_JSON).with(csrf()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.firstName").value(testUser.getFirstName()));
	}

	@Test
	@WithMockUser(value = "spring")
	public void testGetUserByUserIdReturnsResponseIncludingFollowerCounts() throws Exception {
		User testUser = userRepository.save(userFactory.getObject());
		mockMvc.perform(get(URI + testUser.getId().toString()+ "/")
								.contentType(MediaType.APPLICATION_JSON).with(csrf()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.followerCount").value(0))
				.andExpect(jsonPath("$.followingCount").value(0));
	}

	@Test
	@WithMockUser(value = "spring")
	public void testGetUserByUserIdReturnsResponseIncludingFollowerCount() throws Exception {
		User testUser = userRepository.save(userFactory.getObject());
		user.addFollowing(testUser);

		userRepository.save(user);

		mockMvc.perform(get(URI + testUser.getId().toString()+ "/")
								.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.followerCount").value(testUser.getFollowers().size()));
	}

	@Test
	@WithMockUser(value = "spring")
	public void testGetUserByUserIdReturnsResponseIncludingFollowingCount() throws Exception {
		User testUser = userRepository.save(userFactory.getObject());
		testUser.addFollowing(user);

		userRepository.save(testUser);

		mockMvc.perform(get(URI + testUser.getId().toString()+ "/")
								.contentType(MediaType.APPLICATION_JSON).with(csrf()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.followingCount").value(testUser.getFollowing().size()));
	}

	@Test
	@WithMockUser(value = "spring")
	public void testGetUserByUserIdWhenCurrentUserIsFollowingAndUnauthenticatedReturnsCorrectIsCurrentUserFollowing() throws Exception {
		User testUser = userRepository.save(userFactory.getObject());
		user.addFollowing(testUser);
		userRepository.save(user);

		mockMvc.perform(get(URI + testUser.getId().toString()+ "/")
								.contentType(MediaType.APPLICATION_JSON).with(csrf()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.currentUserIsFollowing").value(false));
	}

	@Test
	@WithMockUser(value = "spring")
	public void testGetUserByUserIdWhenCurrentUserIsFollowingReturnsCorrectIsCurrentUserFollowing() throws Exception {
		User testUser = userRepository.save(userFactory.getObject());
		user.addFollowing(testUser);
		userRepository.save(user);

		mockMvc.perform(get(URI + testUser.getId().toString()+ "/")
								.with(user(userDetails))
								.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.currentUserIsFollowing").value(true));
	}

	@Test
	@WithMockUser(value = "spring")
	public void testGetAllUsers() throws Exception {

		mockMvc.perform(get(URI)
				.contentType(MediaType.APPLICATION_JSON).with(csrf()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content.[*].firstName", hasItem(user.getFirstName())));
	}
	
	
	/**
	 * Test that a user can be created, but the same email cannot be used two times
	 *
	 * @throws Exception from post request
	 */
	@Test
	public void testCreateUserTwoTimesFails() throws Exception {
		User user = userFactory.getObject();
		assert user != null;
		userRepository.save(user);
		
		String email = user.getEmail();
		String password = "ValidPassword123";

		UserRegistrationDto validUser = new UserRegistrationDto(firstName, surname, password, email, birthDate);
		
		
		mockMvc.perform(post(URI)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(validUser)))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.message").value("Email is already associated with another user"));
		
	}
	
	/**
	 * Test that a user cannot be created if email is on a wrong format
	 *
	 * @throws Exception
	 */
	@ParameterizedTest
	@MethodSource("provideInvalidEmails")
	public void testCreateUserWithInvalidEmail(String email) throws Exception {
		String password = "ValidPassword123";
		
		UserRegistrationDto invalidUser = new UserRegistrationDto(firstName, surname, password, email, birthDate);
		
		mockMvc.perform(post(URI)
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidUser)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("One or more method arguments are invalid"))
				.andExpect(jsonPath("$.data.email").exists());
	}

	/**
	 * Test that a user cannot be created if password is too weak
	 *
	 * @throws Exception
	 */
	@Test
	public void testCreateUserWithInvalidPassword() throws Exception {
		String password = "abc123";

		UserRegistrationDto invalidUser = new UserRegistrationDto(firstName, surname, password, "test@testersen.com", birthDate);

		mockMvc.perform(post(URI)
			.with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(invalidUser)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("One or more method arguments are invalid"))
			.andExpect(jsonPath("$.data.password").exists());
	}
	
	/**
	 * Tests that get return a correct user according to token
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetUserReturnsCorrectUser() throws Exception {
		UserDetails userDetails = UserDetailsImpl.builder().email(user.getEmail()).build();
		mockMvc.perform(get(URI + "me/")
				.with(user(userDetails)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(user.getId().toString()));
	}
	
	/**
	 * Tests that put updated a user and returns the updated user info
	 *
	 * @throws Exception
	 */
	@Test
	public void testUpdateUserUpdatesUserAndReturnUpdatedData() throws Exception {
		String surname = getRandomString(8);
		user.setSurname(surname);
		UserDetails userDetails = UserDetailsImpl.builder().email(user.getEmail()).build();
		mockMvc.perform(put(URI + user.getId() + "/")
				.with(user(userDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(user)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(user.getId().toString()));
	}

	@Test
	@WithMockUser(value = "spring")
	public void testDeleteUserAndReturnsOk() throws Exception {
		User userToDelete = userFactory.getObject();
		assert userToDelete != null;
		userToDelete = userRepository.save(userToDelete);

		UserDetails userDetails = UserDetailsImpl.builder().email(userToDelete.getEmail()).build();

		mockMvc.perform(delete(URI + "me/")
				.with(user(userDetails))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("User has been deleted"));;
	}

	
}
