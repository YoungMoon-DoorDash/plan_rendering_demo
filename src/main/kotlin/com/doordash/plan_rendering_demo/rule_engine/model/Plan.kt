package com.doordash.plan_rendering_demo.rule_engine.model

data class Plan(
    val id: Long,
    val name: String = "undefined",
    val description: String = "",
    val benefits: List<Long> = listOf(),
    val paymentSchedules: List<Long> = listOf(),
    val planType: PlanType = PlanType.PLAN_TYPE_STANDARD_PLAN,
    val planStatus: PlanStatus = PlanStatus.PLAN_STATUS_PENDING,
    val isEmployeeOnly: Boolean = false,
    val isTrial: Boolean = false,
    val signUpEmailCampaignId: Long = 0,
    val canBePaused: Boolean = false,
    val priority: Long = Long.MAX_VALUE,
)

