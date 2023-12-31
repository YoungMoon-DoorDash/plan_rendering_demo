package com.doordash.plan_rendering_demo.controller

import com.doordash.plan_rendering_demo.factory.HtmlFactory
import com.doordash.plan_rendering_demo.model.Experiment
import com.doordash.plan_rendering_demo.repository.ExperimentRepository
import com.doordash.plan_rendering_demo.utils.filterName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class ExperimentController(
    private val experimentRepository: ExperimentRepository
) {
    @GetMapping("/experiment")
    fun experimentHome(@RequestParam search: String?, model: Model): String {
        model["html-title-line"] = HtmlFactory.getTitleLine(
            "/experiment",
            "experiment",
            search ?: "",
            placeHolder = "Search by experiment name"
        )
        setExperimentParams(model, "Registered Experiments", true)
        if (search.isNullOrBlank()) {
            model["search"] = ""
            model["experiments"] = experimentRepository.findAll().map {
                it.copy(controls = asControlList(it.controls))
            }
        } else {
            model["search"] = search
            model["experiments"] = experimentRepository.searchByName('%' + search + '%')
        }
        return "experiment"
    }

    @GetMapping("/experiment/add")
    fun experimentAddPage(model: Model): String {
        setExperimentParams(model, "Registered a new experiment")
        return "experiment-add"
    }

    @PostMapping("/experiment/add")
    fun experimentRegister(
        @RequestParam name: String,
        @RequestParam controls: String,
        model: Model
    ): String {
        val saved = addOrUpdate(name, controls)
        return showExperiment(model, saved)
    }

    @GetMapping("/experiment/info")
    fun experimentInfo(@RequestParam id: Long, model: Model): String {
        val findExperiment = experimentRepository.findById(id)
        if (findExperiment.isPresent) {
            return showExperiment(model, findExperiment.get())
        }

        return experimentHome(null, model)
    }

    @GetMapping("/experiment/edit")
    fun experimentEditPage(@RequestParam("id") id: Long, model: Model): String {
        val experiment = experimentRepository.findById(id)
        if (experiment.isPresent) {
            setExperimentParams(model, "Edit experiment information")
            model["experiment"] = experiment.get()
            return "experiment-edit"
        }

        return experimentHome(null, model)
    }

    @PostMapping("/experiment/edit")
    fun experimentUpdate(
        @RequestParam id: Long,
        @RequestParam name: String,
        @RequestParam controls: String,
        model: Model
    ): String {
        val filteredName = name.filterName()
        val saved = experimentRepository.save(
            Experiment(id = id, name = filteredName, controls = filterControls(controls))
        )
        return showExperiment(model, saved)
    }

    @GetMapping("/experiment/delete")
    fun experimentUpdate(@RequestParam id: Long, model: Model
    ): String {
        experimentRepository.deleteById(id)
        return experimentHome(null, model)
    }

    @GetMapping("/experiment/import")
    fun experimentImportPage(model: Model): String {
        setExperimentParams(model, "Import experiments from Json")
        return "experiment-import"
    }

    @Serializable
    private data class ImportExperimentData(
        val name: String,
        val controls: String
    )

    @PostMapping("/experiment/import")
    fun experimentImport(
        @RequestParam values: String,
        model: Model
    ): String {
        Json.decodeFromString<List<ImportExperimentData>>(values).forEach {
            addOrUpdate(it.name, it.controls)
        }

        return experimentHome(null, model)
    }
    
    private fun addOrUpdate(name: String, controls: String): Experiment {
        val filteredName = name.filterName()
        return experimentRepository.findByName(filteredName)?.let {
            experimentRepository.save(
                Experiment(id = it.id, name = filteredName, controls = filterControls(controls))
            )
        } ?: run {
            experimentRepository.save(
                Experiment(name = filteredName, controls = filterControls(controls))
            )
        }
    }

    private fun filterControls(controls: String): String =
        controls.split(",").joinToString(",") { it.trim().lowercase() }

    private fun showExperiment(model: Model, experiment: Experiment): String {
        setExperimentParams(model, experiment.name)
        model["experiment"] = experiment.copy(
            controls = asControlList(experiment.controls)
        )
        return "experiment-info"
    }

    private fun setExperimentParams(model: Model, title: String, isHome: Boolean = false) {
        model["banner"] = HtmlFactory.getHomeBanner(
            if (isHome) "experiment" else ""
        )
        model["title"] = title
    }

    private fun asControlList(controls: String): String =
        if (controls.isBlank())
            "&nbsp;"
        else
            controls.split(",").joinToString(
                prefix = "<ul style='margin-bottom:0pt;'><li>",
                postfix = "</li></ul>",
                separator = "</li><li>"
            )
}