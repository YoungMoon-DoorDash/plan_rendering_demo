package com.doordash.plan_rendering_demo.rule_engine.parser

enum class ExpressionContainerType {
    OPERATOR,
    OPERAND,
    VALUE,
    PROPERTY,
}

data class ExpressionContainer(
    val type: ExpressionContainerType,
    val operator: ExpressionOperator = ExpressionOperator.UNDEFINED,
    val operand: ExpressionOperand = ExpressionOperand.UNDEFINED,
    val values: List<String> = listOf(),
    val index: Int = -1,
) {
    override fun toString(): String = when (type) {
        ExpressionContainerType.OPERATOR -> operator.value
        ExpressionContainerType.OPERAND -> operand.value
        ExpressionContainerType.PROPERTY, ExpressionContainerType.VALUE -> values.joinToString(",", prefix = "(", postfix = ")")
    }
}