package com.doordash.plan_rendering_demo.rule_engine.parser

enum class ExpressionOperand(val value: String) {
    UNDEFINED("undefined"),
    // is, in
    COUNTRY("country"),
    // is, in
    CONSUMER_ID("consumer_id"),
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
    // is, in
    PLAN_TYPE("plan.type"),
    // have
    PLAN_TRIAL("plan.trial"),
    // is, in
    SCHEDULE_TYPE("schedule.type"),
    // is, in
    SCHEDULE_STATUS("schedule.status"),
    // is, in, have
    SCHEDULE_PAYMENT_METHOD("schedule.payment_method"),
    // is, in
    SUB_MARKET("sub_market"),
    // have
    SUBSCRIPTION("subscription"),
    // is, in
    SUBSCRIPTION_STATUS("subscription.status"),
    // have transition, transition to <plan_type>
    TRANSITION("transition"),
    // treatment <treatment_name> in (<treatment bucket list>)
    TREATMENT("treatment")
}
