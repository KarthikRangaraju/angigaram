package org.angigaram.repositories

import org.angigaram.entities.BadgeTransactionEntity
import org.angigaram.entities.LeaderBoardSummaryEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface BadgeTransactionRepository : CrudRepository<BadgeTransactionEntity, String> {

    @Query("SELECT COUNT(*) FROM badge_transactions WHERE from_user_guid = :fromUserGuid" +
            " AND DATE_FORMAT(CONVERT_TZ(created_at, '+00:00', :localTimeZoneOffset), '%Y-%m-%d') = :localDate",
            nativeQuery = true)
    fun getBadgesAlreadyGivenForTheDay(@Param("fromUserGuid") fromUserGuid: String,
                                       @Param("localTimeZoneOffset") localTimeZoneOffset: String,
                                       @Param("localDate") localDate: LocalDate): Int

    @Query("SELECT COUNT(*) FROM badge_transactions WHERE to_user_guid = :toUserGuid" +
            " AND DATE_FORMAT(CONVERT_TZ(created_at, '+00:00', :localTimeZoneOffset), '%Y-%m-%d') = :localDate",
            nativeQuery = true)
    fun getBadgesReceivedForTheDay(@Param("toUserGuid") toUserGuid: String,
                                   @Param("localTimeZoneOffset") localTimeZoneOffset: String,
                                   @Param("localDate") localDate: LocalDate): Int

    @Query("SELECT * FROM badge_transactions WHERE transaction_id = :transactionId LIMIT 1", nativeQuery = true)
    fun findAtleastOneByTransactionId(@Param("transactionId") transactionId: String): BadgeTransactionEntity?

    @Query("SELECT " +
            " to_user_guid AS userGuid," +
            " u.name," +
            " u.photo_url AS photoUrl," +
            " COUNT(*) AS totalBadges," +
            " GROUP_CONCAT(badge) AS badges " +
            "  FROM badge_transactions bt" +
            " JOIN users u ON u.guid = bt.to_user_guid" +
            " WHERE DATE(bt.created_at) BETWEEN :fromDate AND :toDate " +
            " GROUP BY bt.to_user_guid" +
            " ORDER BY COUNT(*) DESC",
            countQuery = "SELECT COUNT(*) FROM badge_transactions " +
                    " WHERE DATE(created_at) BETWEEN :fromDate AND :toDate " +
                    " GROUP BY to_user_guid", nativeQuery = true)
    fun getLeaderBoardSummary(@Param("fromDate") fromDate: LocalDate,
                              @Param("toDate") toDate: LocalDate, pageable: Pageable): Page<LeaderBoardSummaryEntity>
}