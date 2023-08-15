package com.doordash.plan_rendering_demo.factory.rule

import com.doordash.plan_rendering_demo.factory.RuleEngine
import com.doordash.plan_rendering_demo.factory.RuleHandler
import com.doordash.plan_rendering_demo.model.Rule
import com.fasterxml.jackson.databind.ObjectMapper

/**
 * Find active subscription for given consumer.
 *
 * input: consumer_id
 */
class CommandGetActiveSubscriptionPlan: RuleHandler {
    override fun execute(rule: Rule, sb: StringBuilder?): String {
        val (message, activePlan) = CommandRuleFactory.findSubscriptionPlan()
        sb?.append("\n")?.append(message)

        activePlan?.let {
            val plan = ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(activePlan)
            RuleEngine.toContext(RuleConstants.GET_ACTIVE_SUBSCRIPTION_PLAN_KEY, plan)
            return plan
        } ?: run {
            return message
        }
    }
}