package org.angigaram.models

data class LeaderBoardSummary(val userGuid: String,
                              val name: String,
                              val photoUrl: String,
                              val totalBadges: Int,
                              val badgesRemainingForToday: Int,
                              val badgeSummaries: List<BadgeSummary>)