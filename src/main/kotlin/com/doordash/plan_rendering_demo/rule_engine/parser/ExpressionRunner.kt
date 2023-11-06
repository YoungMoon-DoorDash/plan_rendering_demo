package com.doordash.plan_rendering_demo.rule_engine.parser

import com.doordash.plan_rendering_demo.rule_engine.model.RuleEngineContext
import com.doordash.plan_rendering_demo.rule_engine.parser.operands.OperandType
import java.util.Stack

class ExpressionRunner(
    private val context: RuleEngineContext
) {
    fun run(postFix: List<ExpressionContainer>): Boolean {
        val stack = Stack<ExpressionContainer>()
        postFix.forEach { container ->
            when (container.type) {
                ExpressionContainerType.OPERAND, ExpressionContainerType.VALUE -> stack.push(container)
                else -> runOperator(container.operator, stack)
            }
        }

        return getOperandBoolean(stack)
    }

    private fun runOperator(operator: ExpressionOperator, stack: Stack<ExpressionContainer>) = when (operator) {
        ExpressionOperator.AND -> calcAnd(stack)
        ExpressionOperator.OR -> calcOr(stack)
        ExpressionOperator.IN -> calcIn(stack)
        ExpressionOperator.GREATER -> calcGreater(stack)
        ExpressionOperator.GREATER_OR_EQUAL -> calcGreaterOrEqual(stack)
        ExpressionOperator.LESS -> calcLess(stack)
        ExpressionOperator.LESS_OR_EQUAL -> calcLessOrEqual(stack)
        ExpressionOperator.TO -> calcTo(stack)
        ExpressionOperator.IS -> calcIs(stack)
        ExpressionOperator.HAVE -> calcHave(stack)
        ExpressionOperator.NOT -> calcNot(stack)
        else -> throw IllegalArgumentException("Unsupported operator: $operator")
    }

    private fun calcAnd(stack: Stack<ExpressionContainer>) {
        val right = getOperandBoolean(stack)
        val left = getOperandBoolean(stack)
        addResult(left && right, stack)
    }

    private fun calcOr(stack: Stack<ExpressionContainer>) {
        val right = getOperandBoolean(stack)
        val left = getOperandBoolean(stack)
        addResult(left || right, stack)
    }

    private fun calcIn(stack: Stack<ExpressionContainer>) {
        val valueList = getContainerValueList(stack)

        val container = stack.pop()
        if (container.type != ExpressionContainerType.OPERAND) {
            throw ExpressionException("Expected operand, but got ${container.type} at ${container.index}")
        }

        if (container.operand == ExpressionOperand.TREATMENT) {
            val left = context.treatments[getContainerValue(stack)] ?: "control"
            addResult(left in valueList, stack)
        } else {
            val operand = container.operand.toOperand(context)
            if (operand.isType(OperandType.NUMBER)) {
                val left = operand.getNumber().toString()
                addResult(left in valueList, stack)
            } else if (operand.isType(OperandType.STRING)) {
                val left = operand.getString().lowercase()
                addResult(left in valueList, stack)
            } else {
                throw ExpressionException("Expected string operand, but got ${container.operand} at ${container.index}")
            }
        }
    }

    private fun calcIs(stack: Stack<ExpressionContainer>) {
        addResult(getOperandBoolean(stack), stack)
    }

    private fun calcLess(stack: Stack<ExpressionContainer>) {
        val right = getContainerNumber(stack)
        val left = getOperandNumber(stack)
        addResult(left < right, stack)
    }

    private fun calcLessOrEqual(stack: Stack<ExpressionContainer>) {
        val right = getContainerNumber(stack)
        val left = getOperandNumber(stack)
        addResult(left <= right, stack)
    }

    private fun calcGreater(stack: Stack<ExpressionContainer>) {
        val right = getContainerNumber(stack)
        val left = getOperandNumber(stack)
        addResult(left > right, stack)
    }

    private fun calcGreaterOrEqual(stack: Stack<ExpressionContainer>) {
        val right = getContainerNumber(stack)
        val left = getOperandNumber(stack)
        addResult(left >= right, stack)
    }

    private fun calcTo(stack: Stack<ExpressionContainer>) {
        val value = getContainerValue(stack)
        val left = getOperandString(stack)
        addResult(left == value, stack)
    }

    private fun calcHave(stack: Stack<ExpressionContainer>) {
        addResult(getOperandHave(stack), stack)
    }

    private fun calcNot(stack: Stack<ExpressionContainer>) {
        val value = getOperandBoolean(stack)
        addResult(!value , stack)
    }

    private fun addResult(
        result: Boolean,
        stack: Stack<ExpressionContainer>,
    ) {
        stack.push(
            ExpressionContainer(
                type = ExpressionContainerType.OPERAND,
                operand = if (result) ExpressionOperand.BOOLEAN_TRUE else ExpressionOperand.BOOLEAN_FALSE
            )
        )
    }

    private fun getOperandBoolean(stack: Stack<ExpressionContainer>): Boolean {
        val container = stack.pop()
        if (container.type != ExpressionContainerType.OPERAND) {
            throw ExpressionException("Expected operand, but got ${container.type} at ${container.index}")
        }

        return when (container.operand) {
            ExpressionOperand.BOOLEAN_TRUE -> true
            ExpressionOperand.BOOLEAN_FALSE -> false
            else -> {
                val operand = container.operand.toOperand(context)
                if (operand.isType(OperandType.BOOLEAN)) {
                    operand.getBoolean()
                } else {
                    throw ExpressionException("Expected boolean operand, but got ${container.operand} at ${container.index}")
                }
            }
        }
    }

    private fun getOperandString(stack: Stack<ExpressionContainer>): String {
        val container = stack.pop()
        if (container.type != ExpressionContainerType.OPERAND) {
            throw ExpressionException("Expected operand, but got ${container.type} at ${container.index}")
        }

        val operand = container.operand.toOperand(context)
        if (!operand.isType(OperandType.STRING)) {
            throw ExpressionException("Expected string operand, but got ${container.operand} at ${container.index}")
        }

        return operand.getString().lowercase()
    }

    private fun getOperandNumber(stack: Stack<ExpressionContainer>): Long {
        val container = stack.pop()
        if (container.type != ExpressionContainerType.OPERAND) {
            throw ExpressionException("Expected operand, but got ${container.type} at ${container.index}")
        }

        val operand = container.operand.toOperand(context)
        if (!operand.isType(OperandType.NUMBER)) {
            throw ExpressionException("Expected number operand, but got ${container.operand} at ${container.index}")
        }

        return operand.getNumber()
    }

    private fun getOperandHave(stack: Stack<ExpressionContainer>): Boolean {
        val container = stack.pop()
        if (container.type != ExpressionContainerType.OPERAND) {
            throw ExpressionException("Expected operand, but got ${container.type} at ${container.index}")
        }

        val operand = container.operand.toOperand(context)
        if (operand.supportHave()) {
            return operand.isExist()
        }

        throw ExpressionException("Expected operand supporting 'have' operation, but got ${container.operand} at ${container.index}")
    }

    private fun getContainerValue(stack: Stack<ExpressionContainer>): String {
        val container = stack.pop()
        if (container.type != ExpressionContainerType.VALUE) {
            throw ExpressionException("Expected operand, but got ${container.type} at ${container.index}")
        }

        if (container.values.size != 1) {
            throw ExpressionException("Expected single value, but got ${container.values.size} at ${container.index}")
        }

        return container.values[0]
    }

    private fun getContainerNumber(stack: Stack<ExpressionContainer>): Long {
        val container = stack.pop()
        if (container.type != ExpressionContainerType.VALUE) {
            throw ExpressionException("Expected operand, but got ${container.type} at ${container.index}")
        }

        if (container.values.size != 1) {
            throw ExpressionException("Expected single value, but got ${container.values.size} at ${container.index}")
        }

        return container.values[0].toLongOrNull() ?: run {
            throw ExpressionException("Expected number, but got ${container.values[0]} at ${container.index}")
        }
    }

    private fun getContainerValueList(stack: Stack<ExpressionContainer>): List<String> {
        val container = stack.pop()
        if (container.type != ExpressionContainerType.VALUE) {
            throw ExpressionException("Expected operand, but got ${container.type} at ${container.index}")
        }

        return container.values
    }
}