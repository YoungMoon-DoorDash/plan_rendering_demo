package com.doordash.plan_rendering_demo.repository

import com.doordash.plan_rendering_demo.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<User, Long> {
    @Query(
        value = "SELECT u.* FROM user u WHERE lower(u.name) like lower(:name)",
        nativeQuery = true
    )
    fun searchByName(@Param("name") name: String): List<User>

    @Query(
        value = "SELECT u.* FROM user u where lower(u.name) = lower(:name)",
        nativeQuery = true
    )
    fun findUser(@Param("name") name: String): User?
}