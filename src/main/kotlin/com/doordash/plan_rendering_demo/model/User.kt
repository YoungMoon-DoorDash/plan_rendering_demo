package com.doordash.plan_rendering_demo.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import kotlin.math.exp

@Entity
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    val name: String,
    val experiments: String = ""
) {
    fun toMap(): Map<String, String> =
        experiments.split(",").associate {
            val pairs = it.split(":")
            if (pairs.size == 2) {
                pairs[0] to pairs[1]
            } else {
                pairs[0] to "none"
            }
        }
}