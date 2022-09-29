package ntnu.idatt2105.section.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.JsonPath
import ntnu.idatt2105.factories.SectionFactory
import ntnu.idatt2105.factories.UserReservationFactory
import ntnu.idatt2105.reservation.repository.ReservationRepository
import ntnu.idatt2105.section.dto.CreateSectionRequest
import ntnu.idatt2105.section.model.Section
import ntnu.idatt2105.section.repository.SectionRepository
import ntnu.idatt2105.user.model.RoleType
import ntnu.idatt2105.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.ZonedDateTime
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
class SectionControllerImplTest {

    private val URL = "/sections/"
    private fun getStatURL(section: Section) = "/sections/${section.id}/statistics/"

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var sectionRepository: SectionRepository

    private lateinit var section: Section

    @Autowired
    private lateinit var reservationRepository: ReservationRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setUp() {
        section = SectionFactory().`object`
        section = sectionRepository.save(section)
    }

    @AfterEach
    fun cleanUp() {
        reservationRepository.deleteAll()
        sectionRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    @WithMockUser(value = "spring")
    fun `test section controller GET all returns OK and page of sections`() {
        val newSection = SectionFactory().`object`
        newSection.parent = section
        sectionRepository.save(newSection)
        this.mvc.perform(get(URL))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content.[*].name", hasItem(section.name)))
            .andExpect(jsonPath("$.content.[*].name", hasItem(newSection.name)))
    }

    @Test
    @WithMockUser(value = "spring")
    fun `test section controller GET returns OK and the section`() {
        this.mvc.perform(get("$URL{sectionId}/", section.id.toString()))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("\$.name").value(section.name))
            .andExpect(jsonPath("\$.type").value(section.getType().toString()))
    }

    @Test
    @WithMockUser(value = "spring")
    fun `test section controller GET returns not found`() {
        this.mvc.perform(get("$URL{sectionId}/", UUID.randomUUID().toString()))
            .andExpect(status().isNotFound)
    }

