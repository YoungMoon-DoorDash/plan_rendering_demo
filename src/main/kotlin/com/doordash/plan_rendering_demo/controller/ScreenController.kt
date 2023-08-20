package com.doordash.plan_rendering_demo.controller

import com.doordash.plan_rendering_demo.factory.HtmlFactory
import com.doordash.plan_rendering_demo.factory.RuleEngine
import com.doordash.plan_rendering_demo.factory.ScreenElementFactory
import com.doordash.plan_rendering_demo.model.Screen
import com.doordash.plan_rendering_demo.repository.RuleRepository
import com.doordash.plan_rendering_demo.repository.ScreenRepository
import com.doordash.plan_rendering_demo.repository.SubscriptionPlanRepository
import com.doordash.plan_rendering_demo.repository.TextRepository
import com.doordash.plan_rendering_demo.repository.UserRepository
import com.doordash.plan_rendering_demo.utils.filterName
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class ScreenController(
    private val planRepository: SubscriptionPlanRepository,
    private val userRepository: UserRepository,
    private val textRepository: TextRepository,
    private val screenRepository: ScreenRepository
){
    @GetMapping("/screen")
    fun screenHome(@RequestParam search: String?, model: Model): String {
        setScreenParams(model, "Registered Screens", true)
        if (search.isNullOrBlank()) {
            model["search"] = ""
            model["screens"] = screenRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))
        } else {
            model["search"] = search
            model["screens"] = screenRepository.searchByName('%' + search + '%').sortedBy { it.name }
        }
        return "screen"
    }

    @GetMapping("/screen/add")
    fun screenAddPage(model: Model): String {
        setScreenParams(model, "Registered a new screen")
        return "screen-add"
    }

    @PostMapping("/screen/add")
    fun screenRegister(
        @RequestParam name: String,
        @RequestParam elements: String,
        model: Model
    ): String {
        val saved = addOrUpdate(name, elements)
        return showScreen(model, saved)
    }

    @GetMapping("/screen/info")
    fun screenInfo(@RequestParam id: Long, model: Model): String {
        val findScreen = screenRepository.findById(id)
        if (findScreen.isPresent) {
            return showScreen(model, findScreen.get())
        }

        return screenHome(null, model)
    }

    @GetMapping("/screen/edit")
    fun screenEditPage(@RequestParam("id") id: Long, model: Model): String {
        val screen = screenRepository.findById(id)
        if (screen.isPresent) {
            setScreenParams(model, "Edit screen information")
            model["screen"] = screen.get()
            return "screen-edit"
        }

        return screenHome(null, model)
    }

    @PostMapping("/screen/edit")
    fun screenUpdate(
        @RequestParam id: Long,
        @RequestParam name: String,
        @RequestParam elements: String,
        model: Model
    ): String {
        val convertName = name.filterName()
        val convertValue = elements.trim()
        ScreenElementFactory.toScreen(convertValue)

        val saved = screenRepository.save(
            Screen(id = id, name = convertName, elements = convertValue)
        )
        return showScreen(model, saved)
    }

    @GetMapping("/screen/delete")
    fun screenUpdate(@RequestParam id: Long, model: Model
    ): String {
        screenRepository.deleteById(id)
        return screenHome(null, model)
    }

    @GetMapping("/screen/simulateg")
    fun screenSimulate(
        @RequestParam id: Long,
        model: Model
    ): String {
        val findScreen = screenRepository.findById(id)
        if (!findScreen.isPresent) {
            return screenHome(null, model)
        }

        setScreenParams(model, "Simulate Screen > ${findScreen.get().name}")
        model["screen"] = findScreen.get()
        model["result"] = ""
        model["parameters"] = "{\n}"
        return "screen-simulate"
    }

    @PostMapping("/screen/simulate")
    fun screenSimulateRun(
        @RequestParam id: Long,
        @RequestParam parameters: String,
        model: Model
    ): String {
        val findScreen = screenRepository.findById(id)
        if (!findScreen.isPresent) {
            return screenHome(null, model)
        }

        RuleEngine.setRepository(planRepository, userRepository, textRepository, screenRepository)
        RuleEngine.populateContext(parameters)

        val screen = findScreen.get()
        val renderResult = RuleEngine.rendering(screen)
        setScreenParams(model, "Simulate Screen > ${screen.name}")
        model["screen"] = screen
        model["result"] = renderResult
        model["parameters"] = parameters
        return "screen-simulate"
    }
    
    private fun addOrUpdate(name: String, elements: String): Screen {
        val convertName = name.filterName()
        val convertValue = elements.trim()
        ScreenElementFactory.toScreen(convertValue)

        return screenRepository.findScreen(convertName)?.let {
            screenRepository.save(Screen(id = it.id, name = convertName, elements = convertValue))
        } ?: run {
            screenRepository.save(Screen(name = convertName, elements = convertValue))
        }
    }

    private fun showScreen(model: Model, screen: Screen): String {
        setScreenParams(model, screen.name)
        model["screen"] = screen
        return "screen-info"
    }

    private fun setScreenParams(model: Model, title: String, isHome: Boolean = false) {
        model["banner"] = HtmlFactory.getHomeBanner(
            if (isHome) "screen" else ""
        )
        model["title"] = title
        model["screenElements"] = HtmlFactory.getScreenElementsSelection()
    }
}