package org.angigaram.repositories

import org.angigaram.entities.UserEntity
import org.springframework.data.repository.CrudRepository


interface UserRepository : CrudRepository<UserEntity, String> {
    fun findByGuid(guid: String): UserEntity
}