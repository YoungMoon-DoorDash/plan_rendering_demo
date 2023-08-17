package com.doordash.plan_rendering_demo.repository

import com.doordash.plan_rendering_demo.model.Text
import com.doordash.plan_rendering_demo.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface TextRepository: JpaRepository<Text, Long> {
    @Query(
        value = "SELECT t.* FROM text t WHERE lower(t.value) like lower(:value)",
        nativeQuery = true
    )
    fun serchByValue(@Param("value") value: String): List<Text>


    @Query(
        value = "SELECT t.* FROM text t where lower(t.name) = lower(:name)",
        nativeQuery = true
    )
    fun findText(@Param("name") name: String): Text?
}