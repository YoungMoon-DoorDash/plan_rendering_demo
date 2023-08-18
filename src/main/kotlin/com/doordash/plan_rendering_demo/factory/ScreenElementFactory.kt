package com.doordash.plan_rendering_demo.factory

import com.doordash.plan_rendering_demo.model.elements.ElementAction
import com.doordash.plan_rendering_demo.model.elements.ElementActionParameter
import com.doordash.plan_rendering_demo.model.elements.ElementBanner
import com.doordash.plan_rendering_demo.model.elements.ElementCenteredImage
import com.doordash.plan_rendering_demo.model.elements.ElementDividerRuler
import com.doordash.plan_rendering_demo.model.elements.ElementDividerSpacer
import com.doordash.plan_rendering_demo.model.elements.ElementImage
import com.doordash.plan_rendering_demo.model.elements.ElementListItemWithImage
import com.doordash.plan_rendering_demo.model.elements.ElementRadioButton
import com.doordash.plan_rendering_demo.model.elements.ElementRichCardRadioButton
import com.doordash.plan_rendering_demo.model.elements.ElementRichContent
import com.doordash.plan_rendering_demo.model.elements.ElementText
import com.doordash.plan_rendering_demo.model.elements.ElementTextWithSeparateLabelOrAction
import com.doordash.plan_rendering_demo.model.elements.ElementUserInputTextBox
import com.doordash.plan_rendering_demo.model.elements.ScreenElement
import com.doordash.plan_rendering_demo.utils.toUIFlowScreenActionDisplayType
import com.doordash.plan_rendering_demo.utils.toUIFlowScreenActionIdentifier
import com.doordash.plan_rendering_demo.utils.toUIFlowScreenActionParameterType
import com.doordash.plan_rendering_demo.utils.toUIFlowScreenSectionType
import com.doordash.plan_rendering_demo.utils.toUIFlowScreenTextAlignment
import com.doordash.rpc.common.UIFlowScreenSectionType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

object ScreenElementFactory {
    fun toScreen(jsonString: String): List<ScreenElement> =
        Json.decodeFromString<List<JsonObject>>(jsonString).map { element ->
            toScreenElement(element)
        }.toList()
    
    private fun toScreenElement(jsonObject: JsonObject): ScreenElement =
        when(val elementType = getProperty("type", jsonObject).toUIFlowScreenSectionType()) {
            UIFlowScreenSectionType.CENTERED_IMAGE ->
                ElementCenteredImage(content = getProperty("content", jsonObject))

            UIFlowScreenSectionType.LIST_ITEM_WITH_IMAGE ->
                ElementListItemWithImage(
                    content = getProperty("content", jsonObject),
                    action_label = getProperty("action_label", jsonObject),
                    action_display_type =
                        getPropertyWithDefaultValue(
                            "action_display_type", jsonObject, "UNKNOWN_ACTION_DISPLAY_TYPE"
                        ).toUIFlowScreenActionDisplayType(),
                    action_parameter_type =
                        getPropertyWithDefaultValue(
                            "action_parameter_type", jsonObject, "UNKNOWN_ACTION_PARAMETER_TYPE"
                        ).toUIFlowScreenActionParameterType(),
                    action_parameter_value = getPropertyWithDefaultValue("action_parameter_value", jsonObject)
                )

            UIFlowScreenSectionType.RADIO_BUTTON ->
                ElementRadioButton(content = getProperty("content", jsonObject))

            UIFlowScreenSectionType.USER_INPUT_TEXT_BOX ->
                ElementUserInputTextBox(content = getProperty("content", jsonObject))

            UIFlowScreenSectionType.DIVIDER_RULER -> ElementDividerRuler()
            UIFlowScreenSectionType.DIVIDER_SPACER -> ElementDividerSpacer()
            UIFlowScreenSectionType.HEADER_IMAGE->
                ElementImage(
                    content = Json.decodeFromString<List<String>>(getProperty("content", jsonObject))
                )
            UIFlowScreenSectionType.BANNER->
                ElementBanner(
                    content = Json.decodeFromString<List<String>>(getProperty("content", jsonObject))
                )
            UIFlowScreenSectionType.TEXT_WITH_SEPARATE_LABEL_OR_ACTION ->
                ElementTextWithSeparateLabelOrAction(
                    content = Json.decodeFromString<List<String>>(getProperty("content", jsonObject))
                )
            UIFlowScreenSectionType.IMAGE ->
                ElementImage(
                    content = Json.decodeFromString<List<String>>(getProperty("content", jsonObject)),
                    alignment = getPropertyWithDefaultValue("alignment", jsonObject, "DEFAULT")
                        .toUIFlowScreenTextAlignment()
                )
            UIFlowScreenSectionType.RICH_CARD_RADIO_BUTTON ->
                ElementRichCardRadioButton(
                    content = Json.decodeFromString<List<String>>(getProperty("content", jsonObject)),
                    alignment = getPropertyWithDefaultValue("alignment", jsonObject, "DEFAULT")
                        .toUIFlowScreenTextAlignment(),
                    action = getElementAction(jsonObject),
                    rich_content = getElementRichContext(jsonObject)
                )

            else ->
                ElementText(
                    type = elementType,
                    content = getProperty("content", jsonObject),
                    alignment = getPropertyWithDefaultValue("alignment", jsonObject, "DEFAULT")
                        .toUIFlowScreenTextAlignment()
                )
        }

    private fun getElementAction(jsonObject: JsonObject): ElementAction {
        val actionObject = jsonObject["action"]?.jsonObject ?: run {
            throw IllegalArgumentException("Can't parse 'action' object.")
        }

        return ElementAction(
            label = getProperty("label", actionObject),
            type = getPropertyWithDefaultValue("type", actionObject, "UNKNOWN_ACTION_IDENTIFIER")
                .toUIFlowScreenActionIdentifier(),
            display_type = getPropertyWithDefaultValue("display_type", actionObject, "UNKNOWN_ACTION_DISPLAY_TYPE")
                .toUIFlowScreenActionDisplayType(),
            parameters = getElementActionParameters(actionObject["parameters"]),
            post_action = getPropertyWithDefaultValue("post_action", actionObject, "UNKNOWN_ACTION_IDENTIFIER")
                .toUIFlowScreenActionIdentifier()
        )
    }

    private fun getElementActionParameters(parameters: JsonElement?): List<ElementActionParameter> {
        requireNotNull(parameters) { "parameters field is not valid" }
        return parameters.jsonArray.map {
            Json.decodeFromJsonElement(ElementActionParameter.serializer(), it)
        }.toList()
    }

    private fun getElementRichContext(parameters: JsonObject): List<ElementRichContent> {
        val richContentObject = parameters["rich_content"]?.jsonArray ?: run {
            throw IllegalArgumentException("Can't parse 'rich_content' object.")
        }
        return richContentObject.map {
            Json.decodeFromJsonElement(ElementRichContent.serializer(), it)
        }.toList()
    }

    private fun getProperty(key: String, jsonObject: JsonObject): String =
        jsonObject.getOrDefault(key, null)?.toString()?.let {
            if (it.startsWith('"')) {
                it.substring(1, it.length - 1)
            } else it
        } ?: run {
            throw IllegalArgumentException("Can't find $key from the current object.")
        }

    private fun getPropertyWithDefaultValue(key: String, jsonObject: JsonObject, defaultValue: String = ""): String =
        jsonObject.getOrDefault(key, null)?.toString()?.let {
            if (it.startsWith('"')) {
                it.substring(1, it.length - 1)
            } else it
        } ?: defaultValue
}