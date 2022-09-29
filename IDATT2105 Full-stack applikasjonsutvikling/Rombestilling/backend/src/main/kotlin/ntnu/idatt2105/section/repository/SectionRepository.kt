package ntnu.idatt2105.section.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.JPAExpressions
import ntnu.idatt2105.reservation.model.QReservation
import ntnu.idatt2105.section.model.QSection
import ntnu.idatt2105.section.model.Section
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer
import org.springframework.data.querydsl.binding.QuerydslBindings
import java.util.*

interface SectionRepository : JpaRepository<Section, UUID>, QuerydslPredicateExecutor<Section>,
    QuerydslBinderCustomizer<QSection> {

    @JvmDefault
    override fun customize(bindings: QuerydslBindings, section: QSection) {
        bindings.bind(section.name).first { _, value -> section.name.contains(value) }
        bindings.bind(section.interval).all { _, values ->
            val predicate = BooleanBuilder()
            if (values.size == 2) {
                val sortedList = values.sorted()
                val from = sortedList.elementAt(0)
                val to = sortedList.elementAt(1)
                val reservation = QReservation.reservation
                val conflictingReservations = JPAExpressions.selectFrom(reservation)
                    .where(reservation.fromTime.before(to))
                    .where(reservation.toTime.after(from))
                    .select(reservation.section.id)
                predicate.and(
                    section.id.`in`(
                        conflictingReservations
                    )
                    .or(
                        section.parent.id.isNotNull.and(
                            section.parent.id.`in`(
                                conflictingReservations
                            )
                        )
                    )
                    .or(
                        section.children.any().id.`in`(
                            conflictingReservations
                        )
                    )
                ).not()
            }
            Optional.of(predicate)
        }
    }
}
