package ntnu.idatt2105.group.service

import com.querydsl.core.types.ExpressionUtils
import com.querydsl.core.types.Predicate
import ntnu.idatt2105.core.exception.ApplicationException
import ntnu.idatt2105.core.exception.EntityType
import ntnu.idatt2105.core.exception.ExceptionType
import ntnu.idatt2105.core.response.Response
import ntnu.idatt2105.core.util.CsvToBean.Companion.closeFileReader
import ntnu.idatt2105.core.util.CsvToBean.Companion.createCSVToBean
import ntnu.idatt2105.core.util.CsvToBean.Companion.throwIfFileEmpty
import ntnu.idatt2105.group.repository.GroupRepository
import ntnu.idatt2105.user.dto.UserEmailDto
import ntnu.idatt2105.user.dto.UserListDto
import ntnu.idatt2105.user.dto.toUserListDto
import ntnu.idatt2105.user.model.QUser
import ntnu.idatt2105.user.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.IllegalStateException
import java.util.*

@Service
class MembershipServiceImpl(val groupRepository: GroupRepository, val userRepository: UserRepository) : MembershipService {

    private fun getGroup(id: UUID) = groupRepository.findById(id).orElseThrow { throw ApplicationException.throwException(
            EntityType.GROUP, ExceptionType.ENTITY_NOT_FOUND, id.toString()) }

    private fun getUser(email: String) = userRepository.findByEmail(email).run {
        if (this != null) return@run this
        throw ApplicationException.throwException(
                EntityType.USER,
                ExceptionType.ENTITY_NOT_FOUND,
                email)
    }

    private fun getUser(id: UUID) = userRepository.findById(id).orElseThrow { throw ApplicationException.throwException(
            EntityType.USER,
            ExceptionType.ENTITY_NOT_FOUND,
            id.toString()) }

    override fun getMemberships(groupId: UUID, predicate: Predicate, pageable: Pageable): Page<UserListDto> =
        getGroup(groupId).run {
            val user = QUser.user
            val newPredicate = ExpressionUtils.allOf(predicate, user.groups.any().id.eq(this.id))!!
            return userRepository.findAll(newPredicate, pageable).map { it.toUserListDto() }
        }

    @Transactional
    override fun createMemberships(groupId: UUID, userEmail: UserEmailDto, predicate: Predicate, pageable: Pageable): Page<UserListDto> {
        groupRepository.findById(groupId).orElseThrow { throw ApplicationException.throwException(
                EntityType.GROUP, ExceptionType.ENTITY_NOT_FOUND, groupId.toString()) }
                .run {
                    val member = getUser(userEmail.email)
                    member.groups.add(this)
                    userRepository.save(member)
                    val user = QUser.user
                    val newPredicate = ExpressionUtils.allOf(predicate, user.groups.any().id.eq(this.id))!!
                    return userRepository.findAll(newPredicate, pageable).map { it.toUserListDto() }
        }
    }

    @Transactional
    override fun deleteMembership(groupId: UUID, userId: UUID) {
        groupRepository.findById(groupId).orElseThrow { throw ApplicationException.throwException(
                EntityType.GROUP, ExceptionType.ENTITY_NOT_FOUND, groupId.toString()) }
                .run {
                    val member = getUser(userId)
                    member.groups.remove(this)
                    userRepository.save(member)
                }
    }

    override fun createMembershipBatch(
        file: MultipartFile,
        groupId: UUID
    ): Response {
        val group = groupRepository.findById(groupId).orElseThrow { throw ApplicationException.throwException(EntityType.GROUP, ExceptionType.ENTITY_NOT_FOUND, groupId.toString()) }
        throwIfFileEmpty(file)
        var fileReader: BufferedReader? = null
        val invalidUsers = mutableListOf<UserEmailDto>()

        try {
            fileReader = BufferedReader(InputStreamReader(file.inputStream))
            val csvToBean = createCSVToBean(fileReader, UserEmailDto::class.java)
            val listOfDto: List<UserEmailDto> = csvToBean.parse()
            if (listOfDto.isEmpty()) throw ApplicationException.throwException(EntityType.GROUP, ExceptionType.ENTITY_NOT_FOUND, groupId.toString())

            listOfDto.forEach {
                try {
                    val user = getUser(it.email)
                    user.groups.add(group)
                    userRepository.save(user)
                } catch (ex: Exception) {
                    invalidUsers.add(it)
                }
            }
            if (invalidUsers.isNotEmpty()) return Response("${invalidUsers.map { it.email }}, tilhører ingen lagret brukere")
            return Response("Medlemskapene ble opprettet")
        } catch (ex: IllegalStateException) {
            throw ApplicationException.throwExceptionWithId(
                EntityType.USER,
                ExceptionType.NOT_VALID,
                "batch.invalidFile"
            )
        } finally {
            closeFileReader(fileReader)
        }
    }
}
