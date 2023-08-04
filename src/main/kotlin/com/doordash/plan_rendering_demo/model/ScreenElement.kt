package com.doordash.plan_rendering_demo.model

interface ScreenElement {
    fun render(parameters: Map<String, String>): String
}