package com.doordash.plan_rendering_demo.rule_engine.parser.operands

enum class OperandType {
    NUMBER,
    STRING,
    BOOLEAN
}

interface Operand {
    fun isType(type: OperandType): Boolean

    fun getNumber(): Long {
        throw IllegalStateException("Unsupported interface getNumber() is called.")
    }

    fun getString(): String {
        throw IllegalStateException("Unsupported interface getString() is called.")
    }

    fun getBoolean(): Boolean {
        throw IllegalStateException("Unsupported interface isTrue() is called.")
    }

    fun supportHave(): Boolean = false

    fun isExist(): Boolean {
        throw IllegalStateException("Unsupported interface isExist() is called.")
    }
}