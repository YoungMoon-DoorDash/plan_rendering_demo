package com.doordash.plan_rendering_demo.controller

import com.doordash.plan_rendering_demo.factory.HtmlFactory
import com.doordash.plan_rendering_demo.factory.ScreenElementFactory
import com.doordash.plan_rendering_demo.model.ScreenElement
import com.doordash.plan_rendering_demo.model.ScreenElementText
import com.doordash.plan_rendering_demo.repository.ScreenElementRepository
import com.doordash.rpc.common.UIFlowScreenSectionType
import com.doordash.rpc.common.UIFlowScreenTextAlignment
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class ScreenElementController(
    private val screenElementRepository: ScreenElementRepository,
) {
    @GetMapping("/elements")
    fun elementsHome(model: Model): String {
        addElementInfo(model, "elements", "Elements")

        model["elements"] = screenElementRepository.findAll()
        return "elements"
    }

    private fun addElementInfo(model: Model, banner: String, title: String) {
        model["banner"] = HtmlFactory.getHomeBanner(banner)
        model["add-screen-elements"] = HtmlFactory.getElementsDropDown()
        model["title"] = title
    }

    @GetMapping("/elements/add")
    fun elementsAdd(@RequestParam("section") sectionType:Int, model: Model): String {
        runCatching {
            UIFlowScreenSectionType.forNumber(sectionType)
        }.getOrNull()?.let {
            addElementInfo(model, "elements-add", "Elements > ${it.name}")

            model["form-elements"] = ScreenElementFactory.getFormText(it)
            model["form-section-type"] = it.ordinal
            model["form-section-id"] = 0
            model["form-action"] = ScreenElementFactory.getFormAction(it)
            model["form-data"] = ""
            return "elements-add"
        }

        return elementsHome(model)
    }

    @PostMapping("/elements/add/text")
    fun elementsAddText(
        @RequestParam("form-section-type") sectionType:Int,
        @RequestParam("form-section-id") sectionId:Int,
        @RequestParam("form-section-alignment") alignmentType:Int,
        @RequestParam("form-section-content") sectionContent: String,
        model: Model
    ): String {
        runCatching {
            UIFlowScreenSectionType.forNumber(sectionType)
        }.getOrNull()?.let {
            val contentJson = Json.encodeToString<ScreenElementText>(
                ScreenElementText(
                    alignment = UIFlowScreenTextAlignment.forNumber(alignmentType),
                    content = sectionContent
                )
            )
            model["element"] = screenElementRepository.save(
                ScreenElement(
                    sectionType = it.ordinal,
                    content = contentJson
                )
            )

            addElementInfo(model, "elements-info", "Elements > ${it.name}")
            return "elements-info"
        }

        return elementsHome(model)
    }
}