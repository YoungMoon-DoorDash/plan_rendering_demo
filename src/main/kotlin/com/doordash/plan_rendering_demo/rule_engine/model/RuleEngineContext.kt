package com.doordash.plan_rendering_demo.rule_engine.model

data class RuleEngineContext(
    val consumerId: Long,
    val plan: Plan? = null,
    val subMarketId: Long? = null,
    val country: String = "US",
    val treatments: Map<String, String> = emptyMap(),
    val overrideConfig: Map<String, String> = emptyMap(),
)
