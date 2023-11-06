package com.doordash.plan_rendering_demo.rule_engine.parser

import com.doordash.plan_rendering_demo.rule_engine.model.RuleEngineContext
import com.doordash.plan_rendering_demo.rule_engine.parser.operands.Operand
import com.doordash.plan_rendering_demo.rule_engine.parser.operands.OperandBenefit
import com.doordash.plan_rendering_demo.rule_engine.parser.operands.OperandConsumerId
import com.doordash.plan_rendering_demo.rule_engine.parser.operands.OperandCountry
import com.doordash.plan_rendering_demo.rule_engine.parser.operands.OperandMembershipSharing
import com.doordash.plan_rendering_demo.rule_engine.parser.operands.OperandMinSubtotal
import com.doordash.plan_rendering_demo.rule_engine.parser.operands.OperandPaymentSchedule
import com.doordash.plan_rendering_demo.rule_engine.parser.operands.OperandPlan
import com.doordash.plan_rendering_demo.rule_engine.parser.operands.OperandSubMarket
import com.doordash.plan_rendering_demo.rule_engine.parser.operands.OperandSubscription
import com.doordash.plan_rendering_demo.rule_engine.parser.operands.OperandTransition
import com.doordash.plan_rendering_demo.rule_engine.parser.operands.OperandTreatment
import com.doordash.plan_rendering_demo.rule_engine.parser.operands.OperandTrial

enum class ExpressionOperand(val value: String) {
    UNDEFINED("undefined"),
    BENEFIT("benefit"), // object
    CONSUMER_ID("consumer_id"),
    COUNTRY("country"),
    MEMBERSHIP_SHARING("membership_sharing"), // object
    MINIMUM_SUBTOTAL("min_subtotal"),
    PAYMENT_SCHEDULE("payment_schedule"),
    PLAN("plan"), // object
    SUB_MARKET("sub_market"),
    SUBSCRIPTION("subscription"), // object
    TRANSITION("transition"), // object
    TREATMENT("treatment"),
    TRIAL("trial"), // object
    BOOLEAN_TRUE("boolean_true"),
    BOOLEAN_FALSE("boolean_false")
}

fun ExpressionOperand.toOperand(context: RuleEngineContext): Operand =
    when (this) {
        ExpressionOperand.BENEFIT -> OperandBenefit(context)
        ExpressionOperand.CONSUMER_ID -> OperandConsumerId(context)
        ExpressionOperand.COUNTRY -> OperandCountry(context)
        ExpressionOperand.MEMBERSHIP_SHARING -> OperandMembershipSharing(context)
        ExpressionOperand.MINIMUM_SUBTOTAL -> OperandMinSubtotal(context)
        ExpressionOperand.PAYMENT_SCHEDULE -> OperandPaymentSchedule(context)
        ExpressionOperand.PLAN -> OperandPlan(context)
        ExpressionOperand.SUB_MARKET -> OperandSubMarket(context)
        ExpressionOperand.SUBSCRIPTION -> OperandSubscription(context)
        ExpressionOperand.TRANSITION -> OperandTransition(context)
        ExpressionOperand.TREATMENT -> OperandTreatment(context)
        ExpressionOperand.TRIAL -> OperandTrial(context)
        else -> throw IllegalArgumentException("Unsupported operands: $this")
    }
