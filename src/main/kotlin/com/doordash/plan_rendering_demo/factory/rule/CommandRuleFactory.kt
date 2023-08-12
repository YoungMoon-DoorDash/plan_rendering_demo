package com.doordash.plan_rendering_demo.factory.rule

import com.doordash.plan_rendering_demo.factory.RuleHandler

object CommandRuleFactory {
    private val commandMap = mapOf(
        "get_active_subscription_plan" to CommandGetActiveSubscriptionPlan()
    )

    fun getCommand(command: String?): RuleHandler? =
        command?.let { commandMap[command] }
}