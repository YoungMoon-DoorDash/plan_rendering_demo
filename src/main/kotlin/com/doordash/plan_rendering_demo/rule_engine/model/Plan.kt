package com.doordash.plan_rendering_demo.rule_engine.model

data class Plan(
    val id: Long,
    val name: String = "undefined",
    val description: String = "",
    val expressions: List<Expression> = listOf(),
    val benefits: List<Long> = listOf(),
    val paymentSchedules: List<Long> = listOf(),
    val planType: PlanType = PlanType.STANDARD_PLAN,
    val isEmployeeOnly: Boolean = false,
    val isTrial: Boolean = false,
    val signUpEmailCampaignId: Long = 0,
    val canBePaused: Boolean = false,
    val priority: Long = Long.MAX_VALUE,
)

