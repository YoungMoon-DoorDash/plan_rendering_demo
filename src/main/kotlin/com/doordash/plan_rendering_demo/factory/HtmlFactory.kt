package com.doordash.plan_rendering_demo.factory

import com.doordash.rpc.common.UIFlowScreenSectionType

object HtmlFactory {
    fun getHomeBanner(currentPage: String): String {
        val sb = StringBuilder("<div class='btn-group' style='padding-right:2pt;'>")
        sb.append(getBtnLink("Home", "/", currentPage == "home"))
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

    fun getElementsDropDown(): String {
        val sb = StringBuilder(
"""
    <div class='dropdown'>
        <button type="button" class="btn btn-info btn-sm dropdown-toggle" id="dropdownScreenElements" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
            New Screen Element
        </button>
        <ul class="dropdown-menu" aria-labelledby="dropdownScreenElements">
""".trimIndent()
        )

        UIFlowScreenSectionType.entries.forEach {
            if (it.ordinal > 0 && it.name != "UNRECOGNIZED") {
                sb.append("<li><a class='dropdown-item' href='/elements/add?section=${it.ordinal}'>${it.name}</a><li>")
            }
        }
        sb.append("</ul></div>")
        return sb.toString()
    }
}