package com.doordash.plan_rendering_demo.controller

import com.doordash.plan_rendering_demo.factory.HtmlFactory
import com.doordash.plan_rendering_demo.model.Text
import com.doordash.plan_rendering_demo.repository.TextRepository
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
class TextController(
    private val textRepository: TextRepository
) {
    @GetMapping("/text")
    fun textHome(@RequestParam search: String?, model: Model): String {
        model["html-title-line"] = HtmlFactory.getTitleLine(
            "/text",
            "text",
            search ?: "",
            placeHolder = "Search by content"
        )
        setTextParams(model, "Registered Texts", true)
        if (search.isNullOrBlank()) {
            model["search"] = ""
            model["texts"] = textRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))
        } else {
            model["search"] = search
            model["texts"] = textRepository.serchByValue('%' + search + '%').sortedBy { it.name }
        }
        return "text"
    }

    @GetMapping("/text/add")
    fun textAddPage(model: Model): String {
        setTextParams(model, "Registered a new text")
        return "text-add"
    }

    @PostMapping("/text/add")
    fun textRegister(
        @RequestParam name: String,
        @RequestParam value: String,
        model: Model
    ): String {
        val saved = addOrUpdate(name, value)
        return showText(model, saved)
    }

    @GetMapping("/text/info")
    fun textInfo(@RequestParam id: Long, model: Model): String {
        val findText = textRepository.findById(id)
        if (findText.isPresent) {
            return showText(model, findText.get())
        }

        return textHome(null, model)
    }

    @GetMapping("/text/edit")
    fun textEditPage(@RequestParam("id") id: Long, model: Model): String {
        val text = textRepository.findById(id)
        if (text.isPresent) {
            setTextParams(model, "Edit text information")
            model["text"] = text.get()
            return "text-edit"
        }

        return textHome(null, model)
    }

    @PostMapping("/text/edit")
    fun textUpdate(
        @RequestParam id: Long,
        @RequestParam name: String,
        @RequestParam value: String,
        model: Model
    ): String {
        val convertName = name.filterName()
        val convertValue = value.trim()
        val saved = textRepository.save(
            Text(id = id, name = convertName, value = convertValue)
        )
        return showText(model, saved)
    }

    @GetMapping("/text/delete")
    fun textUpdate(@RequestParam id: Long, model: Model
    ): String {
        textRepository.deleteById(id)
        return textHome(null, model)
    }

    @GetMapping("/text/import")
    fun textImportPage(model: Model): String {
        setTextParams(model, "Import texts from Json")
        return "text-import"
    }

    @Serializable
    private data class ImportTextData(
        val name: String,
        val value: String
    )

    @PostMapping("/text/import")
    fun textImport(
        @RequestParam values: String,
        model: Model
    ): String {
        Json.decodeFromString<List<ImportTextData>>(values).forEach{ text ->
            addOrUpdate(text.name, text.value)
        }

        return textHome(null, model)
    }

    private fun addOrUpdate(name: String, value: String): Text {
        val convertName = name.filterName()
        val convertValue = value.trim()
        return textRepository.findText(convertName)?.let {
            textRepository.save(Text(id = it.id, name = convertName, value = convertValue))
        } ?: run {
            textRepository.save(Text(name = convertName, value = convertValue))
        }
    }

    private fun showText(model: Model, text: Text): String {
        setTextParams(model, text.name)
        model["text"] = text
        return "text-info"
    }

    private fun setTextParams(model: Model, title: String, isHome: Boolean = false) {
        model["banner"] = HtmlFactory.getHomeBanner(
            if (isHome) "text" else ""
        )
        model["title"] = title
    }
}