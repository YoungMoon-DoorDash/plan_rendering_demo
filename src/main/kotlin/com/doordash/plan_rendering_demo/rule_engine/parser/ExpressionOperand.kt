package com.doordash.plan_rendering_demo.rule_engine.parser

import com.doordash.plan_rendering_demo.rule_engine.model.RuleEngineContext
import com.doordash.plan_rendering_demo.rule_engine.parser.operands.Operand
import com.doordash.plan_rendering_demo.rule_engine.parser.operands.OperandConsumerId
import com.doordash.plan_rendering_demo.rule_engine.parser.operands.OperandCountry
import com.doordash.plan_rendering_demo.rule_engine.parser.operands.OperandEmployeeOnly
import com.doordash.plan_rendering_demo.rule_engine.parser.operands.OperandMembershipSharing
import com.doordash.plan_rendering_demo.rule_engine.parser.operands.OperandMinSubtotal
import com.doordash.plan_rendering_demo.rule_engine.parser.operands.OperandPlanCanBePaused
import com.doordash.plan_rendering_demo.rule_engine.parser.operands.OperandPlanName
import com.doordash.plan_rendering_demo.rule_engine.parser.operands.OperandPlanPriority
import com.doordash.plan_rendering_demo.rule_engine.parser.operands.OperandPlanTrial
import com.doordash.plan_rendering_demo.rule_engine.parser.operands.OperandPlanType
import com.doordash.plan_rendering_demo.rule_engine.parser.operands.OperandSchedulePaymentMethod
import com.doordash.plan_rendering_demo.rule_engine.parser.operands.OperandScheduleStatus
import com.doordash.plan_rendering_demo.rule_engine.parser.operands.OperandScheduleType
import com.doordash.plan_rendering_demo.rule_engine.parser.operands.OperandSubMarket
import com.doordash.plan_rendering_demo.rule_engine.parser.operands.OperandSubscription
import com.doordash.plan_rendering_demo.rule_engine.parser.operands.OperandSubscriptionStatus
import com.doordash.plan_rendering_demo.rule_engine.parser.operands.OperandTransition
import com.doordash.plan_rendering_demo.rule_engine.parser.operands.OperandTreatment
import com.doordash.plan_rendering_demo.rule_engine.parser.operands.OperandTrial

enum class ExpressionOperand(val value: String) {
    UNDEFINED("undefined"),
    BOOLEAN_TRUE("boolean_true"),
    BOOLEAN_FALSE("boolean_false"),
    // is, in
    CONSUMER_ID("consumer_id"),
    // is, in
    COUNTRY("country"),
    // have
    MEMBERSHIP_SHARING("membership_sharing"),
    // is, less, greater, less_or_equal, greater_or_equal
    MINIMUM_SUBTOTAL("min_subtotal"),
    // is
    PLAN_CAN_BE_PAUSED("plan.can_be_paused"),
    // is
    PLAN_EMPLOYEE_ONLY("plan.is_employee_only"),
    // is, in
    PLAN_NAME("plan.name"),
    // is, less, greater, less_or_equal, greater_or_equal
    PLAN_PRIORITY("plan.priority"),
    // have
    PLAN_TRIAL("plan.trial"),
    // is, in
    PLAN_TYPE("plan.type"),
    // is, in, have
    SCHEDULE_PAYMENT_METHOD("schedule.payment_method"),
    // is, in
    SCHEDULE_STATUS("schedule.status"),
    // is, in
    SCHEDULE_TYPE("schedule.type"),
    // is, in
    SUB_MARKET("sub_market"),
    // have
    SUBSCRIPTION("subscription"),
    // is, in
    SUBSCRIPTION_STATUS("subscription.status"),
    // have transition, transition to <plan_type>
    TRANSITION("transition"),
    // treatment <treatment_name> in (<treatment bucket list>)
    TREATMENT("treatment"),
    // is
    TRIAL("trial")
}

fun ExpressionOperand.toOperand(context: RuleEngineContext): Operand =
    when (this) {
        ExpressionOperand.COUNTRY -> OperandCountry(context)
        ExpressionOperand.CONSUMER_ID -> OperandConsumerId(context)
        ExpressionOperand.MEMBERSHIP_SHARING -> OperandMembershipSharing(context)
        ExpressionOperand.MINIMUM_SUBTOTAL -> OperandMinSubtotal(context)
        ExpressionOperand.PLAN_CAN_BE_PAUSED -> OperandPlanCanBePaused(context)
        ExpressionOperand.PLAN_EMPLOYEE_ONLY -> OperandEmployeeOnly(context)
        ExpressionOperand.PLAN_NAME -> OperandPlanName(context)
        ExpressionOperand.PLAN_PRIORITY -> OperandPlanPriority(context)
        ExpressionOperand.PLAN_TRIAL -> OperandPlanTrial(context)
        ExpressionOperand.PLAN_TYPE -> OperandPlanType(context)
        ExpressionOperand.SCHEDULE_PAYMENT_METHOD -> OperandSchedulePaymentMethod(context)
        ExpressionOperand.SCHEDULE_STATUS -> OperandScheduleStatus(context)
        ExpressionOperand.SCHEDULE_TYPE -> OperandScheduleType(context)
        ExpressionOperand.SUB_MARKET -> OperandSubMarket(context)
        ExpressionOperand.SUBSCRIPTION -> OperandSubscription(context)
        ExpressionOperand.SUBSCRIPTION_STATUS -> OperandSubscriptionStatus(context)
        ExpressionOperand.TRANSITION -> OperandTransition(context)
        ExpressionOperand.TREATMENT -> OperandTreatment(context)
        ExpressionOperand.TRIAL -> OperandTrial(context)
        else -> throw IllegalArgumentException("Unsupported operands: $this")
    }
