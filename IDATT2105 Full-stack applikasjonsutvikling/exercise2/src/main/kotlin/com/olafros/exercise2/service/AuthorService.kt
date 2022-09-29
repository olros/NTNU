package com.olafros.exercise2.service

import com.olafros.exercise2.controller.AuthorController
import com.olafros.exercise2.exception.NotFoundException
import com.olafros.exercise2.model.*
import com.olafros.exercise2.repository.AddressRepository
import com.olafros.exercise2.repository.AuthorRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class AuthorService(
    val authorRepository: AuthorRepository,
    val addressRepository: AddressRepository,
) {

    companion object {
        private val logger = LoggerFactory.getLogger(AuthorController::class.java)
    }

    fun getAuthorById(authorId: Long): Author {
        val author = authorRepository.findAuthorById(authorId)
        return if (author != null) {
            author
        } else {
            logger.warn("Could not find author by id: $authorId")
            throw NotFoundException("Could not find the author")
        }
    }

    fun getAuthors(name: String?): List<Author> {
        val authors = if (name == null) authorRepository.findAll() else {
            logger.info("Search for author by name: $name")
            authorRepository.findAuthorsByNameContains(name)
        }
        return authors
    }

    fun createNewAuthor(newAuthor: CreateAuthorDto): Author {
        val newAddress = Address(
            0,
            newAuthor.address.street,
            newAuthor.address.post_nr,
            newAuthor.address.city,
            newAuthor.address.country,
            null
        )
        val address = addressRepository.save(newAddress)
        val author = Author(0, newAuthor.name, address, mutableListOf())
        return authorRepository.save(author)
    }

    fun updateAuthorById(
        authorId: Long,
        updatedAuthor: UpdateAuthorDto
    ): Author {
        val author = authorRepository.findAuthorById(authorId)
        return if (author != null) {
            val address = if (updatedAuthor.address != null) Address(
                0,
                updatedAuthor.address.street,
                updatedAuthor.address.post_nr,
                updatedAuthor.address.city,
                updatedAuthor.address.country,
                author
            ) else author.address
            val newAuthor = author.copy(
                name = updatedAuthor.name ?: author.name,
                address = address,
            )
            authorRepository.save(newAuthor)
        } else {
            logger.warn("Could not find author by id: $authorId")
            throw NotFoundException("Could not find the team to update")
        }
    }

    fun deleteAuthorById(authorId: Long): SuccessResponse {
        val author = authorRepository.findAuthorById(authorId)
        return if (author != null) {
            authorRepository.delete(author)
            SuccessResponse("Author successfully deleted")
        } else {
            logger.warn("Could not find author by id: $authorId")
            throw NotFoundException("Could not find the author to delete")
        }
    }
}