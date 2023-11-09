package com.doordash.plan_rendering_demo.rule_engine.parser.operands

import com.doordash.plan_rendering_demo.rule_engine.model.RuleEngineContext

class OperandSubscription(
    private val context: RuleEngineContext
): Operand {
    override fun getType(): OperandType = OperandType.OBJECT

    override fun haveObject(): Boolean =
        context.overrideConfig["override_have_subscription"]?.toBoolean() ?: false
}