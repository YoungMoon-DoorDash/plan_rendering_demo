package com.doordash.plan_rendering_demo.rule_engine.parser.operands

import com.doordash.plan_rendering_demo.rule_engine.model.RuleEngineContext

class OperandTreatment(
    private val context: RuleEngineContext
): Operand {
    override fun getType(): OperandType = OperandType.STRING
}