package com.doordash.plan_rendering_demo.rule_engine.model

enum class PlanType(val value: String) {
    UNKNOWN_PLAN("unknown_plan"),
    ANNUAL_PLAN("annual_plan"),
    PARTNER_PLAN("partner_plan"),
    STANDARD_PLAN("standard_plan"),
    CORP_PLAN("corp_plan"),
    STUDENT_PLAN("student_plan"),
}

enum class PaymentScheduleType(val value: String) {
    UNKNOWN_SCHEDULE_TYPE("unknown"),
    DAILY("daily"),
    WEEKLY("weekly"),
    MONTHLY("monthly"),
    YEARLY("yearly")
}

enum class PaymentScheduleStatus(val value: String) {
    UNKNOWN_SCHEDULE_STATUS("unknown"),
    PENDING("pending"),
    PAID("paid"),
    FAILED("failed"),
    SCHEDULED("scheduled")
}