package org.angigaram

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AngigaramApplication

fun main(args: Array<String>) {
    runApplication<AngigaramApplication>(*args) {
        setBannerMode(Banner.Mode.OFF)
    }
}
