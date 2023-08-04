package com.doordash.plan_rendering_demo.view

import com.doordash.plan_rendering_demo.model.ScreenElement
import com.doordash.plan_rendering_demo.model.ScreenElementDto
import com.doordash.plan_rendering_demo.model.ScreenElementDtoText
import com.doordash.rpc.common.UIFlowScreenSectionType
import com.doordash.rpc.common.UIFlowScreenTextAlignment
import jakarta.transaction.NotSupportedException
import kotlinx.serialization.json.Json

fun String.replaceParameters(parameters: Map<String, String>): String =
    parameters.entries.fold(this) { acc, (key, value) -> acc.replace("{$key}", value) }

object ScreenElementFactory {
    fun fromDto(dto: ScreenElementDto): ScreenElement = when (dto.sectionType) {
        UIFlowScreenSectionType.TEXT -> {
            val data = Json.decodeFromString<ScreenElementDtoText>(dto.content)
            ScreenElementText(data.content, data.alignment ?: UIFlowScreenTextAlignment.DEFAULT)
        }

        UIFlowScreenSectionType.BOLDED_TITLE_TEXT -> {
            val data = Json.decodeFromString<ScreenElementDtoText>(dto.content)
            ScreenElementBoldedTitleText(data.content, data.alignment ?: UIFlowScreenTextAlignment.DEFAULT)
        }

        else -> throw NotSupportedException("not supported screen element type")
    }
}