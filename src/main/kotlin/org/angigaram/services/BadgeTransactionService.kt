package org.angigaram.services

import org.angigaram.entities.BadgeTransactionEntity
import org.angigaram.events.EmptyBadgesForTheDayEvent
import org.angigaram.models.User
import org.angigaram.repositories.BadgeTransactionRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.stream.Collectors
import javax.transaction.Transactional

@Service
class BadgeTransactionsService(
    val badgeTransactionRepository: BadgeTransactionRepository,
    @Value("\${org.angigaram.supported-badges}") val supportedBadges: Array<String>,
    @Value("\${org.angigaram.total-daily-badges}") val totalDailyBadges: Int,
    val applicationEventPublisher: ApplicationEventPublisher
) {

    @Transactional
    fun process(transactionId: String,
                sourceId: String,
                fromUser: User,
                toUser: User,
                badges: List<String>) {
        if (fromUser.guid == toUser.guid) {
            return
        }
        // Ensuring idempotency. If the transaction happened already before just return.
        if (badgeTransactionRepository.findAtleastOneByTransactionId(transactionId) != null) {
            return
        }

        val fromUserZonedDateTime = ZonedDateTime.now(ZoneId.of(fromUser.timeZone))
        val badgesAlreadyGivenForTheDay = badgeTransactionRepository
            .getBadgesAlreadyGivenForTheDay(
                fromUser.guid, fromUserZonedDateTime.offset.toString(),
                fromUserZonedDateTime.toLocalDate()
            )

        if (badgesAlreadyGivenForTheDay == totalDailyBadges) {
            applicationEventPublisher.publishEvent(EmptyBadgesForTheDayEvent(fromUser.guid, sourceId))
            return
        }

        val badgesRemaining = totalDailyBadges - badgesAlreadyGivenForTheDay
        val nowInUTC = ZonedDateTime.now(ZoneId.of("UTC"))

        val badgeTransactions = badges
            .stream()
            .filter { supportedBadges.contains(it) }
            .limit(badgesRemaining.toLong())
            .map {
                BadgeTransactionEntity(0, transactionId,
                    fromUser.guid, toUser.guid, it, nowInUTC, nowInUTC
                )
            }
            .collect(Collectors.toList())

        badgeTransactionRepository.saveAll(badgeTransactions)
    }

    fun getBadgesRemaining(user: User): Int {
        val userZonedDateTime = ZonedDateTime.now(ZoneId.of(user.timeZone))

        val badgesAlreadyGivenForTheDay = badgeTransactionRepository.getBadgesAlreadyGivenForTheDay(
            user.guid,
            userZonedDateTime.offset.toString(), userZonedDateTime.toLocalDate()
        )

        return totalDailyBadges - badgesAlreadyGivenForTheDay
    }
}