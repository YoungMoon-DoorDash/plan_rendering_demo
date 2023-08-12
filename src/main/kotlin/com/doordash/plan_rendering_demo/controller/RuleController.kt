package com.doordash.plan_rendering_demo.controller

import com.doordash.plan_rendering_demo.factory.HtmlFactory
import com.doordash.plan_rendering_demo.factory.RuleEngine
import com.doordash.plan_rendering_demo.factory.filterName
import com.doordash.plan_rendering_demo.model.Rule
import com.doordash.plan_rendering_demo.model.RuleType
import com.doordash.plan_rendering_demo.model.toRuleType
import com.doordash.plan_rendering_demo.repository.RuleRepository
import com.doordash.plan_rendering_demo.repository.SubscriptionPlanRepository
import com.doordash.plan_rendering_demo.repository.UserRepository
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class RuleController(
    private val planRepository: SubscriptionPlanRepository,
    private val ruleRepository: RuleRepository,
    private val userRepository: UserRepository,
) {
    @GetMapping("/rule")
    fun ruleHome(@RequestParam search: String?, model: Model): String {
        setRuleParams(model, "Registered Rules")
        if (search.isNullOrBlank()) {
            model["search"] = ""
            model["rules"] = ruleRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))
        } else {
            model["search"] = search
            model["rules"] = ruleRepository.searchByName('%' + search + '%').sortedBy { it.name }
        }
        return "rule"
    }

    @GetMapping("/rule/add")
    fun ruleAddPage(model: Model): String {
        setRuleParams(model, "Registered a new rule")
        model["rule_types"] = HtmlFactory.getRuleTypeSelect(RuleType.CHECK)
        return "rule-add"
    }

    @PostMapping("/rule/add")
    fun ruleRegister(
        @RequestParam name: String,
        @RequestParam type: Int,
        @RequestParam run: String,
        model: Model
    ): String {
        val saved = addOrUpdate(name, type.toRuleType(), run)
        return showRule(model, saved)
    }

    @GetMapping("/rule/info")
    fun ruleInfo(@RequestParam id: Long, model: Model): String {
        val findRule = ruleRepository.findById(id)
        if (findRule.isPresent) {
            return showRule(model, findRule.get())
        }

        return ruleHome(null, model)
    }

    @GetMapping("/rule/edit")
    fun ruleEditPage(@RequestParam("id") id: Long, model: Model): String {
        val rule = ruleRepository.findById(id)
        if (rule.isPresent) {
            setRuleParams(model, "Edit rule information")
            model["rule_types"] = HtmlFactory.getRuleTypeSelect(rule.get().type)
            model["rule"] = rule.get()
            return "rule-edit"
        }

        return ruleHome(null, model)
    }

    @PostMapping("/rule/edit")
    fun ruleUpdate(
        @RequestParam id: Long,
        @RequestParam name: String,
        @RequestParam type: Int,
        @RequestParam run: String,
        model: Model
    ): String {
        val convertName = name.filterName()
        val ruleType = type.toRuleType()
        val saved = ruleRepository.save(
            Rule(id = id, name = convertName, type = ruleType, run = run)
        )
        return showRule(model, saved)
    }

    @GetMapping("/rule/delete")
    fun ruleUpdate(@RequestParam id: Long, model: Model
    ): String {
        ruleRepository.deleteById(id)
        return ruleHome(null, model)
    }

    @GetMapping("/rule/import")
    fun ruleImportPage(model: Model): String {
        setRuleParams(model, "Import rules from Json")
        return "rule-import"
    }

    @Serializable
    private data class ImportRuleData(
        val name: String,
        val type: String,
        val run: String
    )

    @PostMapping("/rule/import")
    fun ruleImport(
        @RequestParam values: String,
        model: Model
    ): String {
        Json.decodeFromString<List<ImportRuleData>>(values).forEach{ rule ->
            addOrUpdate(rule.name, rule.type.toRuleType(), rule.run)
        }
        return ruleHome(null, model)
    }

    @GetMapping("/rule/check")
    fun ruleSimulate(
        @RequestParam id: Long,
        model: Model
    ): String {
        val findRule = ruleRepository.findById(id)
        if (!findRule.isPresent) {
            return ruleHome(null, model)
        }

        setRuleParams(model, "Simulate Rule > ${findRule.get().name}")
        model["rule"] = findRule.get()
        model["result"] = "none"
        model["parameters"] = ""
        return "rule-check"
    }

    @PostMapping("/rule/check")
    fun ruleSimulateRun(
        @RequestParam id: Long,
        @RequestParam parameters: String,
        model: Model
    ): String {
        val findRule = ruleRepository.findById(id)
        if (!findRule.isPresent) {
            return ruleHome(null, model)
        }

        RuleEngine.setRepository(planRepository, userRepository)

        val rule = findRule.get()
        setRuleParams(model, "Simulate Rule > ${rule.name}")
        model["rule"] = rule
        model["result"] = RuleEngine.execute(
            rule,
            Json.decodeFromString<Map<String, String>>(parameters)
        )
        model["parameters"] = parameters
        return "rule-check"
    }

    private fun showRule(model: Model, rule: Rule): String {
        setRuleParams(model, "Registered Rule > ${rule.name}")
        model["rule"] = rule
        return "rule-info"
    }
    
    private fun addOrUpdate(
        name: String,
        ruleType: RuleType,
        run: String
    ): Rule {
        val convertName = name.filterName()
        val convertRun = RuleEngine.convertRuleRun(run)
        RuleEngine.validate(ruleType, convertRun)

        val runValue = Json.encodeToString(convertRun)
        return ruleRepository.findRule(convertName)?.let {
            ruleRepository.save(Rule(id = it.id, name = convertName, type = ruleType, run = runValue))
        } ?: run {
            ruleRepository.save(Rule(name = convertName, type = ruleType, run = runValue))
        }
    }

    private fun setRuleParams(model: Model, title: String, isHome: Boolean = false) {
        model["banner"] = HtmlFactory.getHomeBanner(
            if (isHome) "rule" else ""
        )
        model["title"] = title
    }
}