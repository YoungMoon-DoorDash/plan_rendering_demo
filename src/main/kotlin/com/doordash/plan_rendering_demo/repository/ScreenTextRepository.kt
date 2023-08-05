package com.doordash.plan_rendering_demo.repository

import com.doordash.plan_rendering_demo.model.ScreenText
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ScreenTextRepository: JpaRepository<ScreenText, Long>