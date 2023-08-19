package com.doordash.plan_rendering_demo.model.elements

import com.doordash.plan_rendering_demo.factory.HtmlFactory
import com.doordash.plan_rendering_demo.factory.RuleEngine
import com.doordash.rpc.common.FormatColor
import com.doordash.rpc.common.FormatType
import com.doordash.rpc.common.UIFlowScreenActionDisplayType
import com.doordash.rpc.common.UIFlowScreenActionIdentifier
import com.doordash.rpc.common.UIFlowScreenActionParameterType
import com.doordash.rpc.common.UIFlowScreenSection
import com.doordash.rpc.common.UIFlowScreenSectionType
import com.doordash.rpc.common.UIFlowScreenTextAlignment
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNull.content
import java.awt.SystemColor.text
import javax.script.ScriptEngine

interface ScreenElement {
    fun render(): String
}

@Serializable
data class ElementText(
    val type: UIFlowScreenSectionType,
    val content: String,
    val alignment: UIFlowScreenTextAlignment = UIFlowScreenTextAlignment.DEFAULT
): ScreenElement {
    override fun render(): String = when(type) {
        UIFlowScreenSectionType.BOLDED_TITLE_TEXT ->
            HtmlFactory.getAlignment(alignment) + "<span style='font-size:14pt;font-weight:bold;'>" + RuleEngine.getText(content) + "</span></div>"
        else ->
            HtmlFactory.getAlignment(alignment) + RuleEngine.getText(content) + "</div>"
    }
}

@Serializable
data class ElementCenteredImage(
    val type: UIFlowScreenSectionType = UIFlowScreenSectionType.CENTERED_IMAGE,
    val content: String
): ScreenElement {
    override fun render(): String = "<div style='width:100%;margin:2pt;text-align:center;border:1pt solid lightgray;'><img src='" +
        RuleEngine.getText(content) + "'/></div>"
}

@Serializable
data class ElementListItemWithImage(
    val type: UIFlowScreenSectionType = UIFlowScreenSectionType.LIST_ITEM_WITH_IMAGE,
    val content: String,
    val action_label: String,
    val action_display_type: UIFlowScreenActionDisplayType,
    val action_parameter_type: UIFlowScreenActionParameterType,
    val action_parameter_value: String
): ScreenElement {
    override fun render(): String {
        val objString = ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this)
        return "<div style='width:100%;margin:2pt;text-align:center;'><img src='" + RuleEngine.getText(content) +
            "'/><pre>$objString</pre></div>border:1pt solid lightgray;"
    }
}

@Serializable
data class ElementRadioButton(
    val type: UIFlowScreenSectionType = UIFlowScreenSectionType.RADIO_BUTTON,
    val content: String
): ScreenElement {
    override fun render(): String = "<div style='width:100%;margin:2pt;text-align:center;border:1pt solid lightgray;'><input type='radio' value='" +
        RuleEngine.getText(content) + "' /></div>"
}

@Serializable
data class ElementUserInputTextBox(
    val type: UIFlowScreenSectionType = UIFlowScreenSectionType.USER_INPUT_TEXT_BOX,
    val content: String
): ScreenElement {
    override fun render(): String = "<div style='width:100%;margin:2pt;text-align:center;border:1pt solid lightgray;'><input type='input' value='" +
        RuleEngine.getText(content) + "' readonly/></div>"
}

@Serializable
data class ElementDividerRuler(
    val type: UIFlowScreenSectionType = UIFlowScreenSectionType.DIVIDER_RULER
): ScreenElement {
    override fun render(): String = "<div style='width:100%;margin:2pt;text-align:center;border:1pt solid lightgray;padding-right:6pt;'>$type</div>"
}

@Serializable
data class ElementDividerSpacer(
    val type: UIFlowScreenSectionType = UIFlowScreenSectionType.DIVIDER_SPACER
): ScreenElement {
    override fun render(): String = "<div style='width:100%;margin:2pt;text-align:center;border:1pt solid lightgray;padding-right:6pt;'>$type</div>"
}

@Serializable
data class ElementHeaderImage(
    val type: UIFlowScreenSectionType = UIFlowScreenSectionType.HEADER_IMAGE,
    val content: List<String>
): ScreenElement {
    override fun render(): String {
        val sb = StringBuilder("<div style='width:100%;margin:2pt;border:1pt solid lightgray;'>")
        content.forEach{
            sb.append("<img src='" + RuleEngine.getText(it) + "'>")
        }
        return sb.append("</div>").toString()
    }
}

