package com.doordash.plan_rendering_demo.view

import com.doordash.plan_rendering_demo.model.ScreenElement
import com.doordash.rpc.common.UIFlowScreenTextAlignment

class ScreenElementText(
    private val content: String,
    private val alignment: UIFlowScreenTextAlignment
): ScreenElement {
    override fun render(parameters: Map<String, String>): String = when(alignment) {
        UIFlowScreenTextAlignment.RIGHT ->
            "<div class='container border text-end' style='width: 640px;'>${content.replaceParameters(parameters)}</div>"
        UIFlowScreenTextAlignment.CENTER ->
            "<div class='container border text-center' style='width: 640px;'>${content.replaceParameters(parameters)}</div>"
        else -> "<div class='container border text-start' style='width: 640px;'>${content.replaceParameters(parameters)}</div>"
    }
}