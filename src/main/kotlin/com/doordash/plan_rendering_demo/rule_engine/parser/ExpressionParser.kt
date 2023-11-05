package com.doordash.plan_rendering_demo.rule_engine.parser

import java.util.*

class ParserException(message: String) : Exception(message)

class ExpressionParser {
    private val operatorOrder = mapOf(
        ExpressionOperator.AND to 0,
        ExpressionOperator.OR to 0,
        ExpressionOperator.IN to 1,
        ExpressionOperator.HAVE to 1,
        ExpressionOperator.IS to 1,
        ExpressionOperator.NOT to 1,
        ExpressionOperator.LESS to 1,
        ExpressionOperator.GREATER to 1,
        ExpressionOperator.LESS_OR_EQUAL to 1,
        ExpressionOperator.GREATER_OR_EQUAL to 1,
        ExpressionOperator.TO to 1
    )
    private val operandNameChar = listOf('_', '.')

    fun convertToInfix(expression: String): List<ExpressionContainer> {
        val stack = Stack<ExpressionContainer>()
        val word = StringBuilder()
        val postfix = mutableListOf<ExpressionContainer>()

        for(i in expression.indices) {
            when (expression[i]) {
                '(' -> {
                    addToken(word.toString(), stack, postfix)
                    word.clear()
                    stack.push(
                        ExpressionContainer(
                            type = ExpressionContainerType.OPERATOR,
                            operator = ExpressionOperator.LEFT_BRACKET
                        )
                    )
                }
                ')' -> {
                    addToken(word.toString(), stack, postfix)
                    word.clear()
                    processRightBracket(stack, postfix)
                }
                '{' -> {
                    addToken(word.toString(), stack, postfix)
                    word.clear()
                    stack.push(
                        ExpressionContainer(
                            type = ExpressionContainerType.OPERATOR,
                            operator = ExpressionOperator.LEFT_BRACE
                        )
                    )
                }
                '}' -> {
                    addToken(word.toString(), stack, postfix)
                    word.clear()
                    processRightBrace(stack, postfix)
                }
                ',' -> {
                    if (word.isNotEmpty()) {
                        addValues(listOf(word.toString()), stack)
                    }
                    word.clear()
                }
                ' ' -> {
                    addToken(word.toString(), stack, postfix)
                    word.clear()
                }
                '>', '<' -> {
                    addToken(word.toString(), stack, postfix)
                    word.clear()
                    word.append(expression[i])
                }
                '=' -> word.append(expression[i])
                else -> {
                    if (expression[i].isLetterOrDigit() || expression[i] in operandNameChar) {
                        word.append(expression[i])
                    } else if (expression[i] != '\n') {
                        throw ParserException("Find an invalid character ${expression[i]} at $i-th position.")
                    }
                }
            }
        }

        addToken(word.toString(), stack, postfix)

        while (!stack.isEmpty()) {
            postfix.add(stack.pop())
        }

        return postfix
    }

    private fun processRightBracket(
        stack: Stack<ExpressionContainer>,
        postfix: MutableList<ExpressionContainer>,
    ) {
        val values = mutableListOf<String>()
        while (!stack.empty()) {
            val container = stack.pop()
            when (container.type) {
                ExpressionContainerType.VALUE -> values.addAll(container.values)
                ExpressionContainerType.OPERATOR -> {
                    if (container.operator == ExpressionOperator.LEFT_BRACKET) {
                        break
                    }

                    values.add(container.operator.value)
                }

                ExpressionContainerType.OPERAND -> values.add(container.operand.value)
            }
        }

        if (values.isEmpty()) {
            throw ParserException("Empty values in '()' is not allowed!")
        }

        postfix.add(
            ExpressionContainer(
                type = ExpressionContainerType.VALUE,
                values = values.sorted()
            )
        )
    }

    private fun processRightBrace(
        stack: Stack<ExpressionContainer>,
        postfix: MutableList<ExpressionContainer>,
    ) {
        // popup all container until left bracket
        while (!stack.isEmpty()) {
            val container = stack.pop()
            if (container.operator == ExpressionOperator.LEFT_BRACE) {
                break
            }

            postfix.add(container)
        }
    }

