package ccom.doordash.plan_rendering_demo.rule_engine.model

import com.doordash.plan_rendering_demo.rule_engine.model.Expression
import com.doordash.plan_rendering_demo.rule_engine.model.PaymentScheduleStatus
import com.doordash.plan_rendering_demo.rule_engine.model.PaymentScheduleType

data class PaymentSchedule(
    val id: Int,
    val name: String,
    val description: String,
    val expression: Expression,
    val paymentAmount: Int,
    val scheduleType: PaymentScheduleType,
    val status: PaymentScheduleStatus
)
