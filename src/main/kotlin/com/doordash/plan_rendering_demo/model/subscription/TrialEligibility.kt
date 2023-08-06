package com.doordash.plan_rendering_demo.model.subscription

import kotlinx.serialization.Serializable

@Serializable
enum class TrialEligibility(val isEligible: Boolean) {
    IS_ELIGIBLE(true),
    IS_RESURRECT_ELIGIBLE(true),
    IS_NOT_ELIGIBLE(false),
    UNKNOWN_ELIGIBILITY(false),
    ;

    companion object {
        fun from(type: String) = entries.firstOrNull { it.toString() == type } ?: UNKNOWN_ELIGIBILITY

        fun notEligible(): Pair<TrialEligibility, List<SubscriptionPlanTrial>> {
            return IS_NOT_ELIGIBLE to emptyList()
        }
    }
}