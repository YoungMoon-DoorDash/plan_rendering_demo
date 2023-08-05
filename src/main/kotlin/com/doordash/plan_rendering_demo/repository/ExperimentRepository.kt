package com.doordash.plan_rendering_demo.repository

import com.doordash.plan_rendering_demo.model.Experiment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ExperimentRepository: JpaRepository<Experiment, Long> {
    @Query(
        value = "SELECT u.* FROM experiment u WHERE lower(u.name)=lower(:name)",
        nativeQuery = true
    )
    fun findByName(@Param("name") name: String): Experiment?

    @Query(
        value = "SELECT u.* FROM experiment u WHERE lower(u.name) like lower(:name)",
        nativeQuery = true
    )
    fun searchByName(@Param("name") name: String): List<Experiment>
}