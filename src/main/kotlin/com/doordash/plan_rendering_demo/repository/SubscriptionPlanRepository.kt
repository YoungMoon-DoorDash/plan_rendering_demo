package com.doordash.plan_rendering_demo.repository

import com.doordash.plan_rendering_demo.model.Text
import com.doordash.plan_rendering_demo.model.User
import com.doordash.plan_rendering_demo.model.subscription.SubscriptionPlan
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface SubscriptionPlanRepository: JpaRepository<SubscriptionPlan, Int> {
    @Query(
        value = "SELECT t.* FROM subscription_plan t WHERE lower(t.integration_name) like lower(:name)",
        nativeQuery = true
    )
    fun searchByName(@Param("name") name: String): List<SubscriptionPlan>
}