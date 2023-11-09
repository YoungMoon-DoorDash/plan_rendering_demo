package com.doordash.plan_rendering_demo.rule_engine.parser.operands

import com.doordash.plan_rendering_demo.rule_engine.model.RuleEngineContext
import com.doordash.plan_rendering_demo.rule_engine.parser.ExpressionException

class OperandTransition(
    private val context: RuleEngineContext
): Operand {
    override fun getType(): OperandType = OperandType.OBJECT

    override fun haveObject(): Boolean =
        context.overrideConfig["override_have_transition"]?.toBoolean() ?: false

    override fun getProperty(propertyName: String): ValueContainer =
        when (propertyName) {
            "type" ->
                ValueContainer(
                    ValueType.STRING,
                    value = context.overrideConfig["override_transition_type"] ?: "undefined"
                )
            else -> throw ExpressionException("Property $propertyName is not supported.")
        }
}