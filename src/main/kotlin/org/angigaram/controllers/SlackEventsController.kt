package org.angigaram.controllers

import com.fasterxml.jackson.databind.JsonNode
import org.angigaram.services.SlackEventsService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("slack-events")
class SlackEventsController(val slackEventsService: SlackEventsService) {

    @PostMapping
    fun postSlackEvent(@RequestBody event: JsonNode): String {
        if (event["challenge"] != null) {
            return event["challenge"].asText()
        }

        if (event["event"]["type"].asText() == "message" &&
                event["event"]["subtype"]?.asText() != "message_changed") {
            val message = event["event"]["text"]?.asText()
                    ?: event["event"]["message"]["text"].asText()
            val fromSlackUserId = event["event"]["user"]?.asText()
                    ?: event["event"]["message"]["user"].asText()
            val eventId = event["event_id"].asText()
            val channelId = event["event"]["channel"].asText()

            slackEventsService.process(eventId, channelId, fromSlackUserId, message)
        }

        return ""
    }
}