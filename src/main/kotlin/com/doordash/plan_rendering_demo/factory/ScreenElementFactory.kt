package com.doordash.plan_rendering_demo.factory

import com.doordash.rpc.common.UIFlowScreenSectionType

fun String.filterName(): String =
    this.split("\\s".toRegex()).joinToString("_") { it.trim().uppercase() }


object ScreenElementFactory {
    fun getFormText(uiFlowScreenSectionType: UIFlowScreenSectionType): String = when(uiFlowScreenSectionType) {
        UIFlowScreenSectionType.TEXT, UIFlowScreenSectionType.BOLDED_TITLE_TEXT ->
            TEXT_ALIGNMENT + TEXT_CONTENT
        else -> "<div style='color:red;'>Not implemented yet</div>"
    }

    fun getFormAction(uiFlowScreenSectionType: UIFlowScreenSectionType): String = when(uiFlowScreenSectionType) {
        UIFlowScreenSectionType.TEXT -> "/elements/add/text"
        UIFlowScreenSectionType.BOLDED_TITLE_TEXT -> "/elements/add/bolded-title-text"
        else -> "#"
    }

    private const val TEXT_CONTENT =
"""
<div class="form-group">
    <label for="form-section-content">Content:</label>
    <textarea class="form-control" name="form-section-content" rows="10"></textarea>
</div>
"""

    private const val TEXT_ALIGNMENT =
"""
<label for="form-section-alignment">Alignment:</label>
<select class="select" name="form-section-alignment" style="width:400px;">
  <option value="0">Default</option>
  <option value="1">Center</option>
  <option value="2">Left</option>
  <option value="3">Right</option>
</select>            
"""
}