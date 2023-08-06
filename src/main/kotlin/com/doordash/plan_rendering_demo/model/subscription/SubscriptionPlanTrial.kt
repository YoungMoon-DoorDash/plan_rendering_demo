package com.doordash.plan_rendering_demo.model.subscription

import com.doordash.plan_rendering_demo.utils.TimestampSerializer
import jakarta.persistence.Entity
import jakarta.persistence.Id
import kotlinx.serialization.Serializable
import java.sql.Timestamp

@Entity
@Serializable
data class SubscriptionPlanTrial(
    @Id val id: Int,
    @Serializable(with = TimestampSerializer::class) val created_at: Timestamp,
    @Serializable(with = TimestampSerializer::class) val updated_at: Timestamp,
    val is_active: Boolean,
    val interval_type: String = "",
    val interval_units: Int = 0,
    val payment_provider_type: String = "",
    val consumer_subscription_plan_id: Int,
)