package com.doordash.plan_rendering_demo.rule_engine.parser

enum class ExpressionOperator(val value: String) {
    UNDEFINED("undefined"),
    AND("and"),
    OR("or"),
    IN("in"),
    HAVE("have"),
    IS("is"),
    NOT("not"),
    LESS("<"),
    GREATER(">"),
    LESS_OR_EQUAL("<="),
    GREATER_OR_EQUAL(">="),
    TO("to"),
    LEFT_BRACE("{"),
    LEFT_BRACKET("("),
}