package com.doordash.plan_rendering_demo.rule_engine.parser

import com.doordash.plan_rendering_demo.rule_engine.model.Expression
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ExpressionParserTest {
    @Test
    fun `ParseExpression - plan eligibility happy path`() {
        val expression = Expression(
            id = 1,
            name = "plan_eligibility_1",
            expression = "country in (US, USA, CA, CAN) and\n" +
                "        is not plan.is_employee_only and\n" +
                "        plan.type in (standard_plan, annual_plan) and\n" +
                "        have plan.trial"
        )
        val expected = "[country, (CA,CAN,US,USA), in, plan.is_employee_only, not, is, plan.type, (annual_plan,standard_plan), in, plan.trial, have, and, and, and]"

        val postfix = ExpressionParser().convertToInfix(expression.expression)
        assertEquals(expected, postfix.toString())
    }

    @Test
    fun `ParseExpression - plan eligibility happy path case 2`() {
        val expression = Expression(
            id = 1,
            name = "plan_eligibility_1",
            expression = "{country in (US, USA, CA, CAN) and {is not plan.is_employee_only}} or\n" +
                "        {plan.type in (standard_plan, annual_plan) and have plan.trial}\n"
        )
        val expected = "[country, (CA,CAN,US,USA), in, plan.is_employee_only, not, is, and, plan.type, (annual_plan,standard_plan), in, plan.trial, have, and, or]"

        val postfix = ExpressionParser().convertToInfix(expression.expression)
        assertEquals(expected, postfix.toString())
    }

    @Test
    fun `ParseExpression - benefit eligibility happy path`() {
        val expression = Expression(
            id = 1,
            name = "plan_eligibility_1",
            expression = "{treatment new_trial_upsell_messaging_experiment in (control, treatment1)} and\n" +
                "        {min_subtotal >= 35.99} and\n" +
                "        {not have membership_sharing}"
        )
        val expected = "[(new_trial_upsell_messaging_experiment), treatment, (control,treatment1), in, min_subtotal, (35.99), >=, membership_sharing, have, not, and, and]"

        val postfix = ExpressionParser().convertToInfix(expression.expression)
        assertEquals(expected, postfix.toString())
    }

    @Test
    fun `ParseExpression - schedule eligibility happy path`() {
        val expression = Expression(
            id = 1,
            name = "plan_eligibility_1",
            expression = "{schedule.type in (monthly, yearly)} and\n" +
                "        {schedule.payment_method in (CreditCard, ApplePay, GooglePay)} and\n" +
                "        {have subscription or transition to partner_plan}"
        )
        val expected = "[schedule.type, (monthly,yearly), in, schedule.payment_method, (ApplePay,CreditCard,GooglePay), in, subscription, have, transition, (partner_plan), to, or, and, and]"

        val postfix = ExpressionParser().convertToInfix(expression.expression)
        assertEquals(expected, postfix.toString())
    }
}