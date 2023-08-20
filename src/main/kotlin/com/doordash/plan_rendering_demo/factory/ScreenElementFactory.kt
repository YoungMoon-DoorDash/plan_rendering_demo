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
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object ScreenElementFactory {
    fun toScreen(jsonString: String): List<ScreenElement> =
        Json.decodeFromString<List<JsonObject>>(jsonString).map { element ->
            toScreenElement(element)
        }.toList()
    
    private fun toScreenElement(jsonObject: JsonObject): ScreenElement =
        when(val elementType = getPropertyString("type", jsonObject).toUIFlowScreenSectionType()) {
            UIFlowScreenSectionType.CENTERED_IMAGE ->
                ElementCenteredImage(content = getPropertyString("content", jsonObject))

            UIFlowScreenSectionType.LIST_ITEM_WITH_IMAGE ->
                ElementListItemWithImage(
                    content = getPropertyString("content", jsonObject),
                    action_label = getPropertyString("action_label", jsonObject),
                    action_display_type =
                        getPropertyStringWithDefault(
                            "action_display_type", jsonObject, "UNKNOWN_ACTION_DISPLAY_TYPE"
                        ).toUIFlowScreenActionDisplayType(),
                    action_parameter_type =
                        getPropertyStringWithDefault(
                            "action_parameter_type", jsonObject, "UNKNOWN_ACTION_PARAMETER_TYPE"
                        ).toUIFlowScreenActionParameterType(),
                    action_parameter_value = getPropertyStringWithDefault("action_parameter_value", jsonObject)
                )

            UIFlowScreenSectionType.RADIO_BUTTON ->
                ElementRadioButton(content = getPropertyString("content", jsonObject))

            UIFlowScreenSectionType.USER_INPUT_TEXT_BOX ->
                ElementUserInputTextBox(content = getPropertyString("content", jsonObject))

            UIFlowScreenSectionType.DIVIDER_RULER -> ElementDividerRuler()
            UIFlowScreenSectionType.DIVIDER_SPACER -> ElementDividerSpacer()
            UIFlowScreenSectionType.HEADER_IMAGE->
                ElementImage(
                    content = Json.decodeFromString<List<String>>(getPropertyString("content", jsonObject))
                )
            UIFlowScreenSectionType.BANNER->
                ElementBanner(
                    content = Json.decodeFromString<List<String>>(getPropertyString("content", jsonObject))
                )
            UIFlowScreenSectionType.TEXT_WITH_SEPARATE_LABEL_OR_ACTION ->
                ElementTextWithSeparateLabelOrAction(
                    content = Json.decodeFromString<List<String>>(getPropertyString("content", jsonObject))
                )
            UIFlowScreenSectionType.IMAGE ->
                ElementImage(
                    content = Json.decodeFromString<List<String>>(getPropertyString("content", jsonObject)),
                    alignment = getPropertyStringWithDefault("alignment", jsonObject, "DEFAULT")
                        .toUIFlowScreenTextAlignment()
                )
            UIFlowScreenSectionType.RICH_CARD_RADIO_BUTTON ->
                ElementRichCardRadioButton(
                    content = getPropertyListString("content", jsonObject),
                    alignment = getPropertyStringWithDefault("alignment", jsonObject, "DEFAULT")
                        .toUIFlowScreenTextAlignment(),
                    action = getElementAction(jsonObject),
                    rich_content = getElementRichContext(jsonObject)
                )

            else ->
                ElementText(
                    type = elementType,
                    content = getPropertyString("content", jsonObject),
                    alignment = getPropertyStringWithDefault("alignment", jsonObject, "DEFAULT")
                        .toUIFlowScreenTextAlignment()
                )
        }

    private fun getElementAction(jsonObject: JsonObject): ElementAction {
        val actionObject = jsonObject["action"]?.jsonObject ?: run {
            throw IllegalArgumentException("Can't parse 'action' object.")
        }

        return ElementAction(
            label = getPropertyString("label", actionObject),
            type = getPropertyStringWithDefault("type", actionObject, "UNKNOWN_ACTION_IDENTIFIER")
                .toUIFlowScreenActionIdentifier(),
            display_type = getPropertyStringWithDefault("display_type", actionObject, "UNKNOWN_ACTION_DISPLAY_TYPE")
                .toUIFlowScreenActionDisplayType(),
            parameters = getElementActionParameters(actionObject),
            post_action = getPropertyStringWithDefault("post_action", actionObject, "UNKNOWN_ACTION_IDENTIFIER")
                .toUIFlowScreenActionIdentifier()
        )
    }

    private fun getElementActionParameters(jsonObject: JsonObject): List<ElementActionParameter> {
        val parametersObject = jsonObject["parameters"]?.jsonArray ?: run {
            val objectString = ObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(jsonObject)
            throw IllegalArgumentException("Can't parse 'parameters' object for action: $objectString.")
        }

        return parametersObject.map {
            Json.decodeFromJsonElement(ElementActionParameter.serializer(), it)
        }.toList()
    }

    private fun getElementRichContext(parameters: JsonObject): List<ElementRichContent> {
        val richContentObject = parameters["rich_content"]?.jsonArray ?: run {
            val objectString = ObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(parameters)
            throw IllegalArgumentException("Can't parse 'rich_content' object: $objectString.")
        }
        return richContentObject.map {
            Json.decodeFromJsonElement(ElementRichContent.serializer(), it)
        }.toList()
    }

    private fun getPropertyString(key: String, jsonObject: JsonObject): String =
        jsonObject.getOrDefault(key, null)?.jsonPrimitive?.content ?: run {
            val objectString = ObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(jsonObject)
            throw IllegalArgumentException("Can't find '$key' field from the current object: $objectString")
        }

    private fun getPropertyListString(key: String, jsonObject: JsonObject): List<String> {
        val arrayObject = jsonObject[key]?.jsonArray ?: run {
            val objectString = ObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(jsonObject)
            throw IllegalArgumentException("Can't find '$key' field from the current object: $objectString")
        }

        return arrayObject.map {
            it.jsonPrimitive.content
        }.toList()
    }

    private fun getPropertyStringWithDefault(key: String, jsonObject: JsonObject, defaultValue: String = ""): String =
        jsonObject.getOrDefault(key, null)?.jsonPrimitive?.content ?: defaultValue
}