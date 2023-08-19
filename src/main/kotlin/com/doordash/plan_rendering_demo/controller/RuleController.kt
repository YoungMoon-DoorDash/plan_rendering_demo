package com.doordash.plan_rendering_demo.controller

import com.doordash.plan_rendering_demo.factory.HtmlFactory
import com.doordash.plan_rendering_demo.factory.RuleEngine
import com.doordash.plan_rendering_demo.factory.rule.CommandRuleFactory
import com.doordash.plan_rendering_demo.model.Rule
import com.doordash.plan_rendering_demo.model.RuleType
import com.doordash.plan_rendering_demo.model.toRuleType
import com.doordash.plan_rendering_demo.repository.RuleRepository
import com.doordash.plan_rendering_demo.repository.ScreenRepository
import com.doordash.plan_rendering_demo.repository.SubscriptionPlanRepository
import com.doordash.plan_rendering_demo.repository.TextRepository
import com.doordash.plan_rendering_demo.repository.UserRepository
import com.doordash.plan_rendering_demo.utils.filterName
import kotlinx.serialization.Serializable
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
    private val textRepository: TextRepository,
    private val screenRepository: ScreenRepository
) {
    @GetMapping("/rule")
    fun ruleHome(@RequestParam search: String?, model: Model): String {
        model["html-title-line"] = HtmlFactory.getTitleLine(
            "/rule",
            "rule",
            search ?: "",
            placeHolder = "Search by rule name"
        )
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
        addCommandList(model)
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
        val findRule = ruleRepository.findById(id)
        if (findRule.isPresent) {
            val rule = findRule.get()
            setRuleParams(model, "Edit rule information")
            model["rule_types"] = HtmlFactory.getRuleTypeSelect(rule.type)
            model["rule"] = rule
            addCommandList(model)
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

        val convertRun = RuleEngine.convertRuleRun(run)
        RuleEngine.validate(ruleType, convertRun)
        val saved = ruleRepository.save(
            Rule(
                id = id,
                name = convertName,
                type = ruleType,
                run = RuleEngine.formatToJson(convertRun)
            )
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

    @GetMapping("/rule/simulate")
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
        model["parameters"] = ""
        return "rule-simulate"
    }

    @PostMapping("/rule/simulate")
    fun ruleSimulateRun(
        @RequestParam id: Long,
        @RequestParam parameters: String,
        model: Model
    ): String {
        val findRule = ruleRepository.findById(id)
        if (!findRule.isPresent) {
            return ruleHome(null, model)
        }

        RuleEngine.setRepository(planRepository, userRepository, textRepository, screenRepository)

        val sb = StringBuilder()
        RuleEngine.populateContext(parameters)

        val rule = findRule.get()
        RuleEngine.getContext(sb)
        sb.append("\n\n=============== Tracing:")
        val result = RuleEngine.execute(rule, sb)
        sb.append("\n\n=============== Result: ${rule.name}: $result")

        setRuleParams(model, "Simulate Rule > ${rule.name}")
        model["rule"] = rule
        model["result"] = sb.toString()
        model["parameters"] = parameters
        return "rule-simulate"
    }

    private fun showRule(model: Model, rule: Rule): String {
        setRuleParams(model, rule.name)
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

        val runValue = RuleEngine.formatToJson(convertRun)
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

    private fun addCommandList(model: Model) {
        model["commandList"] = CommandRuleFactory.getCommandList().joinToString(
            prefix = "<li>",
            postfix = "</li>",
            separator = "</li><li>"
        ) { it }
    }
}