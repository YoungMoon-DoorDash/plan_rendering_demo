package com.doordash.plan_rendering_demo.factory

import com.doordash.plan_rendering_demo.factory.rule.CommandRuleFactory
import com.doordash.plan_rendering_demo.factory.rule.RuleHandlerCheck
import com.doordash.plan_rendering_demo.model.Rule
import com.doordash.plan_rendering_demo.model.RuleType
import com.doordash.plan_rendering_demo.repository.SubscriptionPlanRepository
import com.doordash.plan_rendering_demo.repository.UserRepository
import kotlinx.serialization.json.Json

interface RuleHandler {
    fun execute(rule: Rule, params: Map<String, String>): String
}

object RuleEngine {
    private var _planRepository: SubscriptionPlanRepository? = null
    private var _userRepository: UserRepository? = null

    fun setRepository(
        planRepository: SubscriptionPlanRepository,
        userRepository: UserRepository
    ) {
        _planRepository = planRepository
        _userRepository = userRepository
    }

    fun getPlanRepository(): SubscriptionPlanRepository {
        checkNotNull(_planRepository) { "SubscriptionPlan repository object is not initialized yet" }
        return _planRepository as SubscriptionPlanRepository
    }

    fun getUserRepository(): UserRepository {
        checkNotNull(_userRepository) { "User repository object is not initialized yet" }
        return _userRepository as UserRepository
    }

    fun execute(rule: Rule, parameters: Map<String, String>): String = when (rule.type) {
        RuleType.CHECK -> RuleHandlerCheck().execute(rule, parameters)
        RuleType.COMMAND -> {
            val commandName = Json.decodeFromString<Map<String, String>>(rule.run)["command"] ?: "null"
            CommandRuleFactory.getCommand(commandName)?.execute(rule, parameters)
                ?: "Command not found: $commandName"
        }
    }

    fun convertRuleRun(run: String): Map<String, String> {
        val parameters = Json.decodeFromString<Map<String, String>>(run)
        return parameters.mapKeys { it.key.lowercase() }
    }

    fun validate(ruleType: RuleType, run: Map<String, String>) {
        val isValid = when (ruleType) {
            RuleType.CHECK -> run.isNotEmpty()
            RuleType.COMMAND -> run.isNotEmpty() && CommandRuleFactory.getCommand(run["command"]) != null
        }
        check(isValid) { "Rule content is not valid: $run" }
    }
}