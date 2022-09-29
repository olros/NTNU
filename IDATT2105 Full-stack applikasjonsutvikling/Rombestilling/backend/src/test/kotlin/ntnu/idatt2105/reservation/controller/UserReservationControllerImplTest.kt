package ntnu.idatt2105.reservation.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.serpro69.kfaker.Faker
import ntnu.idatt2105.core.util.ReservationConstants
import ntnu.idatt2105.factories.UserReservationFactory
import ntnu.idatt2105.reservation.dto.CreateUserReservationRequest
import ntnu.idatt2105.reservation.model.UserReservation
import ntnu.idatt2105.reservation.repository.ReservationRepository
import ntnu.idatt2105.section.model.Section
import ntnu.idatt2105.section.repository.SectionRepository
import ntnu.idatt2105.user.model.RoleType
import ntnu.idatt2105.user.repository.UserRepository
import org.hamcrest.Matchers
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
import java.time.LocalTime
import java.time.ZonedDateTime

@SpringBootTest
@AutoConfigureMockMvc
class UserReservationControllerImplTest {

    private fun getURL(section: Section) = "/sections/${section.id}/reservations/"

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var reservationRepository: ReservationRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userDetailsService: UserDetailsService

    @Autowired
    private lateinit var sectionRepository: SectionRepository

    private lateinit var reservation: UserReservation

    private val faker = Faker()

