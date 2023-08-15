package com.doordash.plan_rendering_demo.factory.rule

import com.doordash.plan_rendering_demo.factory.RuleEngine
import com.doordash.plan_rendering_demo.factory.RuleHandler
import com.doordash.plan_rendering_demo.model.subscription.SubscriptionPlan

object CommandRuleFactory {
    private val commandMap = mapOf(
        "get_active_subscription_plan" to CommandGetActiveSubscriptionPlan(),
        "get_consumer_order" to CommandGetConsumerOrder()
    )

    fun getCommandList(): List<String> =
        commandMap.keys.toList()

    fun getCommand(command: String?): RuleHandler? =
        command?.let { commandMap[command] }

    fun validate(run: Map<String, String>): Pair<Boolean, String> =
        if (run.isEmpty()) {
            false to "Rule content is empty"
        } else if (run["command"].isNullOrEmpty()) {
            false to "The \"command\" field is not set"
        } else {
            val command = run["command"]
            getCommand(command)?.let {
                true to "valid"
            } ?: run {

                false to "The $command is not supported"
            }
        }

    fun findSubscriptionPlan(): Pair<String, SubscriptionPlan?> {
        val consumerId = RuleEngine.fromContext(RuleConstants.CONSUMER_ID_KEY)?.toLong() ?: 0
        if (consumerId == 0L) {
            "${RuleConstants.CONSUMER_ID_KEY} parameter is missed." to null
        }

        val user = RuleEngine.getUserRepository().findById(consumerId)
        if (!user.isPresent) {
            return "Consumer is not found with id: $consumerId" to null
        }

        val planId = user.get().toMap()["subscription_plan_id"]
            ?.toInt() ?: 0
        if (planId == 0) {
            return "Consumer has no active subscription" to null
        }

        val activePlan = RuleEngine.getPlanRepository().findById(planId)
        if (!activePlan.isPresent) {
            "Plan information is not found: consumer_id: $consumerId, plan_id: $planId" to null
        }

        return "valid" to activePlan.get()
    }
}