package com.doordash.plan_rendering_demo.rule_engine.model

enum class PlanType(val value: String) {
    PLAN_TYPE_UNKNOWN("unknown_plan"),
    PLAN_TYPE_ANNUAL_PLAN("annual_plan"),
    PLAN_TYPE_PARTNER_PLAN("partner_plan"),
    PLAN_TYPE_STANDARD_PLAN("standard_plan"),
    PLAN_TYPE_CORP_PLAN("corp_plan"),
    PLAN_TYPE_STUDENT_PLAN("student_plan"),
}

enum class PlanStatus(val value: String) {
    PLAN_STATUS_UNKNOWN("unknown"),
    PLAN_STATUS_ACTIVE("active"),
    PLAN_STATUS_PENDING("pending"),
    PLAN_STATUS_PAUSED("paused"),
    PLAN_STATUS_REMOVED("removed")
}

enum class PaymentScheduleType(val value: String) {
    PAYMENT_SCHEDULE_TYPE_UNKNOWN("unknown"),
    PAYMENT_SCHEDULE_TYPE_DAILY("daily"),
    PAYMENT_SCHEDULE_TYPE_WEEKLY("weekly"),
    PAYMENT_SCHEDULE_TYPE_MONTHLY("monthly"),
    PAYMENT_SCHEDULE_TYPE_YEARLY("yearly")
}

enum class PaymentScheduleStatus(val value: String) {
    PAYMENT_SCHEDULE_STATUS_UNKNOWN("unknown"),
    PAYMENT_SCHEDULE_STATUS_ACTIVE("active"),
    PAYMENT_SCHEDULE_STATUS_PENDING("pending"),
    PAYMENT_SCHEDULE_STATUS_PAUSED("paused"),
    PAYMENT_SCHEDULE_STATUS_REMOVED("removed")
}