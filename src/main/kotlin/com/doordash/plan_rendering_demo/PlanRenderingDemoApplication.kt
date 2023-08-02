package com.doordash.plan_rendering_demo

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PlanRenderingDemoApplication

fun main(args: Array<String>) {
    runApplication<PlanRenderingDemoApplication>(*args) {
        setBannerMode(Banner.Mode.OFF)
    }
}
