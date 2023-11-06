package com.doordash.plan_rendering_demo.rule_engine.parser.operands

import com.doordash.plan_rendering_demo.rule_engine.model.RuleEngineContext

class OperandMinSubtotal(
    private val context: RuleEngineContext
): Operand {
    override fun isType(type: OperandType): Boolean = type == OperandType.NUMBER

    override fun getNumber(): Long =
        context.overrideConfig["override_min_subtotal"]?.toLongOrNull() ?: 0
}