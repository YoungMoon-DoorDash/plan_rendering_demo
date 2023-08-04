package com.doordash.plan_rendering_demo.model

import com.doordash.rpc.common.UIFlowScreenSectionType
import com.doordash.rpc.common.UIFlowScreenTextAlignment
import kotlinx.serialization.Serializable

@Serializable
data class ScreenElement(
    val sectionType: UIFlowScreenSectionType,
    val content: String,
    val alignment: UIFlowScreenTextAlignment = UIFlowScreenTextAlignment.DEFAULT
)