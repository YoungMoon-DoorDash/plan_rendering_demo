package com.doordash.plan_rendering_demo.factory

import com.doordash.plan_rendering_demo.model.RuleType
import com.doordash.rpc.common.UIFlowScreenSectionType
import com.doordash.rpc.common.UIFlowScreenTextAlignment
import org.apache.coyote.http11.Constants.a

object HtmlFactory {
    fun getHomeBanner(currentPage: String): String {
        val sb = StringBuilder("<div class='btn-group' style='padding-right:2pt;'>")
        sb.append(getBtnLink("Home", "/", currentPage == "home"))
        sb.append(getBtnLink("Screen", "/screen", currentPage == "screen"))
        sb.append(getBtnLink("Flow", "/flow", currentPage == "flow"))
        sb.append(getBtnLink("Rule", "/rule", currentPage == "rule"))
        sb.append(getBtnLink("Plan", "/plan", currentPage == "plan"))
        sb.append(getBtnLink("Trial", "/trial", currentPage == "trial"))
        sb.append(getBtnLink("Text", "/text", currentPage == "text"))
        sb.append(getBtnLink("User", "/user", currentPage == "user"))
        sb.append(getBtnLink("Experiment", "/experiment", currentPage == "experiment"))
        sb.append("</div>")
        return sb.toString()
    }

    private fun getBtnLink(title: String, path: String, isSelected: Boolean): String =
        if (isSelected) {
            "<button type='button' class='btn btn-primary btn-sm'>$title</button> "
        } else {
            "<a href='$path' class='btn btn-outline-primary btn-sm'>$title</a> "
        }

    fun getTitleLine(
        basePath: String,
        title: String,
        search: String,
        withAdd: Boolean = true,
        withImport: Boolean = true,
        placeHolder:String = "Search by name"
    ): String {
        val sb = StringBuilder(
            """
<table style='margin-top:10px;width:100%;'>
    <tr>
        <td nowrap><b>Registered $title</b></td>
        <td style='padding-left:16px;text-align:right;' nowrap>
            <input type='text' id='search' value="$search" size='32' placeholder='$placeHolder'/>
            <button type='button' class='btn btn-outline-primary btn-sm' onclick='on_search()'>Find</button>
"""
        )
        if (withAdd) {
            sb.append("<a href='$basePath/add' class='btn btn-outline-primary btn-sm'>Add $title</a>")
        }
        if (withImport) {
            sb.append("<a href='$basePath/import' class='btn btn-outline-primary btn-sm'>Import</a>")
        }
        return sb.append("</td></tr></table>").toString()
    }

    fun getScreenElementsSelection(): String {
        val sb = StringBuilder("<select name='elementType' id='elementType' onchange='onScreenItemChanged()'>")
        UIFlowScreenSectionType.entries.sortedBy { it.name }
            .forEach {
                if (it.ordinal > 0 && it.name != "UNRECOGNIZED") {
                    sb.append("<option value='${it.name}'>${it.name}</option>")
                }
            }
        sb.append("</select>")
        return sb.toString()
    }

    fun getRuleTypeSelect(curSelect: RuleType): String {
        val sb = StringBuilder("<select class='form-control' id='type' name='type'>")
        RuleType.entries.forEach {
            if (it == curSelect)
                sb.append("<option value='${it.ordinal}' selected>${it.name}</option>")
            else
                sb.append("<option value='${it.ordinal}'>${it.name}</option>")
        }

        sb.append("</select>")
        return sb.toString()
    }

    fun getAlignment(alignment: UIFlowScreenTextAlignment, borderColor:String = "lightgray") = when (alignment) {
        UIFlowScreenTextAlignment.RIGHT ->
            "<div style='width:100%;margin:2pt;text-align:right;border:1pt solid $borderColor;padding-right:6pt;'>"
        UIFlowScreenTextAlignment.CENTER ->
            "<div style='width:100%;margin:2pt;text-align:center;border:1pt solid $borderColor;padding-right:6pt;'>"
        else ->
            "<div style='width:100%;margin:2pt;border:1pt solid $borderColor;padding-right:6pt;'>"
    }
}