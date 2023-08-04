package com.doordash.plan_rendering_demo.model

import com.doordash.rpc.common.UIFlowScreenSectionType
import kotlinx.serialization.Serializable

@Serializable
data class ScreenElementDto(
    val id: Int,
    val sectionType: UIFlowScreenSectionType,
    val content: String
)