    private fun addToken(
        token: String,
        stack: Stack<ExpressionContainer>,
        postfix: MutableList<ExpressionContainer>
    ) {
        if (token.isBlank() || token.isEmpty()) {
            return
        }

        when (token) {
            // check operators
            ExpressionOperator.AND.value -> addOperator(ExpressionOperator.AND, stack, postfix)
            ExpressionOperator.OR.value -> addOperator(ExpressionOperator.OR, stack, postfix)
            ExpressionOperator.IN.value -> addOperator(ExpressionOperator.IN, stack, postfix)
            ExpressionOperator.HAVE.value -> addOperator(ExpressionOperator.HAVE, stack, postfix)
            ExpressionOperator.IS.value -> addOperator(ExpressionOperator.IS, stack, postfix)
            ExpressionOperator.NOT.value -> addOperator(ExpressionOperator.NOT, stack, postfix)
            ExpressionOperator.LESS.value -> addOperator(ExpressionOperator.LESS, stack, postfix)
            ExpressionOperator.GREATER.value -> addOperator(ExpressionOperator.GREATER, stack, postfix)
            ExpressionOperator.LESS_OR_EQUAL.value -> addOperator(ExpressionOperator.LESS_OR_EQUAL, stack, postfix)
            ExpressionOperator.GREATER_OR_EQUAL.value -> addOperator(ExpressionOperator.GREATER_OR_EQUAL, stack, postfix)
            ExpressionOperator.TO.value -> addOperator(ExpressionOperator.TO, stack, postfix)

            // check operands
            ExpressionOperand.COUNTRY.value -> addOperand(ExpressionOperand.COUNTRY, stack)
            ExpressionOperand.CONSUMER_ID.value -> addOperand(ExpressionOperand.CONSUMER_ID, stack)
            ExpressionOperand.MEMBERSHIP_SHARING.value -> addOperand(ExpressionOperand.MEMBERSHIP_SHARING, stack)
            ExpressionOperand.MINIMUM_SUBTOTAL.value -> addOperand(ExpressionOperand.MINIMUM_SUBTOTAL, stack)
            ExpressionOperand.PLAN_CAN_BE_PAUSED.value -> addOperand(ExpressionOperand.PLAN_CAN_BE_PAUSED, stack)
            ExpressionOperand.PLAN_EMPLOYEE_ONLY.value -> addOperand(ExpressionOperand.PLAN_EMPLOYEE_ONLY, stack)
            ExpressionOperand.PLAN_NAME.value -> addOperand(ExpressionOperand.PLAN_NAME, stack)
            ExpressionOperand.PLAN_PRIORITY.value -> addOperand(ExpressionOperand.PLAN_PRIORITY, stack)
            ExpressionOperand.PLAN_TYPE.value -> addOperand(ExpressionOperand.PLAN_TYPE, stack)
            ExpressionOperand.PLAN_TRIAL.value -> addOperand(ExpressionOperand.PLAN_TRIAL, stack)
            ExpressionOperand.SCHEDULE_TYPE.value -> addOperand(ExpressionOperand.SCHEDULE_TYPE, stack)
            ExpressionOperand.SCHEDULE_STATUS.value -> addOperand(ExpressionOperand.SCHEDULE_STATUS, stack)
            ExpressionOperand.SCHEDULE_PAYMENT_METHOD.value -> addOperand(ExpressionOperand.SCHEDULE_PAYMENT_METHOD, stack)
            ExpressionOperand.SUB_MARKET.value -> addOperand(ExpressionOperand.SUB_MARKET, stack)
            ExpressionOperand.SUBSCRIPTION.value -> addOperand(ExpressionOperand.SUBSCRIPTION, stack)
            ExpressionOperand.SUBSCRIPTION_STATUS.value -> addOperand(ExpressionOperand.SUBSCRIPTION_STATUS, stack)
            ExpressionOperand.TRANSITION.value -> addOperand(ExpressionOperand.TRANSITION, stack)
            ExpressionOperand.TREATMENT.value -> addOperand(ExpressionOperand.TREATMENT, stack)

            // values
            else -> addValues(listOf(token), stack)
        }
    }

    private fun addOperator(
        operator: ExpressionOperator,
        stack: Stack<ExpressionContainer>,
        postfix: MutableList<ExpressionContainer>
    ) {
        while (!stack.isEmpty()) {
            val container = stack.peek()
            if (container.type == ExpressionContainerType.OPERATOR) {
                if (container.operator == ExpressionOperator.LEFT_BRACE ||
                    compareOperator(operator, container.operator) <= 0
                ) {
                    break
                }
            }
            postfix.add(stack.pop())
        }

        stack.push(
            ExpressionContainer(
                type = ExpressionContainerType.OPERATOR,
                operator = operator
            )
        )
    }

    private fun compareOperator(first: ExpressionOperator, second: ExpressionOperator): Int {
        val firstOrder = operatorOrder[first] ?: 100
        val secondOrder = operatorOrder[second] ?: 100
        return secondOrder - firstOrder
    }

    private fun addOperand(
        operand: ExpressionOperand,
        stack: Stack<ExpressionContainer>,
    ) {
        stack.push(
            ExpressionContainer(
                type = ExpressionContainerType.OPERAND,
                operand = operand
            )
        )
    }

    private fun addValues(
        values: List<String>,
        stack: Stack<ExpressionContainer>,
    ) {
        stack.push(
            ExpressionContainer(
                type = ExpressionContainerType.VALUE,
                values = values
            )
        )
    }
}