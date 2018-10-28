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
        if (event["event"]["type"].asText() == "message") {
            val message = event["event"]["text"].asText()
            val fromSlackUserId = event["event"]["user"].asText()
            val eventId = event["event_id"].asText()

            slackEventsService.process(eventId, fromSlackUserId, message)
        }


        return event["challenge"]?.asText().orEmpty()
    }
}