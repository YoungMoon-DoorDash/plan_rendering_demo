package com.doordash.plan_rendering_demo.controller

import com.doordash.plan_rendering_demo.factory.HtmlFactory
import com.doordash.plan_rendering_demo.model.subscription.SubscriptionPlan
import com.doordash.plan_rendering_demo.model.subscription.SubscriptionPlanTrial
import com.doordash.plan_rendering_demo.repository.SubscriptionPlanRepository
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
class PlanController(
    private val subscriptionPlanRepository: SubscriptionPlanRepository,
    private val subscriptionPlanTrialRepository: SubscriptionPlanTrialRepository,
) {
    @GetMapping("/plan")
    fun planHome(@RequestParam search: String?, model: Model): String {
        setPlanParams(model, "Registered Plans", true)
        if (search.isNullOrBlank()) {
            model["plans"] = subscriptionPlanRepository.findAllActive().sortedBy { it.id }
            model["search"] = ""
        } else {
            model["plans"] = subscriptionPlanRepository.searchByName('%' + search + '%').sortedBy { it.id }
            model["search"] = search
        }
        return "plan"
    }

    @GetMapping("/plan/info")
    fun planInfo(@RequestParam id: Int, model: Model): String {
        val plan = subscriptionPlanRepository.findById(id)
        if (plan.isPresent) {
            setPlanParams(model, "Registered plan > ${plan.get().id} > ${plan.get().integration_name}")
            model["plan"] = plan.get()
            model["trials"] = subscriptionPlanTrialRepository.findTrialPlans(plan.get().id).joinToString(",") { it.id.toString() }
            return "plan-info"
        }

        return planHome(null, model)
    }

    @GetMapping("/plan/import")
    fun planImportPage(model: Model): String {
        setPlanParams(model, "Import plans from Json")
        return "plan-import"
    }

    @PostMapping("/plan/import")
    fun planImport(
        @RequestParam values: String,
        model: Model
    ): String {
        Json.decodeFromString<List<SubscriptionPlan>>(values).forEach{
            subscriptionPlanRepository.save(it)
        }

        return planHome(null, model)
    }

    @GetMapping("/plan/trial")
    fun trialHome(model: Model): String {
        setTrialParams(model, "Registered Trail Plans", true)
        model["trials"] = subscriptionPlanTrialRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
        return "trial"
    }

    @GetMapping("/plan/trial/import")
    fun trialImportPage(model: Model): String {
        setTrialParams(model, "Import trial from Json")
        return "trial-import"
    }

    @PostMapping("/plan/trial/import")
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

    private fun setPlanParams(model: Model, title: String, isHome: Boolean = false) {
        model["banner"] = HtmlFactory.getHomeBanner(
            if (isHome) "plan" else ""
        )
        model["title"] = title
    }
}