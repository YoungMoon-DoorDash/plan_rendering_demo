package com.doordash.plan_rendering_demo.factory.rule

import com.doordash.plan_rendering_demo.factory.RuleEngine
import com.doordash.plan_rendering_demo.factory.RuleHandler
import com.doordash.plan_rendering_demo.model.Rule
import com.doordash.plan_rendering_demo.model.subscription.SubscriptionPlan
import kotlinx.serialization.json.Json

class CommandGetActiveSubscriptionPlan: RuleHandler {

    override fun execute(rule: Rule, params: Map<String, String>): String {
        val consumerId = params["consumer_id"]?.toLong() ?: 0
        if (consumerId == 0L) {
            return "context.consumer_id parameter is missed."
        }

        val user = RuleEngine.getUserRepository().findById(consumerId)
        if (!user.isPresent) {
            return "Consumer is not found with id: $consumerId"
        }

        val planId = user.get().toMap()["subscription_plan_id"]
            ?.toInt() ?: 0
        if (planId == 0) {
            return "Consumer has no active subscription"
        }

        val activePlan = RuleEngine.getPlanRepository().findById(planId)
        if (!activePlan.isPresent) {
            return "Plan information is not found: consumer_id: $consumerId, plan_id: $planId"
        }

        val plan = activePlan.get()
        return Json.encodeToString(SubscriptionPlan.serializer(), plan)
    }
}