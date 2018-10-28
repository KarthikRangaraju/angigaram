package org.angigaram.repositories

import org.angigaram.entities.BadgeTransactionEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface BadgeTransactionRepository : CrudRepository<BadgeTransactionEntity, String> {

    @Query(
        "SELECT COUNT(*) FROM badge_transactions WHERE from_user_guid = :fromUserGuid" +
                " AND DATE_FORMAT(CONVERT_TZ(created_at, '+00:00', :localTimeZoneOffset), '%Y-%m-%d') = :localDate",
        nativeQuery = true
    )
    fun getBadgesAlreadyGivenForTheDay(
        @Param("fromUserGuid") fromUserGuid: String,
        @Param("localTimeZoneOffset") localTimeZoneOffset: String,
        @Param("localDate") localDate: LocalDate
    ): Int

    @Query(
        "SELECT COUNT(*) FROM badge_transactions WHERE to_user_guid = :toUserGuid" +
                " AND DATE_FORMAT(CONVERT_TZ(created_at, '+00:00', :localTimeZoneOffset), '%Y-%m-%d') = :localDate",
        nativeQuery = true
    )
    fun getBadgesReceivedForTheDay(
        @Param("toUserGuid") toUserGuid: String,
        @Param("localTimeZoneOffset") localTimeZoneOffset: String,
        @Param("localDate") localDate: LocalDate
    ): Int

    @Query("SELECT * FROM badge_transactions WHERE transaction_id = :transactionId LIMIT 1", nativeQuery = true)
    fun findAtleastOneByTransactionId(@Param("transactionId") transactionId: String): BadgeTransactionEntity?
}