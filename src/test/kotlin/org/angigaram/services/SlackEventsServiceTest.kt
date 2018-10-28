package org.angigaram.services

import com.fasterxml.jackson.databind.ObjectMapper
import org.angigaram.repositories.BadgeTransactionRepository
import org.angigaram.repositories.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.ZoneId
import java.time.ZonedDateTime

@ExtendWith(SpringExtension::class)
@ActiveProfiles("test")
@SpringBootTest
class SlackEventsServiceTest(
    @Autowired val slackEventsService: SlackEventsService,
    @Autowired val badgeTransactionRepository: BadgeTransactionRepository,
    @Autowired val userService: UserService,
    @Autowired val userRepository: UserRepository,
    @Autowired val objectMapper: ObjectMapper
) {

    @MockBean
    private lateinit var slackClientService: SlackClientService

    @BeforeEach
    fun setUp() {
        Mockito.`when`(slackClientService.getUsersInfo("krangaraju"))
            .thenReturn(
                objectMapper.readTree(
                    "{\n" +
                            "        \"id\": \"krangaraju\",\n" +
                            "        \"name\": \"krangaraju\",\n" +
                            "        \"tz\": \"America/Los_Angeles\",\n" +
                            "        \"profile\": {\n" +
                            "            \"real_name\": \"Vengada Karthik Rangaraju\",\n" +
                            "            \"display_name\": \"krangaraju\",\n" +
                            "            \"image_24\": \"dummyImageURL1\"\n" +
                            "        }\n" +
                            "}"
                )
            )

        Mockito.`when`(slackClientService.getUsersInfo("achernykh"))
            .thenReturn(
                objectMapper.readTree(
                    "{\n" +
                            "        \"id\": \"achernykh\",\n" +
                            "        \"name\": \"achernykh\",\n" +
                            "        \"tz\": \"America/Los_Angeles\",\n" +
                            "        \"profile\": {\n" +
                            "            \"real_name\": \"Andrey Chernykh\",\n" +
                            "            \"display_name\": \"achernykh\",\n" +
                            "            \"image_24\": \"dummyImageURL2\"\n" +
                            "        }\n" +
                            "}"
                )
            )

        Mockito.`when`(slackClientService.getUsersInfo("fhadfa"))
            .thenReturn(
                objectMapper.readTree(
                    "{\n" +
                            "        \"id\": \"fhadfa\",\n" +
                            "        \"name\": \"fhadfa\",\n" +
                            "        \"tz\": \"America/Los_Angeles\",\n" +
                            "        \"profile\": {\n" +
                            "            \"real_name\": \"Faizan Hadfa\",\n" +
                            "            \"display_name\": \"fhadfa\",\n" +
                            "            \"image_24\": \"dummyImageURL3\"\n" +
                            "        }\n" +
                            "}"
                )
            )
    }

    @Test
    fun `Karthik gives more badges than daily allowed badges to Andrey`() {
        slackEventsService.process(
            "eventA", "channelA", "krangaraju",
            "Hey <@achernykh> thank you for making things happen :badge1: :badge1:"
        )
        slackEventsService.process(
            "eventB", "channelA ",  "krangaraju",
            "Hey <@achernykh> thank you for making things happen :badge2: :badge2:"
        )
        slackEventsService.process(
            "eventC", "channelA",  "krangaraju",
            "Hey <@achernykh> thank you for making things happen :badge2: :badge2: :badge2:"
        )
        slackEventsService.process(
            "eventD", "channelA", "krangaraju",
            "Hey <@achernykh> thank you for making things happen :badge2:"
        )

        val zonedDateTime = ZonedDateTime.now(ZoneId.of("America/Los_Angeles"))
        val badgesGivenForTheDayByKarthik = badgeTransactionRepository
            .getBadgesAlreadyGivenForTheDay("krangaraju", zonedDateTime.offset.toString(), zonedDateTime.toLocalDate())
        val badgesReceivedForTheDayForAndrey = badgeTransactionRepository
            .getBadgesReceivedForTheDay("achernykh", zonedDateTime.offset.toString(), zonedDateTime.toLocalDate())

        // Daily allowed badges is 5. Therefore the transaction should prevent from giving badges beyond the limit.
        assertThat(badgesGivenForTheDayByKarthik).isEqualTo(5)
        assertThat(badgesReceivedForTheDayForAndrey).isEqualTo(5)

        assertThat(userService.findByGuid("krangaraju")).isNotNull
        assertThat(userService.findByGuid("achernykh")).isNotNull
    }

    @Test
    fun `Faizan and Karthik give some of their badges to Andrey`() {
        slackEventsService.process(
            "eventA", "channelA",  "fhadfa",
            "Hey <@achernykh> thank you for making things happen :badge1: :badge1:"
        )
        slackEventsService.process(
            "eventB", "channelA ",  "krangaraju",
            "Hey <@achernykh> thank you for making things happen :badge2: :badge2:"
        )
        slackEventsService.process(
            "eventC", "channelA",  "krangaraju",
            "Hey <@achernykh> thank you for making things happen :badge2: :badge2: :badge2:"
        )

        val localDate = ZonedDateTime.now(ZoneId.of("America/Los_Angeles")).toLocalDate()
        val badgesGivenForTheDayByKarthik = badgeTransactionRepository
            .getBadgesAlreadyGivenForTheDay("krangaraju", "-07:00", localDate)
        val badgesGivenForTheDayByFaizan = badgeTransactionRepository
            .getBadgesAlreadyGivenForTheDay("fhadfa", "-07:00", localDate)
        val badgesReceivedForTheDayForAndrey = badgeTransactionRepository
            .getBadgesReceivedForTheDay("achernykh", "-07:00", localDate)

        assertThat(badgesGivenForTheDayByFaizan).isEqualTo(2)
        assertThat(badgesGivenForTheDayByKarthik).isEqualTo(5)
        assertThat(badgesReceivedForTheDayForAndrey).isEqualTo(7)

        assertThat(userService.findByGuid("krangaraju")).isNotNull
        assertThat(userService.findByGuid("achernykh")).isNotNull
        assertThat(userService.findByGuid("fhadfa")).isNotNull
    }

    @Test
    fun `When Karthik tags multiple people while giving a badge, only the first tag will get the badges`() {
        slackEventsService.process(
            "eventA", "channelA",  "krangaraju",
            "Hey <@achernykh> <@fhadfa> thank you for making things happen :badge2: :badge2:"
        )

        val localDate = ZonedDateTime.now(ZoneId.of("America/Los_Angeles")).toLocalDate()
        val badgesGivenForTheDayByKarthik = badgeTransactionRepository
            .getBadgesAlreadyGivenForTheDay("krangaraju", "-07:00", localDate)
        val badgesReceivedForTheDayForAndrey = badgeTransactionRepository
            .getBadgesReceivedForTheDay("achernykh", "-07:00", localDate)
        val badgesReceivedForTheDayForFaizan = badgeTransactionRepository
            .getBadgesReceivedForTheDay("fhadfa", "-07:00", localDate)

        assertThat(badgesGivenForTheDayByKarthik).isEqualTo(2)
        assertThat(badgesReceivedForTheDayForAndrey).isEqualTo(2)
        assertThat(badgesReceivedForTheDayForFaizan).isEqualTo(0)

        assertThat(userService.findByGuid("krangaraju")).isNotNull
        assertThat(userService.findByGuid("achernykh")).isNotNull
        assertThat(userService.findByGuid("fhadfa")).isNull()
    }

    @Test
    fun `Karthik cannot give badges to himself`() {
        slackEventsService.process(
            "eventA", "channelA",  "krangaraju",
            "Hey <@krangaraju> thank you for making things happen :badge1: :badge1:"
        )

        val zonedDateTime = ZonedDateTime.now(ZoneId.of("America/Los_Angeles"))
        val badgesGivenForTheDayByKarthik = badgeTransactionRepository
            .getBadgesAlreadyGivenForTheDay("krangaraju", zonedDateTime.offset.toString(), zonedDateTime.toLocalDate())
        val badgesReceivedForTheDayForKarthik = badgeTransactionRepository
            .getBadgesReceivedForTheDay("krangaraju", zonedDateTime.offset.toString(), zonedDateTime.toLocalDate())

        assertThat(badgesGivenForTheDayByKarthik).isEqualTo(0)
        assertThat(badgesReceivedForTheDayForKarthik).isEqualTo(0)

        assertThat(userService.findByGuid("krangaraju")).isNotNull
    }

    @Test
    fun `When the same event is processed more than once, the processing is idempotent`() {
        slackEventsService.process(
            "eventA", "channelA",  "krangaraju",
            "Hey <@achernykh> thank you for making things happen :badge1: :badge1:"
        )
        slackEventsService.process(
            "eventA", "channelA",  "krangaraju",
            "Hey <@achernykh> thank you for making things happen :badge1: :badge1:"
        )

        val zonedDateTime = ZonedDateTime.now(ZoneId.of("America/Los_Angeles"))
        val badgesGivenForTheDayByKarthik = badgeTransactionRepository
            .getBadgesAlreadyGivenForTheDay("krangaraju", zonedDateTime.offset.toString(), zonedDateTime.toLocalDate())
        val badgesReceivedForTheDayForKarthik = badgeTransactionRepository
            .getBadgesReceivedForTheDay("achernykh", zonedDateTime.offset.toString(), zonedDateTime.toLocalDate())

        assertThat(badgesGivenForTheDayByKarthik).isEqualTo(2)
        assertThat(badgesReceivedForTheDayForKarthik).isEqualTo(2)

        assertThat(userService.findByGuid("krangaraju")).isNotNull
        assertThat(userService.findByGuid("achernykh")).isNotNull
    }

    @AfterEach
    fun afterEach() {
        userRepository.deleteAll()
        badgeTransactionRepository.deleteAll()
    }
}