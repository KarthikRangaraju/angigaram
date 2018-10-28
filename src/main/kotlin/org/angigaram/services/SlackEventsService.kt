package org.angigaram.services

import org.angigaram.models.User
import org.springframework.stereotype.Service
import java.util.regex.Pattern
import javax.transaction.Transactional

@Service
class SlackEventsService(
    val badgeTransactionsService: BadgeTransactionsService,
    val slackClientService: SlackClientService,
    val userService: UserService
) {

    val USER_MENTION_REGEX = Pattern.compile("[<][@]([A-za-z0-9]*)[>]")
    val EMOJI_REGEX = Pattern.compile("[:]([A-za-z0-9]*)[:]")

    @Transactional
    fun process(eventId: String, channelId: String, fromSlackUserId: String, slackMessage: String) {
        val userMentionMatcher = USER_MENTION_REGEX.matcher(slackMessage)

        // We need to process only if a user was mentioned in a slack message. For example: Hey <@achernykh>
        // Kudos, :badge1: :badge2: !!!
        if (userMentionMatcher.find()) {
            val toSlackUserId = userMentionMatcher.group(1)

            val fromUser = findAndUpdate(fromSlackUserId) ?: return
            val toUser = findAndUpdate(toSlackUserId) ?: return
            val emojis = extractEmojis(slackMessage)
            badgeTransactionsService.process(eventId, channelId, fromUser, toUser, emojis)
        }
    }

    private fun extractEmojis(slackMessage: String): List<String> {
        val emojiMatcher = EMOJI_REGEX.matcher(slackMessage)

        val emojis = mutableListOf<String>()
        while (emojiMatcher.find()) {
            emojis.add(emojiMatcher.group(1))
        }

        return emojis
    }

    private fun findAndUpdate(slackUserId: String): User? {
        val slackUserJsonNode = slackClientService.getUsersInfo(slackUserId) ?: return null

        val user = User(
            slackUserJsonNode["id"].asText(),
            slackUserJsonNode["profile"]["real_name"].asText(),
            slackUserJsonNode["tz"].asText(),
            slackUserJsonNode["profile"]["image_24"].asText()
        )

        val userInDB = userService.findByGuid(user.guid)

        with(user) {
            when {
                userInDB == null || userInDB.name != name || userInDB.photoUrl != photoUrl
                        || userInDB.timeZone != timeZone -> return userService.save(user)
                else -> return userInDB
            }
        }
    }
}