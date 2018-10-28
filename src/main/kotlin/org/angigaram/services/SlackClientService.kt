package org.angigaram.services

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.lang.RuntimeException

@Service
class SlackClientService(
    @Value("\${org.angigaram.slack-bot-oauth-token}") val slackBotOAuthToken: String,
    val objectMapper: ObjectMapper,
    val restTemplate: RestTemplate
) {

    fun getUsersInfo(slackUserId: String): JsonNode? {
        val getUserInfoURL = "https://slack.com/api/users.info?token=$slackBotOAuthToken&user=$slackUserId"

        val response = objectMapper.readTree(restTemplate.getForObject(getUserInfoURL, String::class.java))

        return when {
            response["ok"].asBoolean().not() -> null
            else -> response["user"]
        }
    }

    fun postEphemeral(slackUserId: String, channelId: String, message: String) {
        val postMessageURL = "https://slack.com/api/chat.postEphemeral?" +
                "token=$slackBotOAuthToken&user=$slackUserId&text=$message&as_user=true&channel=$channelId"

        val response = objectMapper.readTree(restTemplate.postForObject(postMessageURL, null, String::class.java))

        return when {
            response["ok"].asBoolean().not() -> throw RuntimeException("Unable to post ephemeral message to slack user" +
                    " with id $slackUserId")
            else -> {
                return
            }
        }
    }
}