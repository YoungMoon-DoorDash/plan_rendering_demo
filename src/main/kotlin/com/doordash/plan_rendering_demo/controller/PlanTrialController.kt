package com.doordash.plan_rendering_demo.controller

import com.doordash.plan_rendering_demo.factory.HtmlFactory
import com.doordash.plan_rendering_demo.model.subscription.SubscriptionPlanTrial
import com.doordash.plan_rendering_demo.repository.SubscriptionPlanTrialRepository
import kotlinx.serialization.json.Json
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class PlanTrialController(
    private val subscriptionPlanTrialRepository: SubscriptionPlanTrialRepository
){
    @GetMapping("/trial")
    fun trialHome(model: Model): String {
        setTrialParams(model, "Registered Trail Plans", true)
        model["trials"] = subscriptionPlanTrialRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
        return "trial"
    }

    @GetMapping("/trial/import")
    fun trialImportPage(model: Model): String {
        setTrialParams(model, "Import trial from Json")
        return "trial-import"
    }

    @PostMapping("/trial/import")
    fun trialImport(
        @RequestParam values: String,
        model: Model
    ): String {
        Json.decodeFromString<List<SubscriptionPlanTrial>>(values).forEach{
            subscriptionPlanTrialRepository.save(it)
        }

        return trialHome(model)
    }

    private fun setTrialParams(model: Model, title: String, isHome: Boolean = false) {
        model["banner"] = HtmlFactory.getHomeBanner(
            if (isHome) "trial" else ""
        )
        model["title"] = title
    }
}