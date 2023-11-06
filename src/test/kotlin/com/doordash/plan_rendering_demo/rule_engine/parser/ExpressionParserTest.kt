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
                "        plan.is_employee_only is false and\n" +
                "        plan.type in (standard_plan, annual_plan) and\n" +
                "        have plan.signup_email_campaign_id"
        )
        val expected = "[country, (ca,can,us,usa), in, (is_employee_only), plan, (false), is, (type), plan, (annual_plan,standard_plan), in, " +
            "(signup_email_campaign_id), plan, have, and, and, and]"

        val postfix = ExpressionParser().convertToInfix(expression.expression)
        assertEquals(expected, postfix.toString())
    }

    @Test
    fun `ParseExpression - plan eligibility happy path case 2`() {
        val expression = Expression(
            id = 1,
            name = "plan_eligibility_1",
            expression = "{country in (US, USA, CA, CAN) and {plan.is_employee_only is false}} or\n" +
                "        {plan.type in (standard_plan, annual_plan) and have plan.signup_email_campaign_id}\n"
        )
        val expected = "[country, (ca,can,us,usa), in, (is_employee_only), plan, (false), is, and, (type), plan, (annual_plan,standard_plan), in, " +
            "(signup_email_campaign_id), plan, have, and, or]"

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
        val expected = "[sub_market, (1,10,2,80), in, (name), plan, (dashpass_corporate_plan,dashpass_student_plan), in, and]"

        val postfix = ExpressionParser().convertToInfix(expression.expression)
        assertEquals(expected, postfix.toString())
    }

    @Test
    fun `ParseExpression - plan eligibility happy path case 4`() {
        val expression = Expression(
            id = 1,
            name = "plan_eligibility_1",
            expression = "{country in (US, USA, CA, CAN) and {plan.is_employee_only is true}} or\n" +
                "        {plan.type in (standard_plan, annual_plan) and have trial}\n"
        )
        val expected = "[country, (ca,can,us,usa), in, (is_employee_only), plan, (true), is, and, (type), plan, (annual_plan,standard_plan), in, " +
            "trial, have, and, or]"

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
        val expected = "[(new_trial_upsell_messaging_experiment), treatment, (control,treatment1), in, min_subtotal, (3599), >=, " +
            "membership_sharing, have, not, and, and]"

        val postfix = ExpressionParser().convertToInfix(expression.expression)
        assertEquals(expected, postfix.toString())
    }

    @Test
    fun `ParseExpression - schedule eligibility happy path`() {
        val expression = Expression(
            id = 1,
            name = "plan_eligibility_1",
            expression = "{payment_schedule.type in (monthly, yearly)} and\n" +
                "        {payment_schedule.payment_method in (CreditCard, ApplePay, GooglePay)} and\n" +
                "        {have subscription or {have transition and transition.type is partner_plan}}"
        )
        val expected = "[(type), payment_schedule, (monthly,yearly), in, (payment_method), payment_schedule, (applepay,creditcard,googlepay), in, " +
            "subscription, have, transition, have, (type), transition, (partner_plan), is, and, or, and, and]"

        val postfix = ExpressionParser().convertToInfix(expression.expression)
        assertEquals(expected, postfix.toString())
    }
}