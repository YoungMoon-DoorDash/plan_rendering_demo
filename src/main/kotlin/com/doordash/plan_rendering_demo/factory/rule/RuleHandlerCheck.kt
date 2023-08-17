package com.doordash.plan_rendering_demo.factory.rule

import com.doordash.plan_rendering_demo.factory.RuleEngine
import com.doordash.plan_rendering_demo.factory.RuleHandler
import com.doordash.plan_rendering_demo.model.Rule
import kotlinx.serialization.json.Json

class RuleHandlerCheck: RuleHandler {
    companion object {
        fun isObject(key: String): Boolean = key[0] == RuleConstants.OBJECT_PREFIX

        fun validate(value: Map<String, String>): Pair<Boolean, String> {
            if (value.isEmpty()) {
                return false to "Rule content is empty"
            }

            value.forEach { (key, _) ->
                if (isObject(key)) {
                    val pairs = key.substring(1).split(RuleConstants.OBJECT_NAME_FIELD_SEPARATOR)
                    if (pairs.size > 2) {
                        return false to "Object naming $key is invalid"
                    }
                }
            }

            return true to "valid"
        }
    }


    override fun execute(rule: Rule, sb: StringBuilder?): String {
        val conditions = Json.decodeFromString<Map<String, String>>(rule.run)
        conditions.forEach { (key, value) ->
            if (isObject(key)) {
                if (!isObjectMatched(key.lowercase(), value)) {
                    sb?.append("\n    >> isObjectMatched FAILED: rule=$rule, key=$key, value=$value")
                    return "false"
                }
                sb?.append("\n    isObjectMatched PASSED: rule=$rule, key=$key, value=$value => true")
            } else if (!isKeyValueMatched(key.lowercase(), value)) {
                sb?.append("\n    >> isKeyValueMatched FAILED: rule=$rule, key=$key, value=$value")
                return "false"
            } else {
                sb?.append("\n    isKeyValueMatched PASSED: rule=$rule, key=$key, value=$value")
            }
        }
        return "true"
    }

    private fun isObjectMatched(key: String, value: String): Boolean {
        val pairs = key.substring(1).split(RuleConstants.OBJECT_NAME_FIELD_SEPARATOR)
        return when (pairs.size) {
            1 -> {
                // check object existence
                val objectExist = RuleEngine.hasContext(pairs[0])
                if (value.lowercase() == "null") {
                    !objectExist
                } else {
                    objectExist
                }
            }

            2 -> {
                // name-field fairs.
                RuleEngine.fromContext(pairs[0])?.let { contextValue ->
                    val contextObject = Json.decodeFromString<Map<String, String>>(contextValue)
                    contextObject[pairs[1]]?.let {
                        value.lowercase() == it.lowercase()
                    }
                } ?: false
            }

            else ->
                false
        }
    }

    private fun isKeyValueMatched(key: String, value: String): Boolean =
        RuleEngine.fromContext(key)?.let { target ->
            if (value.startsWith("(") && value.endsWith(")")) {
                value.substring(1, value.length - 1)
                    .split(RuleConstants.OBJECT_VALUE_SEPARATOR)
                    .any {
                        it.lowercase() == target.lowercase()
                    }
            } else {
                target.lowercase() == value.lowercase()
            }
        } ?: false
}