package com.doordash.plan_rendering_demo.rule_engine.parser.operands

enum class OperandType {
    BOOLEAN,
    NUMBER,
    OBJECT,
    STRING
}

data class ValueContainer(
    val number: Long = 0,
    val value: String = "",
    val flag: Boolean = false,
    val values: List<String> = emptyList(),
)

interface Operand {
    fun getType(): OperandType

    fun getType(propertyName: String): OperandType {
        throw IllegalStateException("Unsupported interface getPropertyType() is called.")
    }

    fun haveObject(): Boolean = false

    fun getProperty(propertyName: String): ValueContainer {
        throw IllegalStateException("Unsupported interface getProperty() is called.")
    }
}