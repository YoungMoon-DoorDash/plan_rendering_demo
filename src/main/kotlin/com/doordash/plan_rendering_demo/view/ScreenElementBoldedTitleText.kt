package com.doordash.plan_rendering_demo.view

import com.doordash.plan_rendering_demo.model.ScreenElement
import com.doordash.rpc.common.UIFlowScreenTextAlignment

class ScreenElementBoldedTitleText(
    private val content: String,
    private val alignment: UIFlowScreenTextAlignment
): ScreenElement {
    override fun render(parameters: Map<String, String>): String = when(alignment) {
        UIFlowScreenTextAlignment.RIGHT ->
            "<div class='container border text-end fw-bold' style='width: 640px;'>${content.replaceParameters(parameters)}</div>"
        UIFlowScreenTextAlignment.CENTER ->
            "<div class='container border text-center fw-bold' style='width: 640px;'>${content.replaceParameters(parameters)}</div>"
        else -> "<div class='container border text-start fw-bold' style='width: 640px;'>${content.replaceParameters(parameters)}</div>"
    }
}