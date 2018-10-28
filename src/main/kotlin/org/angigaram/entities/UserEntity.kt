package org.angigaram.entities

import java.time.ZonedDateTime
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "users")
data class UserEntity(
    @Id val guid: String,
    val name: String,
    val timeZone: String,
    val photoUrl: String,
    val createdAt: ZonedDateTime,
    val updatedAt: ZonedDateTime
)