package com.doordash.plan_rendering_demo.controller

import com.doordash.plan_rendering_demo.model.ScreenElementDto
import com.doordash.plan_rendering_demo.model.ScreenElementDtoText
import com.doordash.rpc.common.UIFlowScreenSectionType
import com.doordash.rpc.common.UIFlowScreenTextAlignment
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull.content
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class ScreenElementController {
    @GetMapping("/elements")
    fun elementsHome(model: Model): String {
        model["banner"] = HtmlFactory.getHomeBanner("elements")
        model["title"] = "Elements"
        model["add-screen-elements"] = HtmlFactory.getElementsDropDown()
        return "elements"
    }

    @GetMapping("/elements/add")
    fun elementsAdd(@RequestParam("section") sectionType:Int, model: Model): String {
        runCatching {
            UIFlowScreenSectionType.forNumber(sectionType)
        }.getOrNull()?.let {
            model["add-screen-elements"] = HtmlFactory.getElementsDropDown()
            model["banner"] = HtmlFactory.getHomeBanner("elements-add")
            model["title"] = "Elements > ${it.name}"

            setFormParameters(it, model)
            model["form-data"] = ""
            return "elements-add"
        }

        return elementsHome(model)
    }

    private fun setFormParameters(uiFlowScreenSectionType: UIFlowScreenSectionType, model: Model) {
        model["form-elements"] = ScreenElementFactory.getFormText(uiFlowScreenSectionType)
        model["form-section-type"] = uiFlowScreenSectionType.ordinal
        model["form-section-id"] = 0
        model["form-action"] = ScreenElementFactory.getFormAction(uiFlowScreenSectionType)
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
            model["add-screen-elements"] = HtmlFactory.getElementsDropDown()
            model["banner"] = HtmlFactory.getHomeBanner("elements-add")
            model["title"] = "Elements > ${it.name} > Post"
            model["form-data"] = Json.encodeToString<ScreenElementDto>(
                ScreenElementDto(
                    id = sectionId,
                    sectionType = it,
                    content = Json.encodeToString<ScreenElementDtoText>(
                        ScreenElementDtoText(
                            alignment = UIFlowScreenTextAlignment.forNumber(alignmentType),
                            content = sectionContent
                        )
                    )
                )
            )

            setFormParameters(it, model)
            return "elements-add"
        }

        return elementsHome(model)
    }
}