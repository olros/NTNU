package ntnu.idatt2105.group.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.serpro69.kfaker.Faker
import ntnu.idatt2105.factories.GroupFactory
import ntnu.idatt2105.group.model.Group
import ntnu.idatt2105.group.repository.GroupRepository
import ntnu.idatt2105.user.model.RoleType
import ntnu.idatt2105.user.repository.UserRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
class GroupControllerImplTest {

    private val URI = "/groups/"

    @Autowired
    private lateinit var groupRepository: GroupRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userDetailsService: UserDetailsService

    private lateinit var userDetails: UserDetails

    private lateinit var group: Group

    private val faker = Faker()
    @BeforeEach
    fun setup() {
        group = GroupFactory().`object`
        userRepository.save(group.creator)
        group = groupRepository.save(group)
        userDetails = userDetailsService.loadUserByUsername(group.creator.email)
    }

    @AfterEach
    fun cleanUp() {
        groupRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    @WithMockUser(value = "spring", roles = [RoleType.USER, RoleType.ADMIN])
    fun `test reservation controller GET returns OK and given group`() {
        this.mvc.perform(MockMvcRequestBuilders.get("$URI{groupId}/", group.id)
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(group.name))
    }

    @Test
    @WithMockUser(value = "spring", roles = [RoleType.USER, RoleType.ADMIN])
    fun `test reservation controller GET returns NOT FOUND when group does not exist`() {
        val name = faker.coffee.blendName()
        group.name = name
        this.mvc.perform(MockMvcRequestBuilders.get("$URI{groupId}/", UUID.randomUUID())
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
    }

    @Test
    @WithMockUser(value = "spring", roles = [RoleType.USER, RoleType.ADMIN])
    fun `test reservation controller POST returns Created and created group`() {
        this.mvc.perform(MockMvcRequestBuilders.post(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                .content(objectMapper.writeValueAsString(group)))
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(group.name))
    }

    @Test
    @WithMockUser(value = "spring", roles = [RoleType.USER, RoleType.ADMIN])
    fun `test reservation controller PUT returns OK and Updated group`() {
        val name = faker.coffee.blendName()
        group.name = name
        this.mvc.perform(MockMvcRequestBuilders.put("$URI{groupId}/", group.id)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                .content(objectMapper.writeValueAsString(group)))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(name))
    }

    @Test
    @WithMockUser(value = "spring", roles = [RoleType.USER, RoleType.ADMIN])
    fun `test reservation controller PUT returns NOT FOUND when group does not exist`() {
        val name = faker.coffee.blendName()
        group.name = name
        this.mvc.perform(MockMvcRequestBuilders.put("$URI{groupId}/", UUID.randomUUID())
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                .content(objectMapper.writeValueAsString(group)))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
    }

    @Test
    @WithMockUser(value = "spring", roles = [RoleType.USER, RoleType.ADMIN])
    fun `test reservation controller DELETE returns OK and returns message`() {
        val name = faker.coffee.blendName()
        group.name = name
        this.mvc.perform(MockMvcRequestBuilders.delete("$URI{groupId}/", group.id)
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
    }

    @Test
    @WithMockUser(value = "spring", roles = [RoleType.USER, RoleType.ADMIN])
    fun `test reservation controller DELETE returns NOT FOUND when group does not exist`() {
        val name = faker.coffee.blendName()
        group.name = name
        this.mvc.perform(MockMvcRequestBuilders.delete("$URI{groupId}/", UUID.randomUUID())
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
    }
}
