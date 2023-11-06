package com.doordash.plan_rendering_demo.rule_engine.parser.operands

import com.doordash.plan_rendering_demo.rule_engine.model.RuleEngineContext

class OperandMembershipSharing(
    private val context: RuleEngineContext
): Operand {
    override fun isType(type: OperandType): Boolean = type == OperandType.BOOLEAN

    override fun supportHave(): Boolean = true

    override fun isExist(): Boolean =
        context.overrideConfig["override_membership_sharing"]?.toBoolean() ?: false

}