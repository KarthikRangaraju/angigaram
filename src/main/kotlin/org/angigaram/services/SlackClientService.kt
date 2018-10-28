package org.angigaram.services

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class SlackClientService(@Value("\${org.angigaram.slack-bot-oauth-token}") val slackBotOAuthToken: String,
                         val objectMapper: ObjectMapper) {

    fun getUsersInfo(slackUserId: String): JsonNode? {
        val restTemplate = RestTemplate()
        val getUserInfoURL = "https://slack.com/api/users.info?token=$slackBotOAuthToken&user=$slackUserId"

        val response = objectMapper.readTree(restTemplate.getForObject(getUserInfoURL, String::class.java))

        return when {
            response["ok"].asBoolean().not() -> null
            else -> response["user"]
        }
    }
}