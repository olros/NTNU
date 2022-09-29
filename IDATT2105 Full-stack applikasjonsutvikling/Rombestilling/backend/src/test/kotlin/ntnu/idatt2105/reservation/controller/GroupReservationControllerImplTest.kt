package ntnu.idatt2105.reservation.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.serpro69.kfaker.Faker
import ntnu.idatt2105.factories.GroupReservationFactory
import ntnu.idatt2105.factories.UserFactory
import ntnu.idatt2105.group.repository.GroupRepository
import ntnu.idatt2105.reservation.dto.CreateGroupReservationRequest
import ntnu.idatt2105.reservation.model.GroupReservation
import ntnu.idatt2105.reservation.repository.ReservationRepository
import ntnu.idatt2105.section.model.Section
import ntnu.idatt2105.section.repository.SectionRepository
import ntnu.idatt2105.user.model.RoleType
import ntnu.idatt2105.user.repository.UserRepository
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
class GroupReservationControllerImplTest {

    private fun getURL(section: Section) = "/sections/${section.id}/reservations/"

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var reservationRepository: ReservationRepository

    @Autowired
    private lateinit var groupRepository: GroupRepository

    @Autowired
    private lateinit var sectionRepository: SectionRepository

    @Autowired
    private lateinit var userDetailsService: UserDetailsService

    @Autowired
    private lateinit var userRepository: UserRepository

    private lateinit var reservation: GroupReservation

    private val faker = Faker()

    @BeforeEach
    fun setUp() {
        reservation = GroupReservationFactory().`object`
        userRepository.save(reservation.group!!.creator)
        groupRepository.save(reservation.group!!)
        sectionRepository.save(reservation.section!!)
        reservation = reservationRepository.save(reservation)
    }

    @AfterEach
    fun cleanUp() {
        reservationRepository.deleteAll()
        sectionRepository.deleteAll()
        groupRepository.deleteAll()
    }

    @Test
    @WithMockUser(roles = [RoleType.USER, RoleType.ADMIN])
    fun `test reservation controller GET all returns OK and page of reservation for given section`() {
        val reservationWithDifferentSection = GroupReservationFactory().`object`

        userRepository.save(reservationWithDifferentSection.group!!.creator)
        groupRepository.save(reservationWithDifferentSection.group!!)
        sectionRepository.save(reservationWithDifferentSection.section!!)
        reservation = reservationRepository.save(reservationWithDifferentSection)

        this.mvc.perform(get(getURL(reservation.section!!)))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.numberOfElements").value(1))
                .andExpect(jsonPath("$.content.[*].group.id", hasItem(reservation.group!!.id.toString())))
                .andExpect(jsonPath("$.content.[*].id", hasItem(reservation.id.toString())))
    }

    @Test
    @WithMockUser(roles = [RoleType.USER, RoleType.ADMIN])
    fun `test reservation controller GET returns OK and reservation`() {
        this.mvc.perform(get("${getURL(reservation.section!!)}${reservation.id}/"))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type").value("group"))
                .andExpect(jsonPath("$.group.id").value(reservation.group!!.id.toString()))
                .andExpect(jsonPath("$.text").value(reservation.text))
    }

    @Test
    @WithMockUser(roles = [RoleType.USER, RoleType.ADMIN])
    fun `test reservation controller POST creates new Reservation`() {
        val newReservation = GroupReservationFactory().`object`

        userRepository.save(newReservation.group!!.creator)
        groupRepository.save(newReservation.group!!)
        sectionRepository.save(newReservation.section!!)

        val reservationCreate = CreateGroupReservationRequest(
                entityId = newReservation.group?.id,
                sectionId = newReservation.section?.id,
                fromTime = reservation.fromTime?.plusDays(1),
                toTime = reservation.toTime?.plusDays(1),
                text = reservation.text,
                nrOfPeople = reservation.nrOfPeople,
                type = "group")

        this.mvc.perform(post(getURL(newReservation.section!!))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservationCreate)))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type").value("group"))
                .andExpect(jsonPath("$.group.id").value(newReservation.group!!.id.toString()))
    }

    @Test
    fun `test reservation controller PUT updates Reservation`() {
        val admin = UserFactory().admin()
        userRepository.save(admin)
        val userDetails = userDetailsService.loadUserByUsername(admin.email)

        val text = faker.breakingBad.episode()
        reservation.text = text

        this.mvc.perform(put("${getURL(reservation.section!!)}${reservation.id}/")
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservation.toReservationDto()))
        )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type").value("group"))
                .andExpect(jsonPath("$.group.id").value(reservation.group!!.id.toString()))
                .andExpect(jsonPath("$.text").value(text))
    }
}
