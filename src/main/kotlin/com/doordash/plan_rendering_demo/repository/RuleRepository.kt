package com.doordash.plan_rendering_demo.repository

import com.doordash.plan_rendering_demo.model.Rule
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface RuleRepository: JpaRepository<Rule, Long> {
    @Query(
        value = "SELECT t.* FROM rule t WHERE lower(t.name) like lower(:name)",
        nativeQuery = true
    )
    fun searchByName(@Param("name") name: String): List<Rule>

    @Query(
        value = "SELECT t.* FROM rule t where lower(t.name) = lower(:name)",
        nativeQuery = true
    )
    fun findRule(@Param("name") name: String): Rule?
}