    @BeforeEach
    fun setUp() {
        reservation = UserReservationFactory().`object`
        userRepository.save(reservation.user!!)
        sectionRepository.save(reservation.section!!)
        reservation = reservationRepository.save(reservation)
    }
    @AfterEach
    fun cleanUp() {
        reservationRepository.deleteAll()
        sectionRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    @WithMockUser(roles = [RoleType.USER, RoleType.ADMIN])
    fun `test reservation controller GET all returns OK and page of reservation for given section`() {
        val newReservation = UserReservationFactory().`object`
        userRepository.save(newReservation.user!!)
        sectionRepository.save(newReservation.section!!)
        reservation = reservationRepository.save(newReservation)

        this.mvc.perform(get(getURL(reservation.section!!)))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.[*].user.id").value(newReservation.user!!.id.toString()))
                .andExpect(jsonPath("$.content.[*].type").value("user"))
                .andExpect(jsonPath("$.content.[*].text", Matchers.hasItem(reservation.text)))
                .andExpect(jsonPath("$.content.[*].text", Matchers.hasItem(newReservation.text)))
    }

    @Test
    @WithMockUser(roles = [RoleType.USER, RoleType.ADMIN])
    fun `test reservation controller GET returns OK and reservation`() {
        this.mvc.perform(get("${getURL(reservation.section!!)}${reservation.id}/"))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type").value("user"))
                .andExpect(jsonPath("$.user.id").value(reservation.user!!.id.toString()))
                .andExpect(jsonPath("$.text").value(reservation.text))
    }

    @Test
    @WithMockUser(roles = [RoleType.USER, RoleType.ADMIN])
    fun `test reservation controller POST creates new Reservation`() {
        val newReservation = UserReservationFactory().`object`
        userRepository.save(newReservation.user!!)

        sectionRepository.save(newReservation.section!!)
        val reservationCreate = CreateUserReservationRequest(
                entityId = newReservation.user?.id,
                sectionId = newReservation.section?.id,
                fromTime = reservation.fromTime?.plusDays(1),
                toTime = reservation.toTime?.plusDays(1),
                text = reservation.text,
                nrOfPeople = reservation.nrOfPeople,
                type = "user")

        this.mvc.perform(post(getURL(reservation.section!!))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservationCreate)))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.user.id").value(newReservation.user!!.id.toString()))
                .andExpect(jsonPath("$.type").value("user"))
                .andExpect(jsonPath("$.text").value(reservation.text))
    }

    @Test
    @WithMockUser(roles = [RoleType.USER, RoleType.ADMIN])
    fun `test reservation controller PUT updates Reservation`() {
        val text = faker.breakingBad.episode()
        reservation.text = text
        val userDetails = userDetailsService.loadUserByUsername(reservation.user?.email)

        this.mvc.perform(put("${getURL(reservation.section!!)}${reservation.id}/")
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservation.toReservationDto())))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type").value("user"))
                .andExpect(jsonPath("$.user.id").value(reservation.user!!.id.toString()))
                .andExpect(jsonPath("$.text").value(text))
    }

    @Test
    @WithMockUser(roles = [RoleType.USER, RoleType.ADMIN])
    fun `test reservation controller POST can't creates new Reservation in a existing interval different end time`() {
        val newReservation = UserReservationFactory().`object`
        userRepository.save(newReservation.user!!)
        sectionRepository.save(newReservation.section!!)

        val reservationCreate = CreateUserReservationRequest(entityId = newReservation.user?.id,
                sectionId = newReservation.section?.id,
                fromTime = reservation.fromTime,
                toTime = reservation.toTime?.plusHours(20),
                text = reservation.text,
                nrOfPeople = reservation.nrOfPeople,
                type = "user")

        this.mvc.perform(post(getURL(reservation.section!!))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservationCreate)))
                .andExpect(status().isBadRequest)
    }
    @Test
    @WithMockUser(roles = [RoleType.USER, RoleType.ADMIN])
    fun `test reservation controller DELETE deletes Reservation`() {
        val userDetails = userDetailsService.loadUserByUsername(reservation.user?.email)

        this.mvc.perform(delete("${getURL(reservation.section!!)}${reservation.id}/")
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists())
    }

    @Test
    @WithMockUser(roles = [RoleType.USER, RoleType.ADMIN])
    fun `test reservation controller POST can't creates new Reservation in a existing interval different start time`() {
        val newReservation = UserReservationFactory().`object`
        userRepository.save(newReservation.user!!)
        sectionRepository.save(newReservation.section!!)
        val reservationCreate = CreateUserReservationRequest(entityId = newReservation.user?.id,
               sectionId = newReservation.section?.id,
                fromTime = reservation.fromTime?.plusHours(2),
                toTime = reservation.toTime,
                text = reservation.text,
                nrOfPeople = reservation.nrOfPeople,
                type = "user")

        this.mvc.perform(post(getURL(reservation.section!!))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservationCreate)))
                .andExpect(status().isBadRequest)
    }
    @Test
    @WithMockUser(roles = [RoleType.USER, RoleType.ADMIN])
    fun `test reservation controller POST can't creates new Reservation in a existing interval`() {
        val newReservation = UserReservationFactory().`object`
        userRepository.save(newReservation.user!!)
        sectionRepository.save(newReservation.section!!)
        val reservationCreate = CreateUserReservationRequest(entityId = newReservation.user?.id,
                sectionId = newReservation.section?.id,
                fromTime = reservation.fromTime,
                toTime = reservation.toTime,
                text = reservation.text,
                nrOfPeople = reservation.nrOfPeople)

        this.mvc.perform(post(getURL(reservation.section!!))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservationCreate)))
                .andExpect(status().isBadRequest)
    }

    @Test
    @WithMockUser(roles = [RoleType.USER, RoleType.ADMIN])
    fun `test reservation controller GET all returns OK and page of reservation for given section with time filter`() {
        var newReservation = UserReservationFactory().`object`

        newReservation.section = reservation.section
        newReservation.fromTime = ZonedDateTime.now().minusDays(50)
        newReservation.toTime = ZonedDateTime.now().minusDays(25)

        userRepository.save(newReservation.user!!)
        newReservation = reservationRepository.save(newReservation)

        this.mvc.perform(get(getURL(reservation.section!!))
                .param("toTimeBefore", reservation.toTime?.plusHours(1).toString())
                .param("fromTimeAfter", reservation.fromTime?.minusHours(1).toString()))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.numberOfElements").value(1))
                .andExpect(jsonPath("$.content.[*].user.id").value(reservation.user!!.id.toString()))
                .andExpect(jsonPath("$.content.[*].type").value("user"))
                .andExpect(jsonPath("$.content.[*].id", Matchers.hasItem(reservation.id.toString())))
                .andExpect(jsonPath("$.content.[*].id", Matchers.not(newReservation.id.toString())))
    }

    @Test
    @WithMockUser(roles = [RoleType.USER, RoleType.ADMIN])
    fun `test that creating a reservation with a negative number of people returns http 400`() {
        val newReservation = UserReservationFactory().`object`
        userRepository.save(newReservation.user!!)
        sectionRepository.save(newReservation.section!!)

        val reservationWithNegativeNrOfPeople = newReservation.copy(nrOfPeople = -1)
        val reservationCreateRequest = CreateUserReservationRequest(entityId = reservationWithNegativeNrOfPeople.user?.id,
                sectionId = reservationWithNegativeNrOfPeople.section?.id,
                fromTime = reservationWithNegativeNrOfPeople.fromTime,
                toTime = reservationWithNegativeNrOfPeople.toTime,
                text = reservationWithNegativeNrOfPeople.text,
                nrOfPeople = reservationWithNegativeNrOfPeople.nrOfPeople,
                type = "user")

        mvc.perform(post(getURL(reservation.section!!))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservationCreateRequest)))
                .andExpect(status().isBadRequest)
    }

    @Test
    @WithMockUser(roles = [RoleType.USER, RoleType.ADMIN])
    fun `test that creating a reservation when from time is in the past returns http 400`() {
        val newReservation = UserReservationFactory().`object`
        userRepository.save(newReservation.user!!)
        sectionRepository.save(newReservation.section!!)

        val reservationStartingInThePast = newReservation.copy(fromTime = ZonedDateTime.now().minusDays(1))
        val reservationCreateRequest = CreateUserReservationRequest(entityId = reservationStartingInThePast.user?.id,
                sectionId = reservationStartingInThePast.section?.id,
                fromTime = reservationStartingInThePast.fromTime,
                toTime = reservationStartingInThePast.toTime,
                text = reservationStartingInThePast.text,
                nrOfPeople = reservationStartingInThePast.nrOfPeople,
                type = "user")

        mvc.perform(post(getURL(reservation.section!!))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservationCreateRequest)))
                .andExpect(status().isBadRequest)
    }

    @Test
    @WithMockUser(roles = [RoleType.USER, RoleType.ADMIN])
    fun `test that creating a reservation when from time is before 0600 returns http 400`() {
        val newReservation = UserReservationFactory().`object`
        userRepository.save(newReservation.user!!)
        sectionRepository.save(newReservation.section!!)

        val reservationStartingInThePast = newReservation.copy(fromTime = reservation.fromTime?.with(LocalTime.of(5, 0)))
        val reservationCreateRequest = CreateUserReservationRequest(entityId = reservationStartingInThePast.user?.id,
                sectionId = reservationStartingInThePast.section?.id,
                fromTime = reservationStartingInThePast.fromTime,
                toTime = reservationStartingInThePast.toTime,
                text = reservationStartingInThePast.text,
                nrOfPeople = reservationStartingInThePast.nrOfPeople,
                type = "user")

        mvc.perform(post(getURL(reservation.section!!))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservationCreateRequest)))
                .andExpect(status().isBadRequest)
    }

    @Test
    @WithMockUser(roles = [RoleType.USER, RoleType.ADMIN])
    fun `test that creating a reservation when from time is after 2000 returns http 400`() {
        val newReservation = UserReservationFactory().`object`
        userRepository.save(newReservation.user!!)
        sectionRepository.save(newReservation.section!!)

        val reservationStartingInThePast = newReservation.copy(fromTime = reservation.fromTime?.with(LocalTime.of(21, 0)))
        val reservationCreateRequest = CreateUserReservationRequest(entityId = reservationStartingInThePast.user?.id,
                sectionId = reservationStartingInThePast.section?.id,
                fromTime = reservationStartingInThePast.fromTime,
                toTime = reservationStartingInThePast.toTime,
                text = reservationStartingInThePast.text,
                nrOfPeople = reservationStartingInThePast.nrOfPeople,
                type = "user")

        mvc.perform(post(getURL(reservation.section!!))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservationCreateRequest)))
                .andExpect(status().isBadRequest)
    }

    @Test
    @WithMockUser(roles = [RoleType.USER])
    fun `test that creating a reservation when from time is more than max months in the future as user returns http 400`() {
        val newReservation = UserReservationFactory().`object`
        userRepository.save(newReservation.user!!)
        sectionRepository.save(newReservation.section!!)

        val reservationStartingInThePast = newReservation.copy(
                fromTime = reservation.fromTime?.plusMonths(ReservationConstants.MAX_MONTHS_FOR_USER_RESERVING_IN_FUTURE)
        )
        val reservationCreateRequest = CreateUserReservationRequest(entityId = reservationStartingInThePast.user?.id,
                sectionId = reservationStartingInThePast.section?.id,
                fromTime = reservationStartingInThePast.fromTime,
                toTime = reservationStartingInThePast.toTime,
                text = reservationStartingInThePast.text,
                nrOfPeople = reservationStartingInThePast.nrOfPeople,
                type = "user")

        mvc.perform(post(getURL(reservation.section!!))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservationCreateRequest)))
                .andExpect(status().isBadRequest)
    }

    @Test
    @WithMockUser(roles = [RoleType.USER, RoleType.ADMIN])
    fun `test that creating a reservation when from time is more than max months in the future as admin returns http 400`() {
        val newReservation = UserReservationFactory().`object`
        userRepository.save(newReservation.user!!)
        sectionRepository.save(newReservation.section!!)

        val reservationStartingInThePast = newReservation.copy(
                fromTime = reservation.fromTime?.plusMonths(ReservationConstants.MAX_MONTHS_FOR_ADMIN_RESERVING_IN_FUTURE + 1)
        )
        val reservationCreateRequest = CreateUserReservationRequest(entityId = reservationStartingInThePast.user?.id,
                sectionId = reservationStartingInThePast.section?.id,
                fromTime = reservationStartingInThePast.fromTime,
                toTime = reservationStartingInThePast.toTime,
                text = reservationStartingInThePast.text,
                nrOfPeople = reservationStartingInThePast.nrOfPeople, type = "user")

        mvc.perform(post(getURL(reservation.section!!))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservationCreateRequest)))
                .andExpect(status().isBadRequest)
    }

    @Test
    @WithMockUser(roles = [RoleType.USER, RoleType.ADMIN])
    fun `test that creating a reservation when from time is before end time returns http 400`() {
        val newReservation = UserReservationFactory().`object`
        userRepository.save(newReservation.user!!)
        sectionRepository.save(newReservation.section!!)

        val reservationWithNegatedTimeInterval = newReservation.copy(fromTime = reservation.toTime, toTime = reservation.fromTime)
        val reservationCreateRequest = CreateUserReservationRequest(entityId = reservationWithNegatedTimeInterval.user?.id,
                sectionId = reservationWithNegatedTimeInterval.section?.id,
                fromTime = reservationWithNegatedTimeInterval.fromTime,
                toTime = reservationWithNegatedTimeInterval.toTime,
                text = reservationWithNegatedTimeInterval.text,
                nrOfPeople = reservationWithNegatedTimeInterval.nrOfPeople, type = "user")

        mvc.perform(post(getURL(reservation.section!!))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservationCreateRequest)))
                .andExpect(status().isBadRequest)
    }

    @Test
    @WithMockUser(roles = [RoleType.USER, RoleType.ADMIN])
    fun `test that creating a reservation when time interval is greater than maximum allowed duration returns http 400`() {
        val newReservation = UserReservationFactory().`object`
        userRepository.save(newReservation.user!!)
        sectionRepository.save(newReservation.section!!)

        val reservationWithLongerThanMaxDuration = newReservation.copy(toTime = reservation.toTime?.withHour(ReservationConstants.MAX_DURATION + 1))
        val reservationCreateRequest = CreateUserReservationRequest(entityId = reservationWithLongerThanMaxDuration.user?.id,
                sectionId = reservationWithLongerThanMaxDuration.section?.id,
                fromTime = reservationWithLongerThanMaxDuration.fromTime,
                toTime = reservationWithLongerThanMaxDuration.toTime,
                text = reservationWithLongerThanMaxDuration.text,
                nrOfPeople = reservationWithLongerThanMaxDuration.nrOfPeople, type = "user")
        mvc.perform(post(getURL(reservation.section!!))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservationCreateRequest)))
                .andExpect(status().isBadRequest)
    }
}
