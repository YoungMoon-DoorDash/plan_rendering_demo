package com.doordash.plan_rendering_demo.repository

import com.doordash.plan_rendering_demo.model.Screen
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ScreenRepository: JpaRepository<Screen, Long> {
    @Query(
        value = "SELECT t.* FROM screen t WHERE lower(t.name) like lower(:name)",
        nativeQuery = true
    )
    fun searchByName(@Param("name") name: String): List<Screen>

    @Query(
        value = "SELECT t.* FROM screen t where lower(t.name) = lower(:name)",
        nativeQuery = true
    )
    fun findScreen(@Param("name") name: String): Screen?
}