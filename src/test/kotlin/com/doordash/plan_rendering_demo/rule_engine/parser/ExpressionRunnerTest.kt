package com.doordash.plan_rendering_demo.rule_engine.parser

import com.doordash.plan_rendering_demo.rule_engine.model.Expression
import com.doordash.plan_rendering_demo.rule_engine.model.Plan
import com.doordash.plan_rendering_demo.rule_engine.model.PlanType
import com.doordash.plan_rendering_demo.rule_engine.model.RuleEngineContext
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ExpressionRunnerTest {
    @Test
    fun `ExpressionRunner - plan eligibility happy path`() {
        val expression = Expression(
            id = 1,
            name = "plan_eligibility_1",
            expression = "country in (US, USA, CA, CAN) and\n" +
                "        plan.is_employee_only is false and\n" +
                "        plan.type in (standard_plan, annual_plan) and\n" +
                "        have plan.signup_email_campaign_id"
        )
        val postFix = ExpressionParser().convertToInfix(expression.expression)

        // all conditions matched
        assertTrue(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(id = 1),
                    subMarketId = 10L,
                    country = "US"
                )
            ).run(postFix)
        )

        // employee only plan is not matched
        assertFalse(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(id = 1, isEmployeeOnly = true),
                    subMarketId = 10L,
                    country = "US"
                )
            ).run(postFix)
        )

        // partner plan is not matched
        assertFalse(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(id = 1, planType = PlanType.PLAN_TYPE_PARTNER_PLAN),
                    subMarketId = 10L,
                    country = "US"
                )
            ).run(postFix)
        )

        // country is not matched
        assertFalse(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(id = 1),
                    subMarketId = 10L,
                    country = "AU"
                )
            ).run(postFix)
        )

        // PLAN not exists
        assertThrows<ExpressionException> {
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = null,
                    subMarketId = 10L,
                    country = "AU"
                )
            ).run(postFix)
        }
    }

    @Test
    fun `ExpressionRunner - plan eligibility happy path - case 2`() {
        val expression = Expression(
            id = 1,
            name = "plan_eligibility_1",
            expression = "{country in (US, USA, CA, CAN) and {plan.is_employee_only is false}} or\n" +
                "        {plan.type in (standard_plan, annual_plan) and have plan.signup_email_campaign_id}\n"
        )
        val postFix = ExpressionParser().convertToInfix(expression.expression)

        // all conditions matched
        assertTrue(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(id = 1),
                    subMarketId = 10L,
                    country = "US"
                )
            ).run(postFix)
        )

        // employee only plan is not matched
        assertTrue(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(id = 1, isEmployeeOnly = true),
                    subMarketId = 10L,
                    country = "US"
                )
            ).run(postFix)
        )

        // partner plan is not matched
        assertTrue(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(id = 1, planType = PlanType.PLAN_TYPE_PARTNER_PLAN),
                    subMarketId = 10L,
                    country = "US"
                )
            ).run(postFix)
        )

        // employee only plan and partner plan are not matched
        assertFalse(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(id = 1, isEmployeeOnly = true, planType = PlanType.PLAN_TYPE_PARTNER_PLAN),
                    subMarketId = 10L,
                    country = "US"
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
                    plan = Plan(id = 1, name="DASHPASS_CORPORATE_PLAN"),
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
                    plan = Plan(id = 1, name="DASHPASS_CORPORATE_PLAN"),
                    subMarketId = 1000L,
                    country = "US"
                )
            ).run(postFix)
        )

        // plan name is not matched
        assertFalse(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(id = 1, name="DASHPASS_STANDARD_MONTHLY_PLAN"),
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
            expression = "{country in (US, USA, CA, CAN) and {plan.is_employee_only is true}} or\n" +
                "        {plan.type in (standard_plan, annual_plan) and have trial}\n"
        )
        val postFix = ExpressionParser().convertToInfix(expression.expression)

        // all conditions matched
        assertTrue(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(id = 1, isEmployeeOnly = true),
                    subMarketId = 10L,
                    country = "US",
                    overrideConfig = mapOf("override_have_trial" to "true")
                )
            ).run(postFix)
        )

        // employee only is not matched
        assertTrue(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(id = 1, isEmployeeOnly = false),
                    subMarketId = 10L,
                    country = "US",
                    overrideConfig = mapOf("override_have_trial" to "true")
                )
            ).run(postFix)
        )

        // trial is not exists
        assertTrue(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(id = 1, isEmployeeOnly = true),
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
                    plan = Plan(id = 1, isEmployeeOnly = true, planType = PlanType.PLAN_TYPE_PARTNER_PLAN),
                    subMarketId = 10L,
                    country = "US",
                    overrideConfig = mapOf("override_have_trial" to "true")
                )
            ).run(postFix)
        )

        // employee only and trial are not matched.
        assertFalse(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(id = 1, isEmployeeOnly = false),
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
                    plan = Plan(id = 1, isEmployeeOnly = false),
                    subMarketId = 10L,
                    country = "US",
                    treatments = mapOf("new_trial_upsell_messaging_experiment" to "treatment1"),
                    overrideConfig = mapOf(
                        "override_min_subtotal" to "6000",
                        "override_have_membership_sharing" to "false"
                    )
                )
            ).run(postFix)
        )

        // not in treatment
        assertFalse(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(id = 1, isEmployeeOnly = false),
                    subMarketId = 10L,
                    country = "US",
                    treatments = mapOf("new_trial_upsell_messaging_experiment" to "control"),
                    overrideConfig = mapOf(
                        "override_min_subtotal" to "6000",
                        "override_have_membership_sharing" to "false"
                    )
                )
            ).run(postFix)
        )

        // minimum subtotal is not matched
        assertFalse(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(id = 1, isEmployeeOnly = false),
                    subMarketId = 10L,
                    country = "US",
                    treatments = mapOf("new_trial_upsell_messaging_experiment" to "treatment1"),
                    overrideConfig = mapOf(
                        "override_min_subtotal" to "2000",
                        "override_have_membership_sharing" to "false"
                    )
                )
            ).run(postFix)
        )

        // membership sharing is not matched
        assertFalse(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(id = 1, isEmployeeOnly = false),
                    subMarketId = 10L,
                    country = "US",
                    treatments = mapOf("new_trial_upsell_messaging_experiment" to "treatment1"),
                    overrideConfig = mapOf(
                        "override_min_subtotal" to "6000",
                        "override_have_membership_sharing" to "true"
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
            expression = "{payment_schedule.type in (monthly, yearly)} and\n" +
                "        {payment_schedule.payment_method in (CreditCard, ApplePay, GooglePay)} and\n" +
                "        {have subscription or {have transition and transition.type is partner_plan}}"
        )
        val postFix = ExpressionParser().convertToInfix(expression.expression)

        assertTrue(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(id = 1),
                    subMarketId = 10L,
                    country = "US",
                    overrideConfig = mapOf(
                        "override_have_payment_schedule" to "true",
                        "override_payment_schedule_type" to "monthly",
                        "override_payment_schedule_payment_method" to "creditcard",
                        "override_have_subscription" to "true",
                        "override_have_transition" to "true",
                        "override_transition_type" to "partner_plan"
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
                    subMarketId = 10L,
                    country = "US",
                    overrideConfig = mapOf(
                        "override_have_payment_schedule" to "true",
                        "override_payment_schedule_type" to "daily",
                        "override_payment_schedule_payment_method" to "creditcard",
                        "override_have_subscription" to "true",
                        "override_have_transition" to "true",
                        "override_transition_type" to "partner_plan"
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
                    subMarketId = 10L,
                    country = "US",
                    overrideConfig = mapOf(
                        "override_have_payment_schedule" to "true",
                        "override_payment_schedule_type" to "monthly",
                        "override_have_subscription" to "true",
                        "override_have_transition" to "true",
                        "override_transition_type" to "partner_plan"
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
                    subMarketId = 10L,
                    country = "US",
                    overrideConfig = mapOf(
                        "override_have_payment_schedule" to "true",
                        "override_payment_schedule_type" to "monthly",
                        "override_payment_schedule_payment_method" to "creditcard",
                        "override_have_transition" to "true",
                        "override_transition_type" to "partner_plan"
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
                    subMarketId = 10L,
                    country = "US",
                    overrideConfig = mapOf(
                        "override_have_payment_schedule" to "true",
                        "override_payment_schedule_type" to "monthly",
                        "override_payment_schedule_payment_method" to "creditcard",
                        "override_have_subscription" to "true",
                        "override_have_transition" to "true",
                        "override_transition_type" to "standard_plan"
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
                    subMarketId = 10L,
                    country = "US",
                    overrideConfig = mapOf(
                        "override_have_payment_schedule" to "true",
                        "override_payment_schedule_type" to "monthly",
                        "override_payment_schedule_payment_method" to "creditcard",
                        "override_have_transition" to "true",
                        "override_transition_type" to "standard_plan"
                    )
                )
            ).run(postFix)
        )

        assertFalse(
            ExpressionRunner(
                RuleEngineContext(
                    consumerId = 10,
                    plan = Plan(id = 1),
                    subMarketId = 10L,
                    country = "US",
                    overrideConfig = mapOf(
                        "override_have_payment_schedule" to "true",
                        "override_payment_schedule_type" to "monthly",
                        "override_payment_schedule_payment_method" to "creditcard",
                        "override_transition_type" to "standard_plan"
                    )
                )
            ).run(postFix)
        )
    }
}