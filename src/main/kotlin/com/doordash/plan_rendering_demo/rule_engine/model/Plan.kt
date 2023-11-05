package com.doordash.plan_rendering_demo.rule_engine.model

data class Plan(
    val id: Int,
    val name: String,
    val description: String,
    val expressions: List<Expression>,
    val benefits: List<Int>,
    val paymentSchedules: List<Int>,
    val planType: PlanType,
    val isEmployeeOnly: Boolean = false,
    val isTrial: Boolean = false,
    val signUpEmailCampaignId: Int = 0,
    val canBePaused: Boolean = false,
    val priority: Int = Int.MAX_VALUE,
)

