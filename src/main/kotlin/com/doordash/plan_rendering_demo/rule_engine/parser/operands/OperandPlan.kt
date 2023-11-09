package com.doordash.plan_rendering_demo.rule_engine.parser.operands

import com.doordash.plan_rendering_demo.rule_engine.model.RuleEngineContext
import com.doordash.plan_rendering_demo.rule_engine.parser.ExpressionException

class OperandPlan(
    private val context: RuleEngineContext,
): Operand {
    override fun getType(): OperandType = OperandType.OBJECT

    override fun haveObject(): Boolean = context.plan != null

    override fun getProperty(propertyName: String): ValueContainer {
        if (context.plan == null) {
            throw ExpressionException("Plan is not defined.")
        }

        return when (propertyName) {
            "id" -> ValueContainer(ValueType.NUMBER, number = context.plan.id)
            "name" -> ValueContainer(ValueType.STRING, value = context.plan.name)
            "type" -> ValueContainer(ValueType.STRING, value = context.plan.planType.value)
            "is_employee_only" -> ValueContainer(ValueType.BOOLEAN, flag = context.plan.isEmployeeOnly)
            "is_trial" -> ValueContainer(ValueType.BOOLEAN, flag = context.plan.isTrial)
            "signup_email_campaign_id" -> ValueContainer(ValueType.NUMBER, number = context.plan.signUpEmailCampaignId)
            "can_be_paused" -> ValueContainer(ValueType.BOOLEAN, flag = context.plan.canBePaused)
            "priority" -> ValueContainer(ValueType.NUMBER, number = context.plan.priority)
            else -> throw ExpressionException("Unsupported interface getProperty() is called.")
        }
    }

}