package com.doordash.plan_rendering_demo.rule_engine.parser.operands

import com.doordash.plan_rendering_demo.rule_engine.model.RuleEngineContext

class OperandTransition(
    private val context: RuleEngineContext
): Operand {
    override fun isType(type: OperandType): Boolean =
        type == OperandType.BOOLEAN || type == OperandType.STRING

    override fun supportHave(): Boolean = true

    override fun isExist(): Boolean =
        context.overrideConfig["override_transition"]?.toBoolean() ?: false

    override fun getString(): String =
        context.overrideConfig["override_transition_to"] ?: "undefined"
}