package org.angigaram.entities

import java.time.ZonedDateTime
import javax.persistence.*

@Entity
@Table(name = "badge_transactions")
data class BadgeTransactionEntity(@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
                                  val id: Int = 0,
                                  val transactionId: String,
                                  val fromUserGuid: String,
                                  val toUserGuid: String,
                                  var badge: String,
                                  val createdAt: ZonedDateTime,
                                  val updatedAt: ZonedDateTime)