@Serializable
data class ElementBanner(
    val type: UIFlowScreenSectionType = UIFlowScreenSectionType.BANNER,
    val content: List<String>
): ScreenElement {
    override fun render(): String {
        val sb = StringBuilder("<div style='width:100%;margin:2pt;border:1pt solid lightgray;padding-right:6pt;'>$type:<br/>")
        content.forEach{
            sb.append(RuleEngine.getText(it) + "<br/>")
        }
        return sb.append("</div>").toString()
    }
}

@Serializable
data class ElementTextWithSeparateLabelOrAction(
    val type: UIFlowScreenSectionType = UIFlowScreenSectionType.TEXT_WITH_SEPARATE_LABEL_OR_ACTION,
    val content: List<String>
): ScreenElement {
    override fun render(): String {
        val sb = StringBuilder("<div style='width:100%;margin:2pt;border:1pt solid lightgray;padding-right:6pt;'>$type:<br/>")
        content.forEach{
            sb.append(RuleEngine.getText(it) + "<br/>")
        }
        return sb.append("</div>").toString()
    }
}

@Serializable
data class ElementImage(
    val type: UIFlowScreenSectionType = UIFlowScreenSectionType.IMAGE,
    val content: List<String>,
    val alignment: UIFlowScreenTextAlignment = UIFlowScreenTextAlignment.DEFAULT
): ScreenElement {
    override fun render(): String {
        val sb = StringBuilder(HtmlFactory.getAlignment(alignment))
        content.forEach{
            sb.append("<img src='" + RuleEngine.getText(it) + "'>")
        }
        return sb.append("</div>").toString()
    }
}

@Serializable
data class ElementActionParameter(
    val key: UIFlowScreenActionParameterType,
    val value: String
): ScreenElement {
    override fun render(): String =
        "$key : " + RuleEngine.getText(value) + "<br/>"
}

@Serializable
data class ElementAction(
    val label: String,
    val type: UIFlowScreenActionIdentifier,
    val display_type: UIFlowScreenActionDisplayType,
    val parameters: List<ElementActionParameter>,
    val post_action: UIFlowScreenActionIdentifier
): ScreenElement {
    override fun render(): String {
        val sb = StringBuilder("<div style='width:100%;margin:2pt;border:1pt solid lightblue;padding-right:6pt;'>")
        val btnType = when(display_type) {
            UIFlowScreenActionDisplayType.FLAT_SECONDARY -> "btn-outline-secondary"
            UIFlowScreenActionDisplayType.TERTIARY -> "btn-outline-success"
            else -> "btn-outline-primary"
        }
        sb.append("<button type='button' class='btn $btnType btn-sm'>" + RuleEngine.getText(label) + "</button><br/>")
        parameters.forEach {
            sb.append(it.render())
        }
        sb.append("post_action : $post_action<br/>")
        return sb.append("</div>").toString()
    }
}

@Serializable
data class ElementRichContent(
    val content: String,
    val format_type: FormatType = FormatType.TYPE_UNKNOWN,
    val format_color: FormatColor = FormatColor.COLOR_UNKNOWN,
    val alignment: UIFlowScreenTextAlignment = UIFlowScreenTextAlignment.DEFAULT
): ScreenElement {
    override fun render(): String =
        if (content.isBlank()) {
            ""
        } else {
            val sb = StringBuilder(HtmlFactory.getAlignment(alignment, "lightgreen"))
            sb.append("<font color='" + getColor() + "'>" + RuleEngine.getText(content) + "</font>")
            sb.append("</div>").toString()
        }

    private fun getColor(): String = when(format_color) {
        FormatColor.COLOR_PRIMARY -> "blue"
        FormatColor.COLOR_SECONDARY -> "lightblue"
        FormatColor.COLOR_HIGHLIGHT -> "magenta"
        FormatColor.COLOR_TERTIARY -> "green"
        FormatColor.COLOR_DISABLED -> "gray"
        else -> "black"
    }
}

@Serializable
data class ElementRichCardRadioButton(
    val type: UIFlowScreenSectionType = UIFlowScreenSectionType.RICH_CARD_RADIO_BUTTON,
    val content: List<String>,
    val alignment: UIFlowScreenTextAlignment = UIFlowScreenTextAlignment.DEFAULT,
    val action: ElementAction,
    val rich_content: List<ElementRichContent>
): ScreenElement {
    override fun render(): String {
        val sb = StringBuilder(HtmlFactory.getAlignment(alignment))
        content.forEach{
            sb.append(it + " : " + RuleEngine.getText(it) + "<br/>")
        }
        sb.append(action.render())
        rich_content.forEach {
            sb.append(it.render())
        }
        return sb.append("</div>").toString()
    }
}
