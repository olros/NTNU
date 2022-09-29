package com.olafros.exercise2.repository

import com.olafros.exercise2.model.Address
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AddressRepository : JpaRepository<Address, Long> {
    fun findAddressById(id: Long?): Address?
}