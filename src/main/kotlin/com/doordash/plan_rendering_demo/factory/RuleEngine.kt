package com.doordash.plan_rendering_demo.factory

import com.doordash.plan_rendering_demo.factory.rule.CommandRuleFactory
import com.doordash.plan_rendering_demo.factory.rule.RuleConstants
import com.doordash.plan_rendering_demo.factory.rule.RuleHandlerCheck
import com.doordash.plan_rendering_demo.model.Rule
import com.doordash.plan_rendering_demo.model.RuleType
import com.doordash.plan_rendering_demo.model.Screen
import com.doordash.plan_rendering_demo.model.subscription.SubscriptionPlan
import com.doordash.plan_rendering_demo.repository.ScreenRepository
import com.doordash.plan_rendering_demo.repository.SubscriptionPlanRepository
import com.doordash.plan_rendering_demo.repository.TextRepository
import com.doordash.plan_rendering_demo.repository.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import java.awt.SystemColor.text

interface RuleHandler {
    fun execute(rule: Rule, sb: StringBuilder? = null): String
}

object RuleEngine {
    private val _contextMap = HashMap<String, String>()
    private var _planRepository: SubscriptionPlanRepository? = null
    private var _userRepository: UserRepository? = null
    private var _textRepository: TextRepository? = null
    private var _screenRepository: ScreenRepository? = null

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
        userRepository: UserRepository,
        textRepository: TextRepository,
        screenRepository: ScreenRepository
    ) {
        _planRepository = planRepository
        _userRepository = userRepository
        _textRepository = textRepository
        _screenRepository = screenRepository
    }

    fun getPlanRepository(): SubscriptionPlanRepository {
        checkNotNull(_planRepository) { "SubscriptionPlan repository object is not initialized yet" }
        return _planRepository as SubscriptionPlanRepository
    }

    fun getUserRepository(): UserRepository {
        checkNotNull(_userRepository) { "User repository object is not initialized yet" }
        return _userRepository as UserRepository
    }

    fun getTextRepository(): TextRepository {
        checkNotNull(_textRepository) { "Text repository object is not initialized yet" }
        return _textRepository as TextRepository
    }

    fun getScreenRepository(): ScreenRepository {
        checkNotNull(_screenRepository) { "Screen repository object is not initialized yet" }
        return _screenRepository as ScreenRepository
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

    fun populateContext(parameters: String) {
        _contextMap.clear()
        Json.decodeFromString<Map<String, String>>(parameters).forEach { (key, value) ->
            val keyLower = key.lowercase()
            toContext(keyLower, value)
            when (keyLower) {
                "consumer_id" -> populateUserConfiguration(value.toLong())
            }
        }
    }

    private fun populateUserConfiguration(consumerId: Long) {
        val findUser = _userRepository?.findById(consumerId)
        if (findUser?.isPresent == true) {
            val user = findUser.get()
            user.experiments.split(RuleConstants.OBJECT_VALUE_SEPARATOR).forEach {
                val values = it.split(RuleConstants.NAME_VALUE_SEPARATOR)
                if (values.size == 2) {
                    val key = values[0].lowercase()
                    toContext(key, values[1])
                    if (key == "subscription_plan_id") {
                        populateActiveSubscriptionPlan(values[1].toInt())
                    }
                }
            }
        }
    }

    private fun populateActiveSubscriptionPlan(planId: Int) {
        val findPlan = _planRepository?.findById(planId)
        if (findPlan?.isPresent == true) {
            val plan = findPlan.get()
            toContext("active_subscription_plan", Json.encodeToString(SubscriptionPlan.serializer(), plan))
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

    fun rendering(screen: Screen): String {
        val buffer = StringBuilder("<center><div style='text-align:left;margin:10pt;padding:3pt;width:640px;border:1pt solid gray;'>")
        ScreenElementFactory.toScreen(screen.elements).forEach {
            buffer.append(it.render())
        }
        return buffer.append("</div></center>").toString()
    }

    fun getText(name: String): String =
        (getTextRepository().findText(name)?.value ?: name).let {
            _contextMap.entries.fold(it) { acc, (key, value) -> acc.replace("{$key}", value) }
        }
}