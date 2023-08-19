package com.doordash.plan_rendering_demo.controller

import com.doordash.plan_rendering_demo.factory.HtmlFactory
import com.doordash.plan_rendering_demo.model.User
import com.doordash.plan_rendering_demo.repository.ExperimentRepository
import com.doordash.plan_rendering_demo.repository.UserRepository
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class UserController(
    private val userRepository: UserRepository,
    private val experimentRepository: ExperimentRepository,
) {
    @GetMapping("/user")
    fun userHome(@RequestParam search: String?, model: Model): String {
        model["html-title-line"] = HtmlFactory.getTitleLine(
            "/user",
            "user",
            search ?: "",
            placeHolder = "Search by user name"
        )
        setUserParams(model, "Registered Users", true)
        if (search.isNullOrBlank()) {
            model["search"] = ""
            model["users"] = userRepository.findAll().map {
                it.copy(experiments = asConfigurationList(it.experiments))
            }
        } else {
            model["search"] = search
            model["users"] = userRepository.searchByName('%' + search + '%').map {
                it.copy(experiments = asConfigurationList(it.experiments))
            }
        }
        return "user"
    }

    @GetMapping("/user/add")
    fun userAddPage(model: Model): String {
        setUserParams(model, "Registered a new user")
        addExperimentList(model)
        return "user-add"
    }

    @PostMapping("/user/add")
    fun userRegister(
        @RequestParam name: String,
        @RequestParam experiments: String,
        model: Model
    ): String {
        val saved = addOrUpdate(name, experiments)
        return showUser(model, saved)
    }
    
    @GetMapping("/user/info")
    fun userInfo(@RequestParam id: Long, model: Model): String {
        val findUser = userRepository.findById(id)
        if (findUser.isPresent) {
            return showUser(model, findUser.get())
        }

        return userHome(null, model)
    }

    @GetMapping("/user/edit")
    fun userEditPage(@RequestParam("id") id: Long, model: Model): String {
        val user = userRepository.findById(id)
        if (user.isPresent) {
            setUserParams(model, "Edit user information")
            model["user"] = user.get()
            addExperimentList(model)
            return "user-edit"
        }

        return userHome(null, model)
    }

    @PostMapping("/user/edit")
    fun userUpdate(
        @RequestParam id: Long,
        @RequestParam name: String,
        @RequestParam experiments: String,
        model: Model
    ): String {
        val convertName = name.trim()
        val convertExperiments = filterConfigurations(experiments)
        val savedUser = userRepository.save(
            User(id = id, name = convertName, experiments = convertExperiments)
        )
        return showUser(model, savedUser)
    }

    @GetMapping("/user/delete")
    fun userUpdate(@RequestParam id: Long, model: Model
    ): String {
        userRepository.deleteById(id)
        return userHome(null, model)
    }

    @GetMapping("/user/import")
    fun userImportPage(model: Model): String {
        setUserParams(model, "Import users from Json")
        return "user-import"
    }
    
    @Serializable
    private data class ImportUserData(
        val name: String,
        val email: String,
        val experiments: String
    )

    @PostMapping("/user/import")
    fun userImport(
        @RequestParam values: String,
        model: Model
    ): String {
        Json.decodeFromString<List<ImportUserData>>(values).forEach{ user ->
            addOrUpdate(user.name, user.experiments)
        }

        return userHome(null, model)
    }
    
    private fun addOrUpdate(name: String, experiments: String): User {
        val convertName = name.trim()
        val convertExperiments = filterConfigurations(experiments)
        return userRepository.findUser(convertName)?.let {
            userRepository.save(
                User(id = it.id, name = convertName, experiments = convertExperiments)
            )
        } ?: run {
            userRepository.save(
                User(name = convertName, experiments = convertExperiments)
            )
        }
    }

    private fun showUser(model: Model, user: User): String {
        setUserParams(model, user.name)
        model["user"] = user.copy(
            experiments = asConfigurationList(user.experiments)
        )
        return "user-info"
    }

    private fun setUserParams(model: Model, title: String, isHome: Boolean = false) {
        model["banner"] = HtmlFactory.getHomeBanner(
            if (isHome) "user" else ""
        )
        model["title"] = title
    }

    private fun filterConfigurations(experiments: String): String =
        experiments.split(",").joinToString(",") { exp ->
            exp.split(":").joinToString(":") { it.trim().lowercase() }
        }


    private fun asConfigurationList(configuration: String): String =
        if (configuration.isBlank())
            "&nbsp;"
        else
            configuration.split(",").joinToString(
                prefix = "<ul style='margin-bottom:0pt;'><li>",
                postfix = "</li></ul>",
                separator = "</li><li>"
            )

    private fun addExperimentList(model: Model) {
        model["experimentList"] = experimentRepository.findAll().joinToString(
            prefix = "<li>",
            postfix = "</li>",
            separator = "</li><li>"
        ) { exp ->
            val controls = exp.controls.split(",").joinToString(
                prefix = "<li>",
                postfix = "</li>",
                separator = "</li><li>"
            ) { it }
            exp.name + "<ul>" + controls + "</ul>"
        }
    }
}