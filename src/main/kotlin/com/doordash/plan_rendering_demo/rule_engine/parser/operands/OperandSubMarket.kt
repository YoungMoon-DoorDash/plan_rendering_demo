package com.doordash.plan_rendering_demo.rule_engine.parser.operands

import com.doordash.plan_rendering_demo.rule_engine.model.RuleEngineContext

class OperandSubMarket(
    private val context: RuleEngineContext
): Operand {
    override fun getType(): OperandType = OperandType.NUMBER

    override fun getProperty(propertyName: String): ValueContainer =
        ValueContainer(type = ValueType.NUMBER, number = context.subMarketId ?: 0)
}