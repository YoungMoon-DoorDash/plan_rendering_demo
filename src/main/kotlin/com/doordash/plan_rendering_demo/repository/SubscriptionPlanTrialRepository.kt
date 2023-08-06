package com.doordash.plan_rendering_demo.repository

import com.doordash.plan_rendering_demo.model.subscription.SubscriptionPlanTrial
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface SubscriptionPlanTrialRepository: JpaRepository<SubscriptionPlanTrial, Int> {
    @Query(
        value = "SELECT t.* FROM subscription_plan_trial t WHERE t.consumer_subscription_plan_id = (:plan_id)",
        nativeQuery = true
    )
    fun findTrialPlans(@Param("plan_id") planId: Int): List<SubscriptionPlanTrial>
}