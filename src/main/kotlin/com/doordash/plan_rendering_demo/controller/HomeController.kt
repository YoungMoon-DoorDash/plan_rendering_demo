package com.doordash.plan_rendering_demo.controller

import com.doordash.plan_rendering_demo.factory.HtmlFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HomeController {
    @GetMapping("/")
    fun hello(model: Model): String {
        model["banner"] = HtmlFactory.getHomeBanner("home")
        model["title"] = "Plan Renderer"
        return "home"
    }
}