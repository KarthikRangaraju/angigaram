package org.angigaram.services

import org.angigaram.entities.UserEntity
import org.angigaram.models.User
import org.angigaram.repositories.UserRepository
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
class UserService(val userRepository: UserRepository, val slackClientService: SlackClientService) {

    fun findByGuid(guid: String): User? {
        return try {
            val userEntity = userRepository.findByGuid(guid)
            User(userEntity.guid, userEntity.name, userEntity.timeZone, userEntity.photoUrl)
        } catch (e: Exception) {
            null
        }
    }

    fun save(user: User): User {
        userRepository.save(UserEntity(user.guid, user.name, user.timeZone, user.photoUrl, ZonedDateTime.now(),
                ZonedDateTime.now()))
        return user
    }
}