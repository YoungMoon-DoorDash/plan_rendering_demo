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
                "        not plan.is_employee_only and\n" +
                "        plan.type in (standard_plan, annual_plan) and\n" +
                "        have plan.trial"
        )
        val expected = "[country, (ca,can,us,usa), in, plan.is_employee_only, not, plan.type, (annual_plan,standard_plan), in, plan.trial, have, and, and, and]"

        val postfix = ExpressionParser().convertToInfix(expression.expression)
        assertEquals(expected, postfix.toString())
    }

    @Test
    fun `ParseExpression - plan eligibility happy path case 2`() {
        val expression = Expression(
            id = 1,
            name = "plan_eligibility_1",
            expression = "{country in (US, USA, CA, CAN) and {is plan.is_employee_only}} or\n" +
                "        {plan.type in (standard_plan, annual_plan) and have plan.trial}\n"
        )
        val expected = "[country, (ca,can,us,usa), in, plan.is_employee_only, is, and, plan.type, (annual_plan,standard_plan), in, plan.trial, have, and, or]"

        val postfix = ExpressionParser().convertToInfix(expression.expression)
        assertEquals(expected, postfix.toString())
    }

    @Test
    fun `ParseExpression - plan eligibility happy path case 3`() {
        val expression = Expression(
            id = 1,
            name = "plan_eligibility_1",
            expression = "sub_market in (1, 2, 10, 80) and\n" +
                "        plan.name in (DASHPASS_STUDENT_PLAN, DASHPASS_CORPORATE_PLAN)\n"
        )
        val expected = "[sub_market, (1,10,2,80), in, plan.name, (dashpass_corporate_plan,dashpass_student_plan), in, and]"

        val postfix = ExpressionParser().convertToInfix(expression.expression)
        assertEquals(expected, postfix.toString())
    }

    @Test
    fun `ParseExpression - plan eligibility happy path case 4`() {
        val expression = Expression(
            id = 1,
            name = "plan_eligibility_1",
            expression = "{country in (US, USA, CA, CAN) and {not plan.is_employee_only}} or\n" +
                "        {plan.type in (standard_plan, annual_plan) and is trial}\n"
        )
        val expected = "[country, (ca,can,us,usa), in, plan.is_employee_only, not, and, plan.type, (annual_plan,standard_plan), in, trial, is, and, or]"

        val postfix = ExpressionParser().convertToInfix(expression.expression)
        assertEquals(expected, postfix.toString())
    }

    @Test
    fun `ParseExpression - benefit eligibility happy path`() {
        val expression = Expression(
            id = 1,
            name = "plan_eligibility_1",
            expression = "{treatment new_trial_upsell_messaging_experiment in (control, treatment1)} and\n" +
                "        {min_subtotal >= 3599} and\n" +
                "        {not have membership_sharing}"
        )
        val expected = "[(new_trial_upsell_messaging_experiment), treatment, (control,treatment1), in, min_subtotal, (3599), >=, membership_sharing, have, not, and, and]"

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
        val expected = "[schedule.type, (monthly,yearly), in, schedule.payment_method, (applepay,creditcard,googlepay), in, subscription, have, transition, (partner_plan), to, or, and, and]"

        val postfix = ExpressionParser().convertToInfix(expression.expression)
        assertEquals(expected, postfix.toString())
    }
}