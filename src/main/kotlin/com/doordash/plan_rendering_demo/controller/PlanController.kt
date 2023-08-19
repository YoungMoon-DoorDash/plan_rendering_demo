package com.doordash.plan_rendering_demo.controller

import com.doordash.plan_rendering_demo.factory.HtmlFactory
import com.doordash.plan_rendering_demo.model.subscription.SubscriptionPlan
import com.doordash.plan_rendering_demo.repository.SubscriptionPlanRepository
import com.doordash.plan_rendering_demo.repository.SubscriptionPlanTrialRepository
import kotlinx.serialization.json.Json
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
        model["html-title-line"] = HtmlFactory.getTitleLine(
            "/plan",
            "plan",
            search ?: "",
            withAdd = false,
            placeHolder = "Search by plan integration name"
        )
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
            setPlanParams(model, plan.get().integration_name)
            model["plan"] = plan.get()
            model["trials"] = subscriptionPlanTrialRepository.findTrialPlans(plan.get().id).joinToString(",") { it.id.toString() }
            return "plan-info"
        }

        return planHome(null, model)
    }

    @GetMapping("/plan/delete")
    fun planUpdate(@RequestParam id: Int, model: Model
    ): String {
        subscriptionPlanRepository.deleteById(id)
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

    private fun setPlanParams(model: Model, title: String, isHome: Boolean = false) {
        model["banner"] = HtmlFactory.getHomeBanner(
            if (isHome) "plan" else ""
        )
        model["title"] = title
    }
}