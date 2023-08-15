package com.doordash.plan_rendering_demo.factory

import com.doordash.plan_rendering_demo.factory.rule.CommandRuleFactory
import com.doordash.plan_rendering_demo.factory.rule.RuleHandlerCheck
import com.doordash.plan_rendering_demo.model.Rule
import com.doordash.plan_rendering_demo.model.RuleType
import com.doordash.plan_rendering_demo.repository.SubscriptionPlanRepository
import com.doordash.plan_rendering_demo.repository.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.serialization.json.Json
import org.apache.catalina.mapper.Mapper

interface RuleHandler {
    fun execute(rule: Rule, sb: StringBuilder? = null): String
}

object RuleEngine {
    private val _contextMap = HashMap<String, String>()
    private var _planRepository: SubscriptionPlanRepository? = null
    private var _userRepository: UserRepository? = null

    fun hasContext(key: String):Boolean = _contextMap.containsKey(key.lowercase())

    fun fromContext(key: String): String? = _contextMap[key.lowercase()]

    fun toContext(key: String, value: String) {
        _contextMap[key.lowercase()] = value
    }

    fun getContext(sb: StringBuilder) {
        sb.append("\ncontext:")
        _contextMap.forEach { (key, value) ->
            sb.append("\n    ").append("$key=$value")
        }
    }

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

    fun execute(rule: Rule, sb: StringBuilder? = null): String = when (rule.type) {
        RuleType.CHECK -> RuleHandlerCheck().execute(rule, sb)
        RuleType.COMMAND -> {
            val commandName = Json.decodeFromString<Map<String, String>>(rule.run)["command"] ?: "null"
            val msg = CommandRuleFactory.getCommand(commandName)?.execute(rule, sb)
                ?: run {
                    "Command not found: $commandName"
                }
            sb?.append("\n    Command.execute: rul=$rule, command=$commandName => $msg")
            msg
        }
    }

    fun convertRuleRun(run: String): Map<String, String> {
        val parameters = Json.decodeFromString<Map<String, String>>(run)
        return parameters.mapKeys { it.key.lowercase() }
    }

    fun validate(ruleType: RuleType, run: Map<String, String>) {
        val (isValid, message) = when (ruleType) {
            RuleType.CHECK -> RuleHandlerCheck.validate(run)
            RuleType.COMMAND -> CommandRuleFactory.validate(run)
        }

        check(isValid) { "Rule content is not valid: run: $run, error: $message" }
    }

    fun formatToJson(values: Map<String, String>): String =
        ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(values)
}