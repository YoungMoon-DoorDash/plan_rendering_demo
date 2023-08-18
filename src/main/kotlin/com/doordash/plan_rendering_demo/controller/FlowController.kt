package com.doordash.plan_rendering_demo.controller

import com.doordash.plan_rendering_demo.factory.HtmlFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping

@Controller
class FlowController {
    @GetMapping("/flow")
    fun flowHome(model: Model): String {
        setFlowParams(model, "Registered Flows", true)
        return "flow"
    }

    private fun setFlowParams(model: Model, title: String, isHome: Boolean = false) {
        model["banner"] = HtmlFactory.getHomeBanner(
            if (isHome) "flow" else ""
        )
        model["title"] = title
    }
}