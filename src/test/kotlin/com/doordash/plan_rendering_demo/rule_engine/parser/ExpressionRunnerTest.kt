package com.doordash.plan_rendering_demo.rule_engine.parser

import com.doordash.plan_rendering_demo.rule_engine.model.Expression
import com.doordash.plan_rendering_demo.rule_engine.model.Plan
import com.doordash.plan_rendering_demo.rule_engine.model.PlanType
import com.doordash.plan_rendering_demo.rule_engine.model.RuleEngineContext
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ExpressionRunnerTest {
    @Test
    fun `ExpressionRunner - plan eligibility happy path`() {
        val expression = Expression(
            id = 1,
            name = "plan_eligibility_1",
            expression = "country in (US, USA, CA, CAN) and\n" +
                "        not plan.is_employee_only and\n" +
                "        plan.type in (standard_plan, annual_plan) and\n" +
                "        have plan.trial"
        )
        val postFix = ExpressionParser().convertToInfix(expression.expression)

        // all conditions matched
        assertTrue(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(
                        id = 1,
                        planType = PlanType.STANDARD_PLAN,
                        isEmployeeOnly = false
                    ),
                    subMarketId = 10L,
                    country = "US",
                    overrideConfig = mapOf("override_plan_trial" to "true")
                )
            ).run(postFix)
        )

        // employee only plan is not matched
        assertFalse(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(
                        id = 1,
                        planType = PlanType.STANDARD_PLAN,
                        isEmployeeOnly = true
                    ),
                    subMarketId = 10L,
                    country = "US",
                    overrideConfig = mapOf("override_plan_trial" to "true")
                )
            ).run(postFix)
        )

        // partner plan is not matched
        assertFalse(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(
                        id = 1,
                        planType = PlanType.PARTNER_PLAN,
                        isEmployeeOnly = false
                    ),
                    subMarketId = 10L,
                    country = "US",
                    overrideConfig = mapOf("override_plan_trial" to "true")
                )
            ).run(postFix)
        )

        // country is not matched
        assertFalse(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(
                        id = 1,
                        planType = PlanType.STANDARD_PLAN,
                        isEmployeeOnly = true
                    ),
                    subMarketId = 10L,
                    country = "AUS",
                    overrideConfig = mapOf("override_plan_trial" to "true")
                )
            ).run(postFix)
        )
    }

    @Test
    fun `ExpressionRunner - plan eligibility happy path - case 2`() {
        val expression = Expression(
            id = 1,
            name = "plan_eligibility_1",
            expression = "{country in (US, USA, CA, CAN) and {is plan.is_employee_only}} or\n" +
                "        {plan.type in (standard_plan, annual_plan) and have plan.trial}\n"
        )
        val postFix = ExpressionParser().convertToInfix(expression.expression)

        // all conditions matched
        assertTrue(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(
                        id = 1,
                        planType = PlanType.STANDARD_PLAN,
                        isEmployeeOnly = true
                    ),
                    subMarketId = 10L,
                    country = "US",
                    overrideConfig = mapOf("override_plan_trial" to "true")
                )
            ).run(postFix)
        )

        // employee only plan is not matched
        assertTrue(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(
                        id = 1,
                        planType = PlanType.STANDARD_PLAN,
                        isEmployeeOnly = false
                    ),
                    subMarketId = 10L,
                    country = "US",
                    overrideConfig = mapOf("override_plan_trial" to "true")
                )
            ).run(postFix)
        )

        // employee only pland and partner plan are not matched
        assertFalse(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(
                        id = 1,
                        planType = PlanType.PARTNER_PLAN,
                        isEmployeeOnly = false
                    ),
                    subMarketId = 10L,
                    country = "US",
                    overrideConfig = mapOf("override_plan_trial" to "true")
                )
            ).run(postFix)
        )
    }

    @Test
    fun `ExpressionRunner - plan eligibility happy path - case 3`() {
        val expression = Expression(
            id = 1,
            name = "plan_eligibility_1",
            expression = "sub_market in (1, 2, 10, 80) and\n" +
                "        plan.name in (DASHPASS_STUDENT_PLAN, DASHPASS_CORPORATE_PLAN)\n"
        )
        val postFix = ExpressionParser().convertToInfix(expression.expression)

        // all conditions matched
        assertTrue(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(
                        id = 1,
                        name = "DASHPASS_CORPORATE_PLAN",
                        planType = PlanType.CORP_PLAN
                    ),
                    subMarketId = 10L,
                    country = "US"
                )
            ).run(postFix)
        )

        // submarket is not matched
        assertFalse(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(
                        id = 1,
                        name = "DASHPASS_CORPORATE_PLAN",
                        planType = PlanType.CORP_PLAN
                    ),
                    subMarketId = 200L,
                    country = "US"
                )
            ).run(postFix)
        )

        // plan name is not matched
        assertFalse(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(
                        id = 1,
                        name = "DASHPASS_STANDARD_PLAN",
                        planType = PlanType.STANDARD_PLAN
                    ),
                    subMarketId = 10L,
                    country = "US"
                )
            ).run(postFix)
        )
    }

    @Test
    fun `ExpressionRunner - plan eligibility happy path - case 4`() {
        val expression = Expression(
            id = 1,
            name = "plan_eligibility_1",
            expression = "{country in (US, USA, CA, CAN) and {not plan.is_employee_only}} or\n" +
                "        {plan.type in (standard_plan, annual_plan) and is trial}\n"
        )
        val postFix = ExpressionParser().convertToInfix(expression.expression)

        // all conditions matched
        assertTrue(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(
                        id = 1,
                        name = "DASHPASS_CORPORATE_PLAN",
                        planType = PlanType.STANDARD_PLAN,
                        isEmployeeOnly = false,
                        isTrial = true
                    ),
                    subMarketId = 10L,
                    country = "US"
                )
            ).run(postFix)
        )

        // employee only is not matched
        assertTrue(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(
                        id = 1,
                        planType = PlanType.STANDARD_PLAN,
                        isEmployeeOnly = true,
                        isTrial = true
                    ),
                    subMarketId = 10L,
                    country = "US"
                )
            ).run(postFix)
        )

        // trail is not matched
        assertTrue(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(
                        id = 1,
                        planType = PlanType.STANDARD_PLAN,
                        isEmployeeOnly = false,
                        isTrial = false
                    ),
                    subMarketId = 10L,
                    country = "US"
                )
            ).run(postFix)
        )

        // plan type is not matched
        assertTrue(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(
                        id = 1,
                        planType = PlanType.CORP_PLAN,
                        isEmployeeOnly = false,
                        isTrial = true
                    ),
                    subMarketId = 10L,
                    country = "US"
                )
            ).run(postFix)
        )

        // employee only and trial are not matched.
        assertFalse(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(
                        id = 1,
                        planType = PlanType.STANDARD_PLAN,
                        isEmployeeOnly = true,
                        isTrial = false
                    ),
                    subMarketId = 10L,
                    country = "US"
                )
            ).run(postFix)
        )
    }

    @Test
    fun `ExpressionRunner - benefit eligibility happy path`() {
        val expression = Expression(
            id = 1,
            name = "plan_eligibility_1",
            expression = "{treatment new_trial_upsell_messaging_experiment in (treatment1, treatment2)} and\n" +
                "        {min_subtotal >= 3599} and\n" +
                "        {not have membership_sharing}"
        )
        val postFix = ExpressionParser().convertToInfix(expression.expression)

        assertTrue(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(id = 1),
                    treatments = mapOf("new_trial_upsell_messaging_experiment" to "treatment1"),
                    overrideConfig = mapOf(
                        "override_min_subtotal" to "3600",
                        "override_membership_sharing" to "false"
                    )
                )
            ).run(postFix)
        )

        // not in treatment
        assertFalse(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(id = 1),
                    overrideConfig = mapOf(
                        "override_min_subtotal" to "3600",
                        "override_membership_sharing" to "false"
                    )
                )
            ).run(postFix)
        )

        // minimum subtotal is not matched
        assertFalse(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(id = 1),
                    treatments = mapOf("new_trial_upsell_messaging_experiment" to "treatment1"),
                    overrideConfig = mapOf(
                        "override_min_subtotal" to "1500",
                        "override_membership_sharing" to "false"
                    )
                )
            ).run(postFix)
        )

        // membership sharing is not matched
        assertFalse(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(id = 1),
                    treatments = mapOf("new_trial_upsell_messaging_experiment" to "treatment1"),
                    overrideConfig = mapOf(
                        "override_min_subtotal" to "3600",
                        "override_membership_sharing" to "true"
                    )
                )
            ).run(postFix)
        )
    }

    @Test
    fun `ExpressionRunner - schedule eligibility happy path`() {
        val expression = Expression(
            id = 1,
            name = "plan_eligibility_1",
            expression = "{schedule.type in (monthly, yearly)} and\n" +
                "        {schedule.payment_method in (CreditCard, ApplePay, GooglePay)} and\n" +
                "        {have subscription or have transition to partner_plan}"
        )
        val postFix = ExpressionParser().convertToInfix(expression.expression)

        assertTrue(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(id = 1),
                    overrideConfig = mapOf(
                        "override_schedule_type" to "monthly",
                        "override_schedule_payment_method" to "applepay",
                        "override_subscription" to "true",
                        "override_transition_to" to "partner_plan"
                    )
                )
            ).run(postFix)
        )

        // schedule type is not matched
        assertFalse(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(id = 1),
                    overrideConfig = mapOf(
                        "override_schedule_type" to "weekly",
                        "override_schedule_payment_method" to "applepay",
                        "override_subscription" to "true",
                        "override_transition_to" to "partner_plan"
                    )
                )
            ).run(postFix)
        )

        // payment method is not matched
        assertFalse(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(id = 1),
                    overrideConfig = mapOf(
                        "override_schedule_type" to "monthly",
                        "override_schedule_payment_method" to "visa",
                        "override_subscription" to "true",
                        "override_transition_to" to "partner_plan"
                    )
                )
            ).run(postFix)
        )

        // only subscription is not matched
        assertTrue(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(id = 1),
                    overrideConfig = mapOf(
                        "override_schedule_type" to "monthly",
                        "override_schedule_payment_method" to "applepay",
                        "override_transition_to" to "partner_plan"
                    )
                )
            ).run(postFix)
        )


        // only transition is not matched
        assertTrue(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(id = 1),
                    overrideConfig = mapOf(
                        "override_schedule_type" to "monthly",
                        "override_schedule_payment_method" to "applepay",
                        "override_subscription" to "true",
                        "override_transition_to" to "corp_plan"
                    )
                )
            ).run(postFix)
        )

        // both subscription and transition are not matched
        assertFalse(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(id = 1),
                    overrideConfig = mapOf(
                        "override_schedule_type" to "monthly",
                        "override_schedule_payment_method" to "applepay"
                    )
                )
            ).run(postFix)
        )
    }
}