package com.doordash.plan_rendering_demo.rule_engine.model

data class Benefit(
    val id: Int,
    val name: String,
    val description: String,
    val expression: Expression
)
