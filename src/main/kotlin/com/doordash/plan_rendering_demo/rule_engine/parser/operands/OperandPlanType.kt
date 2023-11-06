package com.doordash.plan_rendering_demo.rule_engine.parser.operands

import com.doordash.plan_rendering_demo.rule_engine.model.RuleEngineContext

class OperandPlanType(
    private val context: RuleEngineContext
): Operand {
    override fun isType(type: OperandType): Boolean = type == OperandType.STRING

    override fun getString(): String = context.plan.planType.toString()
}