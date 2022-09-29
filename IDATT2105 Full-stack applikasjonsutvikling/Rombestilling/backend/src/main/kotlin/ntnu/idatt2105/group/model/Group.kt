package ntnu.idatt2105.group.model

import ntnu.idatt2105.reservation.model.Reserver
import ntnu.idatt2105.user.model.User
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "group_table")
data class Group(
    @Id
    @Column(columnDefinition = "CHAR(32)")
    var id: UUID = UUID.randomUUID(),
    var name: String = "",
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "group_user",
            joinColumns = [JoinColumn(name = "group_id", referencedColumnName = "id")],
            inverseJoinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")])
    var members: MutableSet<User> = mutableSetOf(),
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    var creator: User,
) : Reserver {
        fun isMember(): Boolean {
                val user = SecurityContextHolder.getContext()?.authentication?.principal as UserDetails? ?: return false
                return containsContextUser(user) || this.creator.email == user.username
        }
        private fun containsContextUser(user: UserDetails): Boolean {
                return this.members.firstOrNull { it.email == user.username } != null
        }
}
