package com.doordash.plan_rendering_demo

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.web.servlet.function.RouterFunctions.resources

@Configuration
class WebConfiguration {
    @Bean
    fun resRouter() = resources("/**", ClassPathResource("static/"))
}