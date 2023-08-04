package com.doordash.plan_rendering_demo.controller

import com.doordash.plan_rendering_demo.model.ScreenElementDto
import com.doordash.plan_rendering_demo.model.ScreenElementDtoText
import com.doordash.plan_rendering_demo.view.ScreenElementFactory
import com.doordash.rpc.common.UIFlowScreenSectionType
import com.doordash.rpc.common.UIFlowScreenTextAlignment
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull.content
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import kotlin.random.Random

@Controller
class HomeController {
    @GetMapping("/")
    fun hello(model: Model): String {
        val value = Random.nextInt() % 2
        val parameters = mapOf(
            "parameter1" to "$1.99",
            "parameter2" to "$2.99"
        )
        val elementDto = if (value == 0) {
            ScreenElementDto(
                id = 1,
                sectionType = UIFlowScreenSectionType.BOLDED_TITLE_TEXT,
                content = Json.encodeToString(
                    ScreenElementDtoText(
                        content="BOLDED_TITLE_TEXT {parameter1} to {parameter2}",
                        alignment = UIFlowScreenTextAlignment.CENTER
                    )
                )
            )
        } else {
            ScreenElementDto(
                id = 2,
                sectionType = UIFlowScreenSectionType.TEXT,
                content = Json.encodeToString(
                    ScreenElementDtoText(
                        content = "TEXT {parameter1} to {parameter2}",
                        alignment = UIFlowScreenTextAlignment.DEFAULT
                    )
                )
            )
        }

        model["element"] = ScreenElementFactory.fromDto(elementDto).render(parameters)
        model["title"] = "Plan Renderer"
        return "home"
    }
}