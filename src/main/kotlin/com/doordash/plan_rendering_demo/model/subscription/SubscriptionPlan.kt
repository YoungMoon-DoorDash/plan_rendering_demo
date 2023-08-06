package com.doordash.plan_rendering_demo.model.subscription

import com.doordash.plan_rendering_demo.utils.TimestampSerializer
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import kotlinx.serialization.Serializable
import java.sql.Timestamp

@Entity
@Serializable
data class SubscriptionPlan(
    @Id val id: Int,
    val stripe_id: String = "",
    @Serializable(with = TimestampSerializer::class) val created_at: Timestamp,
    @Serializable(with = TimestampSerializer::class) val updated_at: Timestamp,
    val fee: Int,
    val currency: String = "USD",

    @Column(name="charge_description", length=4096)
    val charge_description: String = "",
    @Serializable(with = TimestampSerializer::class) val start_time: Timestamp,
    @Serializable(with = TimestampSerializer::class) val end_time: Timestamp,
    val is_accepting_new_subscribers: Boolean = false,
    val employees_only: Boolean = false,
    val allow_all_stores: Boolean = false,
    val callout_text: String = "",
    val policy_text: String = "",
    val recurrence_interval_type: String = "",
    val recurrence_interval_units: Int = 0,
    val plan_benefit_short: String = "",
    val plan_benefit_long: String = "",
    val plan_benefit_delivery_fee: String = "",
    val signup_email_campaign_id: Long = 0,

    @Column(name="terms_and_conditions", length=4096)
    val terms_and_conditions: String = "",
    val consumer_discount_id: Int,

    @Column(name="terms_and_conditions_v2", length=4096)
    val terms_and_conditions_v2: String = "",

    val is_partner_plan: Boolean = false,
    val is_corporate_plan: Boolean = false,
    val can_be_paused: Boolean = false,
    val plan_type: String = "",
    val integration_name: String = "",
    val recurrence_interval_days: Int = 0,
    val priority: Int? = -1,
)