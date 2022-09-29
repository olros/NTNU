package ntnu.idatt2105.user.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.querydsl.core.annotations.PropertyType
import com.querydsl.core.annotations.QueryType
import ntnu.idatt2105.group.model.Group
import ntnu.idatt2105.reservation.model.Reserver
import ntnu.idatt2105.security.token.PasswordResetToken
import java.time.LocalDate
import java.util.*
import javax.persistence.*

@Entity
data class User(
    @Id
    @Column(columnDefinition = "CHAR(32)")
    var id: UUID = UUID.randomUUID(),
    var firstName: String = "",
    var surname: String = "",
    @Column(unique = true)
    var email: String = "",
    var phoneNumber: String = "",
    var image: String = "",
    var expirationDate: LocalDate = LocalDate.now().plusYears(1),
    var password: String = "",
    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.MERGE, CascadeType.PERSIST])
    @JoinTable(name = "user_roles",
            joinColumns = [JoinColumn(name = "USER_ID", referencedColumnName = "id")],
            inverseJoinColumns = [JoinColumn(name = "ROLE_ID", referencedColumnName = "id")])
    var roles: MutableSet<Role> = mutableSetOf(),
    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL])
    val pwdToken: PasswordResetToken? = null,
    @ManyToMany(cascade = [CascadeType.MERGE, CascadeType.PERSIST], fetch = FetchType.LAZY)
    @JoinTable(name = "group_user",
    joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
    inverseJoinColumns = [JoinColumn(name = "group_id", referencedColumnName = "id")],
    uniqueConstraints = [UniqueConstraint(columnNames = ["group_id", "user_id"])])
    @JsonIgnore
    var groups: MutableList<Group> = mutableListOf()
) : Reserver {

    fun isAdmin() = roles.any { it.name == RoleType.ADMIN }

    @Transient
    @QueryType(PropertyType.STRING)
    val search: String? = null
}
