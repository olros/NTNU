package ntnu.idatt2105.util

import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.Expressions
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

class JpaUtils {

    fun getDefaultPageable(): Pageable {
        return PageRequest.of(0, 25)
    }

    fun getEmptyPredicate(): Predicate {
        return Expressions.asBoolean(true).isTrue
    }
}
