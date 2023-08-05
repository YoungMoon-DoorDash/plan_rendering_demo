package com.doordash.plan_rendering_demo.repository

import com.doordash.plan_rendering_demo.model.ScreenElement
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ScreenElementRepository: JpaRepository<ScreenElement, Long>