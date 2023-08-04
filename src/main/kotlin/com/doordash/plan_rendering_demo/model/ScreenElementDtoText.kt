package com.doordash.plan_rendering_demo.model

import com.doordash.rpc.common.UIFlowScreenTextAlignment
import kotlinx.serialization.Serializable

@Serializable
data class ScreenElementDtoText(
    val content: String,
    val alignment: UIFlowScreenTextAlignment?
)
