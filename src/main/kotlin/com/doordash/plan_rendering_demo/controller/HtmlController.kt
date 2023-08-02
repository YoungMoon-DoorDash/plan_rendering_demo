package com.doordash.plan_rendering_demo.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HtmlController {
    @GetMapping("/")
    fun hello(model: Model): String {
        model["title"] = "Plan Renderer"
        return "blog"
    }
}