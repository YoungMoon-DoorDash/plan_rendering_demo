package com.doordash.plan_rendering_demo.factory.rule

import com.doordash.plan_rendering_demo.factory.RuleEngine
import com.doordash.plan_rendering_demo.factory.RuleHandler
import com.doordash.plan_rendering_demo.model.Rule
import com.fasterxml.jackson.databind.ObjectMapper

/**
 * Load order information for given consumer
 * input: consumer_id, order_cart_id, order_uuid
 */
class CommandGetConsumerOrder: RuleHandler {
    override fun execute(rule: Rule, sb: StringBuilder?): String {
        val orderCartId = RuleEngine.fromContext(RuleConstants.ORDER_CART_ID_KEY)
        val orderUuid = RuleEngine.fromContext(RuleConstants.ORDER_UUID_KEY)

        if (orderCartId.isNullOrBlank() && orderUuid.isNullOrBlank()) {
            sb?.append("\nEither order_cart and order_uuid are missed. At least one of parameter is required")
            return "Either order_cart and order_uuid are missed. At least one of parameter is required"
        }

        sb?.append("\nNOT IMPLEMENTED YET")
        return "NOT IMPLEMENTED YET"
    }
}
