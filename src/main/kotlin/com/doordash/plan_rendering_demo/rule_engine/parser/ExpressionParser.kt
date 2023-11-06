package com.doordash.plan_rendering_demo.rule_engine.parser

import java.util.*

class ExpressionException(message: String) : Exception(message)

class ExpressionParser {
    companion object {
        private val operandNameChar = listOf('_', '.')

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
            ExpressionOperator.GREATER_OR_EQUAL to 1
        )
    }

    fun convertToInfix(expression: String) = convertToPostfixInternal(expression.lowercase())

    private fun convertToPostfixInternal(expression: String): List<ExpressionContainer> {
        val context = Context(
            index = 0,
            expression = expression,
            stack = Stack(),
            postfix = mutableListOf()
        )
        val word = StringBuilder()

        for(i in expression.indices) {
            context.index = i
            when (expression[i]) {
                '(' -> {
                    addToken(word.toString(), context)
                    word.clear()
                    context.pushOperator(ExpressionOperator.LEFT_BRACKET)
                }
                ')' -> {
                    addToken(word.toString(), context)
                    word.clear()
                    context.processRightBracket()
                }
                '{' -> {
                    addToken(word.toString(), context)
                    word.clear()
                    context.pushOperator(ExpressionOperator.LEFT_BRACE)
                }
                '}' -> {
                    addToken(word.toString(), context)
                    word.clear()
                    context.processRightBrace()
                }
                ',' -> {
                    if (word.isNotEmpty()) {
                        context.addValues(listOf(word.toString()))
                    }
                    word.clear()
                }
                ' ' -> {
                    addToken(word.toString(), context)
                    word.clear()
                }
                '>', '<' -> {
                    addToken(word.toString(), context)
                    word.clear()
                    word.append(expression[i])
                }
                '=' -> word.append(expression[i])
                else -> {
                    if (expression[i].isLetterOrDigit() || expression[i] in operandNameChar) {
                        word.append(expression[i])
                    } else if (expression[i] != '\n') {
                        throw ExpressionException("Find an invalid character ${expression[i]} at $i-th position.")
                    }
                }
            }
        }

        addToken(word.toString(), context)

        while (!context.stack.isEmpty()) {
            context.postfix.add(context.stack.pop())
        }

        return context.postfix
    }

    private fun addToken(
        token: String,
        context: Context,
    ) {
        if (token.isBlank() || token.isEmpty()) {
            return
        }

        when (token) {
            // check operators
            ExpressionOperator.AND.value -> context.addOperator(ExpressionOperator.AND)
            ExpressionOperator.OR.value -> context.addOperator(ExpressionOperator.OR)
            ExpressionOperator.IN.value -> context.addOperator(ExpressionOperator.IN)
            ExpressionOperator.HAVE.value -> context.addOperator(ExpressionOperator.HAVE)
            ExpressionOperator.IS.value -> context.addOperator(ExpressionOperator.IS)
            ExpressionOperator.NOT.value -> context.addOperator(ExpressionOperator.NOT)
            ExpressionOperator.LESS.value -> context.addOperator(ExpressionOperator.LESS)
            ExpressionOperator.GREATER.value -> context.addOperator(ExpressionOperator.GREATER)
            ExpressionOperator.LESS_OR_EQUAL.value -> context.addOperator(ExpressionOperator.LESS_OR_EQUAL)
            ExpressionOperator.GREATER_OR_EQUAL.value -> context.addOperator(ExpressionOperator.GREATER_OR_EQUAL)

            // check operands
            ExpressionOperand.BENEFIT.value -> context.addOperand(ExpressionOperand.BENEFIT)
            ExpressionOperand.CONSUMER_ID.value -> context.addOperand(ExpressionOperand.CONSUMER_ID)
            ExpressionOperand.COUNTRY.value -> context.addOperand(ExpressionOperand.COUNTRY)
            ExpressionOperand.MEMBERSHIP_SHARING.value -> context.addOperand(ExpressionOperand.MEMBERSHIP_SHARING)
            ExpressionOperand.MINIMUM_SUBTOTAL.value -> context.addOperand(ExpressionOperand.MINIMUM_SUBTOTAL)
            ExpressionOperand.PAYMENT_SCHEDULE.value -> context.addOperand(ExpressionOperand.PAYMENT_SCHEDULE)
            ExpressionOperand.PLAN.value -> context.addOperand(ExpressionOperand.PLAN)
            ExpressionOperand.SUB_MARKET.value -> context.addOperand(ExpressionOperand.SUB_MARKET)
            ExpressionOperand.SUBSCRIPTION.value -> context.addOperand(ExpressionOperand.SUBSCRIPTION)
            ExpressionOperand.TRANSITION.value -> context.addOperand(ExpressionOperand.TRANSITION)
            ExpressionOperand.TREATMENT.value -> context.addOperand(ExpressionOperand.TREATMENT)
            ExpressionOperand.TRIAL.value -> context.addOperand(ExpressionOperand.TRIAL)

            // values
            else -> {
                val words = token.split('.')
                if (words.size > 1) {
                    addObject(words, context)
                } else {
                    context.addValues(listOf(token))
                }
            }
        }
    }

    private fun addObject(words: List<String>, context: Context) {
        // object access. e.g. benefit.name
        val objectName = words[0]
        val values = listOf(words.subList(1, words.size).joinToString("."))
        when (objectName) {
            ExpressionOperand.BENEFIT.value -> context.addOperand(ExpressionOperand.BENEFIT)
            ExpressionOperand.MEMBERSHIP_SHARING.value -> context.addOperand(ExpressionOperand.MEMBERSHIP_SHARING)
            ExpressionOperand.PAYMENT_SCHEDULE.value -> context.addOperand(ExpressionOperand.PAYMENT_SCHEDULE)
            ExpressionOperand.PLAN.value -> context.addOperand(ExpressionOperand.PLAN)
            ExpressionOperand.SUBSCRIPTION.value -> context.addOperand(ExpressionOperand.SUBSCRIPTION)
            ExpressionOperand.TRANSITION.value -> context.addOperand(ExpressionOperand.TRANSITION)
            ExpressionOperand.TRIAL.value -> context.addOperand(ExpressionOperand.TRIAL)
            else -> throw ExpressionException("Unsupported object $objectName at ${context.index}")
        }
        context.addValues(values, ExpressionContainerType.PROPERTY)
    }

    private data class Context(
        var index: Int,
        val expression: String,
        val stack: Stack<ExpressionContainer>,
        val postfix: MutableList<ExpressionContainer>,
    ) {
        fun processRightBracket() {
            val values = mutableListOf<String>()
            var firstIndex = index
            while (!stack.empty()) {
                val container = stack.pop()
                firstIndex = container.index
                when (container.type) {
                    ExpressionContainerType.VALUE -> values.addAll(container.values)
                    ExpressionContainerType.OPERATOR -> {
                        if (container.operator == ExpressionOperator.LEFT_BRACKET) {
                            break
                        }

                        values.add(container.operator.value)
                    }

                    ExpressionContainerType.OPERAND -> values.add(container.operand.value)
                    ExpressionContainerType.PROPERTY -> values.addAll(container.values)
                }
            }

            if (values.isEmpty()) {
                throw ExpressionException("Empty values in '()' is not allowed at $firstIndex!")
            }

            postfix.add(
                ExpressionContainer(
                    type = ExpressionContainerType.VALUE,
                    values = values.sorted(),
                    index = firstIndex,
                )
            )
        }

        fun processRightBrace() {
            while (!stack.isEmpty()) {
                val container = stack.pop()
                if (container.operator == ExpressionOperator.LEFT_BRACE) {
                    break
                }

                postfix.add(container)
            }
        }

        fun pushOperator(operator: ExpressionOperator) {
            stack.push(
                ExpressionContainer(
                    type = ExpressionContainerType.OPERATOR,
                    operator = operator,
                    index = index,
                )
            )
        }

        fun addOperator(operator: ExpressionOperator) {
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
                    operator = operator,
                    index = index,
                )
            )
        }

        private fun compareOperator(first: ExpressionOperator, second: ExpressionOperator): Int {
            val firstOrder = operatorOrder[first] ?: 100
            val secondOrder = operatorOrder[second] ?: 100
            return secondOrder - firstOrder
        }

        fun addOperand(operand: ExpressionOperand) {
            stack.push(
                ExpressionContainer(
                    type = ExpressionContainerType.OPERAND,
                    operand = operand,
                    index = index,
                )
            )
        }

        fun addValues(values: List<String>, type: ExpressionContainerType = ExpressionContainerType.VALUE) {
            stack.push(
                ExpressionContainer(
                    type = type,
                    values = values,
                    index = index,
                )
            )
        }
    }
}