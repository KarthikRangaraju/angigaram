package org.angigaram.controllers

import org.angigaram.models.LeaderBoardSummary
import org.angigaram.services.LeaderBoardSummaryService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("leaderboards")
class LeaderBoardController(val leaderBoardSummaryService: LeaderBoardSummaryService) {

    @GetMapping("/daily")
    fun getDaily(@PageableDefault(size = 5) pageable: Pageable, model: Model): String {
        val page: Page<LeaderBoardSummary> = leaderBoardSummaryService.getDailyLeaderBoard(pageable)
        model["page"] = page
        model["title"] = "Daily Leaderboard"
        model["path"] = "daily"
        return "leaderboard"
    }

    @GetMapping("/weekly")
    fun getWeekly(@PageableDefault(size = 5) pageable: Pageable, model: Model): String {
        val page: Page<LeaderBoardSummary> = leaderBoardSummaryService.getWeeklyLeaderBoard(pageable)
        model["page"] = page
        model["title"] = "Weekly Leaderboard"
        model["path"] = "weekly"
        return "leaderboard"
    }

    @GetMapping("/monthly")
    fun getMonthly(@PageableDefault(size = 5) pageable: Pageable, model: Model): String {
        val page: Page<LeaderBoardSummary> = leaderBoardSummaryService.getMonthlyLeaderBoard(pageable)
        model["page"] = page
        model["title"] = "Monthly Leaderboard"
        model["path"] = "monthly"
        return "leaderboard"
    }

    @GetMapping("/yearly")
    fun getYearly(@PageableDefault(size = 5) pageable: Pageable, model: Model): String {
        val page: Page<LeaderBoardSummary> = leaderBoardSummaryService.getYearlyLeaderBoard(pageable)
        model["page"] = page
        model["title"] = "Yearly Leaderboard"
        model["path"] = "yearly"
        return "leaderboard"
    }
}