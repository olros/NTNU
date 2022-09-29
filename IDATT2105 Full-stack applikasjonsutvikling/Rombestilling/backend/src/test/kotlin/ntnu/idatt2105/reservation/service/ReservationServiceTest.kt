package ntnu.idatt2105.reservation.service

import com.querydsl.core.types.Predicate
import io.github.serpro69.kfaker.Faker
import ntnu.idatt2105.factories.UserReservationFactory
import ntnu.idatt2105.group.service.GroupServiceImpl
import ntnu.idatt2105.reservation.dto.CreateUserReservationRequest
import ntnu.idatt2105.reservation.model.Reservation
import ntnu.idatt2105.reservation.model.UserReservation
import ntnu.idatt2105.reservation.repository.ReservationRepository
import ntnu.idatt2105.section.repository.SectionRepository
import ntnu.idatt2105.user.model.User
import ntnu.idatt2105.user.service.UserServiceImpl
import ntnu.idatt2105.util.JpaUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import java.util.*

@ExtendWith(MockitoExtension::class)
class ReservationServiceTest {

    private lateinit var reservationService: ReservationServiceImpl

    @Mock
    private lateinit var sectionRepository: SectionRepository

    @Mock
    private lateinit var reservationRepository: ReservationRepository

    @InjectMocks
    private lateinit var reserverServiceResolver: ReserverServiceResolverImpl

    @Mock
    private lateinit var userService: UserServiceImpl

    @Mock
    private lateinit var groupService: GroupServiceImpl

    private lateinit var reservation: UserReservation

    private val faker = Faker()

    @BeforeEach
    fun setUp() {
        reservationService = ReservationServiceImpl(reservationRepository, sectionRepository, reserverServiceResolver)
        reservation = UserReservationFactory().`object`

        lenient().`when`(reservationRepository.findById(reservation.id)).thenReturn(Optional.of(reservation))
        lenient().`when`(sectionRepository.findById(reservation.section?.id!!)).thenReturn(Optional.of(reservation.section!!))
        lenient().`when`(userService.getUser(reservation.user?.id!!, User::class.java)).thenReturn(reservation.user!!)
        lenient().`when`(reservationRepository.save(any(UserReservation::class.java))).thenReturn(reservation)
    }

    @Test
    fun `test get all reservation gets a list of reservations`() {
        val page = JpaUtils().getDefaultPageable()
        val predicate = JpaUtils().getEmptyPredicate()

        val testList: List<Reservation> = mutableListOf(reservation)
        val reservations: Page<Reservation> = PageImpl(testList, page, testList.size.toLong())

        lenient().`when`(reservationRepository.findAll(any(Predicate::class.java), eq(page)))
                .thenReturn(reservations)

        val content = reservationService.getAllReservation(reservation.section?.id!!, page, predicate).content

        assertThat(content.size).isEqualTo(testList.size)
    }

    @Test
    fun `test create reservation creates a reservation`() {
        lenient().`when`(reservationRepository.existsInterval(reservation.fromTime!!, reservation.toTime!!, reservation.section!!.id)).thenReturn(false)
        val text = faker.aquaTeenHungerForce.quote()
        reservation.text = text
        val newReservation = CreateUserReservationRequest(entityId = reservation.user?.id,
                sectionId = reservation.section?.id,
                fromTime = reservation.fromTime?.plusHours(2),
                toTime = reservation.toTime,
                text = reservation.text,
                nrOfPeople = reservation.nrOfPeople)
        `when`(userService.getReserverById(newReservation.entityId!!)).thenReturn(reservation.user)
        lenient().`when`(reservationRepository.save(any(UserReservation::class.java))).thenReturn(reservation)
        val test = reservationService.createReservation(reservation.section?.id!!, newReservation)

        assertThat(test.text).isEqualTo(reservation.text)
    }
}
