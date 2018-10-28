package org.angigaram.entities

interface LeaderBoardSummaryEntity {
    fun getUserGuid(): String
    fun getName(): String
    fun getPhotoUrl(): String
    fun getTotalBadges(): Int
    fun getBadges(): String
}