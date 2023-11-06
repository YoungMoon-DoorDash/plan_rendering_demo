package com.doordash.plan_rendering_demo.rule_engine.parser.operands

import com.doordash.plan_rendering_demo.rule_engine.parser.ExpressionException

enum class OperandType {
    BOOLEAN,
    NUMBER,
    OBJECT,
    STRING
}

enum class ValueType {
    BOOLEAN,
    NUMBER,
    STRING
}

data class ValueContainer(
    val type: ValueType,
    val number: Long = 0,
    val value: String = "",
    val flag: Boolean = false,
    val values: List<String> = emptyList(),
)

interface Operand {
    fun getType(): OperandType

    fun getType(propertyName: String): OperandType {
        throw ExpressionException("Unsupported interface getPropertyType() is called.")
    }

    fun haveObject(): Boolean = false

    fun getProperty(propertyName: String): ValueContainer {
        throw ExpressionException("Unsupported interface getProperty() is called.")
    }
}