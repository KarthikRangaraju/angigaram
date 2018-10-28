package org.angigaram.configurations

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.web.client.RestTemplate


@Configuration
class ApplicationConfiguration {

    @Bean
    fun objectMapper(): ObjectMapper {
        val jackson2ObjectMapperBuilder = Jackson2ObjectMapperBuilder()
        jackson2ObjectMapperBuilder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        jackson2ObjectMapperBuilder
                .featuresToDisable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        // ISO 8601 format. Example: 2001-07-04T12:08:56.235-07:00
        // Reference: https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
        jackson2ObjectMapperBuilder.simpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        jackson2ObjectMapperBuilder.modules(JavaTimeModule())
        jackson2ObjectMapperBuilder.modules(KotlinModule())

        return jackson2ObjectMapperBuilder.build()
    }

    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }
}