    @Test
    @WithMockUser(value = "spring", roles = [RoleType.USER, RoleType.ADMIN])
    fun `test section controller POST returns Created and the created section`() {

        this.mvc.perform(
            post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(section))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("\$.name").value(section.name))
    }

    @Test
    @WithMockUser(value = "spring", roles = [RoleType.USER, RoleType.ADMIN])
    fun `test section controller POST with negative capacity returns Created and the created section`() {
        sectionRepository.save(section.copy(capacity = -1))

        this.mvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(section)))
                .andExpect(status().isCreated)
                .andExpect(jsonPath("\$.name").value(section.name))
    }

    @Test
    @WithMockUser(value = "spring", roles = [RoleType.USER, RoleType.ADMIN])
    fun `test section controller PUT returns OK and the updated section`() {
        val name = "new name"
        section.name = name

        this.mvc.perform(
            put("$URL{sectionId}/", section.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(section))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("\$.name").value(name))
    }

    @Test
    @WithMockUser(value = "spring", roles = [RoleType.USER, RoleType.ADMIN])
    fun `test section controller PUT returns not found`() {
        this.mvc.perform(
            put("$URL{sectionId}/", UUID.randomUUID().toString())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(section))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    @WithMockUser(value = "spring", roles = [RoleType.USER, RoleType.ADMIN])
    fun `test section controller DELETE returns OK`() {
        this.mvc.perform(delete("$URL{sectionId}/", section.id))
            .andExpect(status().isOk)
    }

    @Test
    @WithMockUser(value = "spring", roles = [RoleType.USER, RoleType.ADMIN])
    fun `test section controller DELETE return NotFound`() {
        this.mvc.perform(delete("$URL{sectionId}/", UUID.randomUUID().toString()))
            .andExpect(status().isNotFound)
    }

    @Test
    @WithMockUser(value = "spring", roles = [RoleType.USER, RoleType.ADMIN])
    fun `test section controller POST with parentId returns http 200`() {
        val child = CreateSectionRequest(name = section.name, parentId = section.id, capacity = section.capacity - 1)

        this.mvc.perform(
            post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(child))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("\$.name").value(section.name))
    }

    @Test
    @WithMockUser(value = "spring", roles = [RoleType.USER, RoleType.ADMIN])
    fun `test section controller POST with parentId adds parent to child`() {
        val child = CreateSectionRequest(name = section.name, parentId = section.id, capacity = section.capacity - 1)

        val mvcResult = this.mvc.perform(
            post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(child))
        )
            .andReturn()

        val responseUUID: String = JsonPath.read(mvcResult.response.contentAsString, "\$.id")
        val actualId = UUID.fromString(responseUUID)
        val actualSection = sectionRepository.findById(actualId).get()
        val parent = sectionRepository.findById(section.id).get()

        assertThat(actualSection.parent?.id).isEqualTo(parent.id)
    }

    @Test
    @WithMockUser(value = "spring", roles = [RoleType.USER, RoleType.ADMIN])
    fun `test section controller POST with parentId adds child to parent`() {
        val child = CreateSectionRequest(name = section.name,
            parentId = section.id,
            capacity = section.capacity - 1)

        val mvcResult = this.mvc.perform(
            post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(child))
        )
            .andReturn()

        val responseUUID: String = JsonPath.read(mvcResult.response.contentAsString, "\$.id")
        val actualChildId = UUID.fromString(responseUUID)

        val parent = sectionRepository.findById(section.id).get()
        val parentHasActualChild = parent.children.stream().anyMatch { it.id == actualChildId }

        assertThat(parentHasActualChild).isTrue
    }

    @Test
    @WithMockUser(value = "spring", roles = [RoleType.USER, RoleType.ADMIN])
    fun `test section controller POST with child capacity greater than parent capacity returns http 400`() {
        val sectionCreateRequest = CreateSectionRequest(
            name = section.name,
            parentId = section.id,
            capacity = section.capacity + 10)

        mvc.perform(
            post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sectionCreateRequest))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("\$.message").isNotEmpty)
    }

    @Test
    @WithMockUser(value = "spring", roles = [RoleType.USER, RoleType.ADMIN])
    fun `test section controller POST with child capacity greater than parents accumulated preoccupation degree returns http 400`() {

        val child = SectionFactory().`object`
        section.children.add(child)
        sectionRepository.save(section)

        val sectionCreateRequest = CreateSectionRequest(
            name = section.name,
            parentId = section.id,
            capacity = section.capacity + 10)

        mvc.perform(
            post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sectionCreateRequest))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("\$.message").isNotEmpty)
    }

    @Test
    @WithMockUser(value = "spring", roles = [RoleType.USER, RoleType.ADMIN])
    fun `test section controller GET all returns OK and page of sections with time filter`() {
        var newReservation = UserReservationFactory().`object`
        userRepository.save(newReservation.user!!)
        sectionRepository.save(newReservation.section!!)
        newReservation.fromTime = ZonedDateTime.now().minusDays(50)
        newReservation.toTime = ZonedDateTime.now().minusDays(25)
        newReservation = reservationRepository.save(newReservation)

        this.mvc.perform(
            get(URL)
                .param("to", newReservation.toTime?.plusHours(1).toString())
                .param("from", newReservation.fromTime?.minusHours(1).toString())
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content.[*].name", hasItem(section.name)))
            .andExpect(jsonPath("$.content.[*].name", Matchers.not(newReservation.section?.name)))
    }

    @Test
    @WithMockUser(value = "spring", roles = [RoleType.USER, RoleType.ADMIN])
    fun `test section controller GET all returns OK and page of sections with search on name`() {
        val newSection = SectionFactory().`object`
        newSection.parent = section
        sectionRepository.save(newSection)
        this.mvc.perform(
            get(URL)
                .param("name", section.name)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content.[*].name", hasItem(section.name)))
            .andExpect(jsonPath("$.content.[*].name", Matchers.not(newSection.name)))
    }

    @Test
    @WithMockUser(value = "spring", roles = [RoleType.USER, RoleType.ADMIN])
    fun `test section controller GET all returns OK and page of sections with partial search on name`() {
        val newSection = SectionFactory().`object`
        newSection.parent = section
        sectionRepository.save(newSection)
        val length = section.name.length
        this.mvc.perform(
            get(URL)
                .param("name", section.name.substring(0, length - 1))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content.[*].name", hasItem(section.name)))
            .andExpect(jsonPath("$.content.[*].name", Matchers.not(newSection.name)))
    }

    @Test
    @WithMockUser(roles = [RoleType.ADMIN])
    fun `test statistics are returned for correct reservation`() {
        val newSection = SectionFactory().`object`
        newSection.parent = section
        sectionRepository.save(newSection)

        val reservation = UserReservationFactory().`object`
        reservation.section = newSection
        reservation.fromTime = ZonedDateTime.now().minusHours(1)
        reservation.toTime = ZonedDateTime.now().plusHours(1)
        newSection.userReservation.add(reservation)

        userRepository.save(reservation.user!!)
        reservationRepository.save(reservation)

        this.mvc.perform(
            get(getStatURL(newSection))
                .param("toTimeBefore", reservation.toTime!!.plusHours(1).toString())
                .param("fromTimeAfter", reservation.fromTime!!.minusHours(1).toString())
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.nrOfReservation").value(1))
            .andExpect(jsonPath("$.hoursOfReservation").value(2))
            .andExpect(jsonPath("$.daysWithReservation").value(1))
            .andExpect(jsonPath("$.userReservationCount").value(1))
    }

    @Test
    @WithMockUser(roles = [RoleType.ADMIN])
    fun `test statistics are not return when incorrect date is given`() {
        val newSection = SectionFactory().`object`
        newSection.parent = section
        sectionRepository.save(newSection)

        val reservation = UserReservationFactory().`object`
        reservation.section = newSection
        reservation.fromTime = ZonedDateTime.now().minusHours(1)
        reservation.toTime = ZonedDateTime.now().plusHours(1)
        newSection.userReservation.add(reservation)

        userRepository.save(reservation.user!!)
        reservationRepository.save(reservation)

        this.mvc.perform(
            get(getStatURL(newSection))
                .param("toTimeBefore", reservation.toTime!!.plusYears(1).toString())
                .param("fromTimeAfter", reservation.fromTime!!.plusYears(1).toString())
        )
            .andExpect(status().isOk)
    }

    @Test
    @WithMockUser(roles = [RoleType.ADMIN])
    fun `test statistics are returned correct when two reservations is within the span`() {
        val newSection = SectionFactory().`object`
        newSection.parent = section
        sectionRepository.save(newSection)

        val reservation1 = UserReservationFactory().`object`
        reservation1.section = newSection
        reservation1.fromTime = ZonedDateTime.now().minusHours(1)
        reservation1.toTime = ZonedDateTime.now().plusHours(1)
        newSection.userReservation.add(reservation1)

        val reservation2 = UserReservationFactory().`object`
        reservation2.section = newSection
        reservation2.fromTime = ZonedDateTime.now().plusHours(2)
        reservation2.toTime = ZonedDateTime.now().plusHours(4)
        newSection.userReservation.add(reservation2)

        userRepository.save(reservation1.user!!)
        reservationRepository.save(reservation1)
        userRepository.save(reservation2.user!!)
        reservationRepository.save(reservation2)

        this.mvc.perform(
            get(getStatURL(newSection))
                .param("toTimeBefore", ZonedDateTime.now().plusDays(1).toString())
                .param("fromTimeAfter", ZonedDateTime.now().minusDays(1).toString())
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.nrOfReservation").value(2))
            .andExpect(jsonPath("$.hoursOfReservation").value(4))
            .andExpect(jsonPath("$.daysWithReservation").value(1))
            .andExpect(jsonPath("$.userReservationCount").value(2))
    }

    @Test
    @WithMockUser(roles = [RoleType.ADMIN])
    fun `test statistics are returned correct when two reservations in different sections is within the span`() {
        val correctSection = SectionFactory().`object`
        correctSection.parent = section
        sectionRepository.save(correctSection)

        val wrongSection = SectionFactory().`object`
        wrongSection.parent = section
        sectionRepository.save(wrongSection)

        val correctReservation = UserReservationFactory().`object`
        correctReservation.section = correctSection
        correctReservation.fromTime = ZonedDateTime.now().minusHours(1)
        correctReservation.toTime = ZonedDateTime.now().plusHours(1)
        correctSection.userReservation.add(correctReservation)

        val wrongReservation = UserReservationFactory().`object`
        wrongReservation.section = wrongSection
        wrongReservation.fromTime = ZonedDateTime.now().plusHours(2)
        wrongReservation.toTime = ZonedDateTime.now().plusHours(4)
        wrongSection.userReservation.add(wrongReservation)

        userRepository.save(correctReservation.user!!)
        reservationRepository.save(correctReservation)
        userRepository.save(wrongReservation.user!!)
        reservationRepository.save(wrongReservation)

        this.mvc.perform(
            get(getStatURL(correctSection))
                .param("toTimeBefore", ZonedDateTime.now().plusDays(1).toString())
                .param("fromTimeAfter", ZonedDateTime.now().minusDays(1).toString())
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.nrOfReservation").value(1))
            .andExpect(jsonPath("$.hoursOfReservation").value(2))
            .andExpect(jsonPath("$.daysWithReservation").value(1))
            .andExpect(jsonPath("$.userReservationCount").value(1))
    }

    @Test
    @WithMockUser(roles = [RoleType.ADMIN])
    fun `test statistics are returned correct when multiple reservations is over a big span`() {
        val newSection = SectionFactory().`object`
        newSection.parent = section
        sectionRepository.save(newSection)

        val reservation1 = UserReservationFactory().`object`
        reservation1.section = newSection
        reservation1.fromTime = ZonedDateTime.now().minusHours(1)
        reservation1.toTime = ZonedDateTime.now().plusHours(1)
        newSection.userReservation.add(reservation1)

        val reservation2 = UserReservationFactory().`object`
        reservation2.section = newSection
        reservation2.fromTime = ZonedDateTime.now().plusDays(1).plusHours(2)
        reservation2.toTime = ZonedDateTime.now().plusDays(1).plusHours(4)
        newSection.userReservation.add(reservation2)

        val reservation3 = UserReservationFactory().`object`
        reservation3.section = newSection
        reservation3.fromTime = ZonedDateTime.now().plusMonths(1).plusHours(2)
        reservation3.toTime = ZonedDateTime.now().plusMonths(1).plusHours(4)
        newSection.userReservation.add(reservation3)

        val reservation4 = UserReservationFactory().`object`
        reservation4.section = newSection
        reservation4.fromTime = ZonedDateTime.now().minusMonths(1).plusHours(2)
        reservation4.toTime = ZonedDateTime.now().minusMonths(1).plusHours(4)
        newSection.userReservation.add(reservation4)

        userRepository.save(reservation1.user!!)
        reservationRepository.save(reservation1)
        userRepository.save(reservation2.user!!)
        reservationRepository.save(reservation2)
        userRepository.save(reservation3.user!!)
        reservationRepository.save(reservation3)
        userRepository.save(reservation4.user!!)
        reservationRepository.save(reservation4)

        this.mvc.perform(
            get(getStatURL(newSection))
                .param("toTimeBefore", ZonedDateTime.now().plusYears(1).toString())
                .param("fromTimeAfter", ZonedDateTime.now().minusYears(1).toString())
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.nrOfReservation").value(4))
            .andExpect(jsonPath("$.hoursOfReservation").value(8))
            .andExpect(jsonPath("$.daysWithReservation").value(4))
            .andExpect(jsonPath("$.userReservationCount").value(4))
    }

    @Test
    @WithMockUser(roles = [RoleType.ADMIN])
    fun `test statistics are returned only for reservations within the correct big span`() {
        val newSection = SectionFactory().`object`
        newSection.parent = section
        sectionRepository.save(newSection)

        val reservation1 = UserReservationFactory().`object`
        reservation1.section = newSection
        reservation1.fromTime = ZonedDateTime.now().minusHours(1)
        reservation1.toTime = ZonedDateTime.now().plusHours(1)
        newSection.userReservation.add(reservation1)

        val reservation2 = UserReservationFactory().`object`
        reservation2.section = newSection
        reservation2.fromTime = ZonedDateTime.now().plusDays(1).plusHours(2)
        reservation2.toTime = ZonedDateTime.now().plusDays(1).plusHours(4)
        newSection.userReservation.add(reservation2)

        val wrongReservation1 = UserReservationFactory().`object`
        wrongReservation1.section = newSection
        wrongReservation1.fromTime = ZonedDateTime.now().plusYears(2).plusHours(2)
        wrongReservation1.toTime = ZonedDateTime.now().plusYears(2).plusHours(4)
        newSection.userReservation.add(wrongReservation1)

        val wrongReservation2 = UserReservationFactory().`object`
        wrongReservation2.section = newSection
        wrongReservation2.fromTime = ZonedDateTime.now().minusYears(2).plusHours(2)
        wrongReservation2.toTime = ZonedDateTime.now().minusYears(2).plusHours(4)
        newSection.userReservation.add(wrongReservation2)

        userRepository.save(reservation1.user!!)
        reservationRepository.save(reservation1)
        userRepository.save(reservation2.user!!)
        reservationRepository.save(reservation2)
        userRepository.save(wrongReservation1.user!!)
        reservationRepository.save(wrongReservation1)
        userRepository.save(wrongReservation2.user!!)
        reservationRepository.save(wrongReservation2)

        this.mvc.perform(
            get(getStatURL(newSection))
                .param("toTimeBefore", ZonedDateTime.now().plusYears(1).toString())
                .param("fromTimeAfter", ZonedDateTime.now().minusYears(1).toString())
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.nrOfReservation").value(2))
            .andExpect(jsonPath("$.hoursOfReservation").value(4))
            .andExpect(jsonPath("$.daysWithReservation").value(2))
            .andExpect(jsonPath("$.userReservationCount").value(2))
    }
}
