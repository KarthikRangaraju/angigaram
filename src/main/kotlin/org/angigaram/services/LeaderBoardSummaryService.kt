package org.angigaram.services

import org.angigaram.entities.LeaderBoardSummaryEntity
import org.angigaram.models.BadgeSummary
import org.angigaram.models.LeaderBoardSummary
import org.angigaram.repositories.BadgeTransactionRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDate

@Service
class LeaderBoardSummaryService(val badgeTransactionRepository: BadgeTransactionRepository,
                                val badgeTransactionsService: BadgeTransactionsService,
                                val userService: UserService) {

    fun getDailyLeaderBoard(pageable: Pageable): Page<LeaderBoardSummary> {
        val leaderBoardSummaryEntityPage = badgeTransactionRepository
                .getLeaderBoardSummary(LocalDate.now(), LocalDate.now(), pageable)
        return convertToLeaderBoardSummaryPage(leaderBoardSummaryEntityPage)
    }

    fun getWeeklyLeaderBoard(pageable: Pageable): Page<LeaderBoardSummary> {
        val leaderBoardSummaryEntityPage = badgeTransactionRepository
                .getLeaderBoardSummary(LocalDate.now().with(DayOfWeek.MONDAY), LocalDate.now(), pageable)
        return convertToLeaderBoardSummaryPage(leaderBoardSummaryEntityPage)
    }

    fun getMonthlyLeaderBoard(pageable: Pageable): Page<LeaderBoardSummary> {
        val leaderBoardSummaryEntityPage = badgeTransactionRepository
                .getLeaderBoardSummary(LocalDate.now().withDayOfMonth(1), LocalDate.now(), pageable)
        return convertToLeaderBoardSummaryPage(leaderBoardSummaryEntityPage)
    }

    fun getYearlyLeaderBoard(pageable: Pageable): Page<LeaderBoardSummary> {
        val leaderBoardSummaryEntityPage = badgeTransactionRepository
                .getLeaderBoardSummary(LocalDate.now().withDayOfYear(1), LocalDate.now(), pageable)
        return convertToLeaderBoardSummaryPage(leaderBoardSummaryEntityPage)
    }

    private fun convertToLeaderBoardSummaryPage(leaderBoardSummaryEntityPage: Page<LeaderBoardSummaryEntity>): Page<LeaderBoardSummary> {
        return leaderBoardSummaryEntityPage.map {
            val badgeToBadgeCountMap: MutableMap<String, Int> = HashMap()
            it.getBadges().split(",").stream().forEach { badge ->
                badgeToBadgeCountMap[badge]?.let { badgeToBadgeCountMap[badge] = it + 1 }
                        ?: badgeToBadgeCountMap.put(badge, 1)
            }

            val badgeSummary = badgeToBadgeCountMap.map { BadgeSummary(it.key, it.value) }.toList()

            val user = userService.findByGuid(it.getUserGuid()) ?: throw IllegalStateException("Unable to find user " +
                    "with userGuid: ${it.getUserGuid()}")
            val badgesRemainingForTheDay = badgeTransactionsService.getBadgesRemaining(user)

            LeaderBoardSummary(it.getUserGuid(), it.getName(), it.getPhotoUrl(), it.getTotalBadges(),
                    badgesRemainingForTheDay, badgeSummary)
        }
    }
}