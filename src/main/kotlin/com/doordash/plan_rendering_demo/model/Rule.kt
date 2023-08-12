package com.doordash.plan_rendering_demo.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import kotlinx.serialization.Serializable
import java.security.InvalidParameterException

enum class RuleType {
    CHECK,
    COMMAND
}

fun Int.toRuleType(): RuleType = when(this) {
    0 -> RuleType.CHECK
    1 -> RuleType.COMMAND
    else -> throw InvalidParameterException("Unknown rule type value: $this")
}

fun String.toRuleType(): RuleType = when(this.lowercase()) {
    "check" -> RuleType.CHECK
    "command" -> RuleType.COMMAND
    else -> throw InvalidParameterException("Unknown rule type name: $this")
}

@Entity
@Serializable
data class Rule(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    val name: String,
    val type: RuleType,
    val run: String
)