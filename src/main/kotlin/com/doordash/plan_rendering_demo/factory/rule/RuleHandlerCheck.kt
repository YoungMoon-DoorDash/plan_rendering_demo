package com.doordash.plan_rendering_demo.factory.rule

import com.doordash.plan_rendering_demo.factory.RuleHandler
import com.doordash.plan_rendering_demo.model.Rule
import kotlinx.serialization.json.Json

class RuleHandlerCheck: RuleHandler {
    override fun execute(rule: Rule, params: Map<String, String>): String {
        val conditions = Json.decodeFromString<Map<String, String>>(rule.run)
        conditions.forEach { (key, value) ->
            if (params[key]?.lowercase() != value.lowercase()) {
                return "false"
            }
        }

        return "true"
    }
}