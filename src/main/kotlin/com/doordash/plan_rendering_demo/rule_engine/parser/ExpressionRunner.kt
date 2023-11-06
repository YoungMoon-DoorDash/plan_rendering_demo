package com.doordash.plan_rendering_demo.rule_engine.parser

import com.doordash.plan_rendering_demo.rule_engine.model.RuleEngineContext
import com.doordash.plan_rendering_demo.rule_engine.parser.operands.OperandType
import com.doordash.plan_rendering_demo.rule_engine.parser.operands.ValueType
import java.util.Stack

class ExpressionRunner(
    private val context: RuleEngineContext
) {
    companion object {
        private const val DEFAULT_PROPERTY_NAME = "undefined"
    }

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
            throw ExpressionException("Expected an operand, but got ${container.type} at ${container.index}")
        }

        if (container.operand == ExpressionOperand.TREATMENT) {
            val left = context.treatments[getContainerValue(stack)] ?: "control"
            addResult(left in valueList, stack)
        } else {
            val operand = container.operand.toOperand(context)
            when (operand.getType()) {
                OperandType.OBJECT -> {
                    val propertyName = getContainerProperty(stack)
                    val propertyValue = operand.getProperty(propertyName)
                    when (propertyValue.type) {
                        ValueType.NUMBER -> addResult(propertyValue.number.toString() in valueList, stack)
                        ValueType.STRING -> addResult(propertyValue.value in valueList, stack)
                        ValueType.BOOLEAN -> addResult(propertyValue.flag.toString() in valueList, stack)
                    }
                }

                OperandType.NUMBER -> {
                    val value = operand.getProperty(DEFAULT_PROPERTY_NAME).number.toString()
                    addResult(value in valueList, stack)
                }

                OperandType.STRING -> {
                    val value = operand.getProperty(DEFAULT_PROPERTY_NAME).value
                    addResult(value in valueList, stack)
                }

                OperandType.BOOLEAN -> {
                    val value = operand.getProperty(DEFAULT_PROPERTY_NAME).flag.toString()
                    addResult(value in valueList, stack)
                }
            }
        }
    }

    private fun calcIs(stack: Stack<ExpressionContainer>) {
        val right = getContainerValueOrBoolean(stack)

        val container = stack.pop()
        when (container.type) {
            ExpressionContainerType.PROPERTY, ExpressionContainerType.OPERATOR ->
                throw ExpressionException("Expected an operand, but got ${container.type} at ${container.index}")
            ExpressionContainerType.OPERAND -> {
                val operand = container.operand.toOperand(context)
                when (operand.getType()) {
                    OperandType.OBJECT -> {
                        val propertyName = getContainerProperty(stack)
                        val propertyValue = operand.getProperty(propertyName)
                        when (propertyValue.type) {
                            ValueType.NUMBER -> addResult(propertyValue.number.toString() == right, stack)
                            ValueType.STRING -> addResult(propertyValue.value == right, stack)
                            ValueType.BOOLEAN -> addResult(propertyValue.flag.toString() == right, stack)
                        }
                    }

                    OperandType.NUMBER -> {
                        val value = operand.getProperty(DEFAULT_PROPERTY_NAME).number.toString()
                        addResult(value == right, stack)
                    }

                    OperandType.STRING -> {
                        val value = operand.getProperty(DEFAULT_PROPERTY_NAME).value
                        addResult(value == right, stack)
                    }

                    OperandType.BOOLEAN -> {
                        val value = operand.getProperty(DEFAULT_PROPERTY_NAME).flag.toString()
                        addResult(value == right, stack)
                    }
                }
            }

            ExpressionContainerType.VALUE -> {
                val left = getContainerValue(stack)
                addResult(left == right, stack)
            }
        }
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

    private fun calcHave(stack: Stack<ExpressionContainer>) {
        val container = stack.pop()
        if (container.type != ExpressionContainerType.OPERAND) {
            throw ExpressionException("Expected an operand, but got ${container.type} at ${container.index}")
        }

        val operand = container.operand.toOperand(context)
        if (operand.getType() != OperandType.OBJECT) {
            throw ExpressionException("Expected an object operand, but got ${operand.getType()} at ${container.index}")
        }

        addResult(operand.haveObject(), stack)
    }

    private fun calcNot(stack: Stack<ExpressionContainer>) {
        val value = getOperandBoolean(stack)
        addResult(!value, stack)
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
            throw ExpressionException("Expected an operand, but got ${container.type} at ${container.index}")
        }

        return when (container.operand) {
            ExpressionOperand.BOOLEAN_TRUE -> true
            ExpressionOperand.BOOLEAN_FALSE -> false
            else -> {
                val operand = container.operand.toOperand(context)
                when (operand.getType()) {
                    OperandType.BOOLEAN -> operand.getProperty(DEFAULT_PROPERTY_NAME).flag
                    OperandType.OBJECT -> {
                        val propertyName = getContainerProperty(stack)
                        val propertyValue = operand.getProperty(propertyName)
                        if (propertyValue.type == ValueType.BOOLEAN) {
                            propertyValue.flag
                        } else {
                            throw ExpressionException("Expected boolean operand, but got $propertyName with ${propertyValue.type} at ${container.index}")
                        }
                    }
                    else -> throw ExpressionException("Expected boolean operand, but got ${container.operand} at ${container.index}")
                }
            }
        }
    }

    private fun getOperandNumber(stack: Stack<ExpressionContainer>): Long {
        val container = stack.pop()
        if (container.type != ExpressionContainerType.OPERAND) {
            throw ExpressionException("Expected an operand, but got ${container.type} at ${container.index}")
        }

        val operand = container.operand.toOperand(context)
        return if (operand.getType() == OperandType.NUMBER) {
            operand.getProperty(DEFAULT_PROPERTY_NAME).number
        } else {
            throw ExpressionException("Expected number operand, but got ${container.operand} at ${container.index}")
        }
    }

    private fun getContainerValueOrBoolean(stack: Stack<ExpressionContainer>): String {
        val container = stack.pop()
        if (container.type == ExpressionContainerType.OPERAND) {
            return when (container.operand) {
                ExpressionOperand.BOOLEAN_TRUE -> "true"
                ExpressionOperand.BOOLEAN_FALSE -> "false"
                else -> throw ExpressionException("Expected boolean operand, but got ${container.operand} at ${container.index}")
            }
        }

        checkSingleContainerValue(container)
        return container.values[0]
    }

    private fun getContainerValue(stack: Stack<ExpressionContainer>): String {
        val container = stack.pop()
        checkSingleContainerValue(container)
        return container.values[0]
    }

    private fun checkSingleContainerValue(container: ExpressionContainer) {
        if (container.type != ExpressionContainerType.VALUE) {
            throw ExpressionException("Expected value, but got ${container.type} at ${container.index}")
        }

        if (container.values.size != 1) {
            throw ExpressionException("Expected single value, but got ${container.values.size} at ${container.index}")
        }
    }

    private fun getContainerNumber(stack: Stack<ExpressionContainer>): Long {
        val container = stack.pop()
        checkSingleContainerValue(container)

        return container.values[0].toLongOrNull() ?: run {
            throw ExpressionException("Expected number, but got ${container.values[0]} at ${container.index}")
        }
    }

    private fun getContainerValueList(stack: Stack<ExpressionContainer>): List<String> {
        val container = stack.pop()
        if (container.type != ExpressionContainerType.VALUE) {
            throw ExpressionException("Expected an operand, but got ${container.type} at ${container.index}")
        }

        return container.values
    }

    private fun getContainerProperty(stack: Stack<ExpressionContainer>): String {
        val container = stack.pop()
        if (container.type != ExpressionContainerType.PROPERTY) {
            throw ExpressionException("Expected property, but got ${container.type} at ${container.index}")
        }

        if (container.values.size != 1) {
            throw ExpressionException("Expected single value, but got ${container.values.size} at ${container.index}")
        }

        return container.values[0]
    }
}