package org.angigaram.listeners

import org.angigaram.events.EmptyBadgesForTheDayEvent
import org.angigaram.services.SlackClientService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener

@Component
class BadgeTransactionEventsListener(val slackClientService: SlackClientService,
                                     @Value("\${org.angigaram.total-daily-badges}") val totalDailyBadges: Int ) {

    @TransactionalEventListener
    fun handleEmptyBadgesForTheDayEvent(emptyBadgesForTheDayEvent: EmptyBadgesForTheDayEvent) {
        slackClientService.postEphemeral(emptyBadgesForTheDayEvent.userGuid, emptyBadgesForTheDayEvent.sourceId,
            "Oh no :disappointed: :disappointed:" +
                    " You can only give $totalDailyBadges badges per day! Wait for tomorrow!")
    }
}