package com.doordash.plan_rendering_demo.controller

import com.doordash.plan_rendering_demo.factory.HtmlFactory
import com.doordash.plan_rendering_demo.model.User
import com.doordash.plan_rendering_demo.repository.ExperimentRepository
import com.doordash.plan_rendering_demo.repository.UserRepository
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
        setUserParams(model, "Registered Users", true)
        if (search.isNullOrBlank()) {
            model["search"] = ""
            model["users"] = userRepository.findAll().map {
                User(id = it.id,
                    name = it.name,
                    email = it.email,
                    experiments = asExperimentList(it.experiments)
                )
            }
        } else {
            model["search"] = search
            model["users"] = userRepository.searchByName('%' + search + '%')
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
        @RequestParam email: String,
        @RequestParam experiments: String,
        model: Model
    ): String {
        val convertName = name.trim()
        val convertEmail = email.trim()
        val convertExperiments = filterExperiments(experiments)
        val existingUser = userRepository.findUser(convertName, convertEmail)
        existingUser?.let {
            val saved = userRepository.save(
                User(id = it.id, name = convertName, email = convertEmail, experiments = convertExperiments)
            )
            return showUser(model, saved)
        }

        val savedUser = userRepository.save(
            User(name = convertName, email = convertEmail, experiments = convertExperiments)
        )
        return showUser(model, savedUser)
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
        @RequestParam email: String,
        @RequestParam experiments: String,
        model: Model
    ): String {
        val convertName = name.trim()
        val convertEmail = email.trim()
        val convertExperiments = filterExperiments(experiments)
        val savedUser = userRepository.save(
            User(id = id, name = convertName, email = convertEmail, experiments = convertExperiments)
        )
        return showUser(model, savedUser)
    }

    @GetMapping("/user/delete")
    fun userUpdate(@RequestParam id: Long, model: Model
    ): String {
        userRepository.deleteById(id)
        return userHome(null, model)
    }

    private fun showUser(model: Model, user: User): String {
        setUserParams(model, "Registered User > ${user.name}")
        model["user"] = user.copy(
            experiments = asExperimentList(user.experiments)
        )
        return "user-info"
    }

    private fun setUserParams(model: Model, title: String, isHome: Boolean = false) {
        model["banner"] = HtmlFactory.getHomeBanner(
            if (isHome) "user" else ""
        )
        model["title"] = title
    }

    private fun filterExperiments(experiments: String): String =
        experiments.split(",").joinToString(",") { exp ->
            exp.split(":").joinToString(":") { it.trim().lowercase() }
        }


    private fun asExperimentList(experiments: String): String =
        if (experiments.isBlank())
            "&nbsp;"
        else
            experiments.split(",").joinToString(
                prefix = "<ul style='margin-bottom:0pt;'><li>",
                postfix = "</li></ul>",
                separator = "</li><li>"
            )

    private fun addExperimentList(model: Model) {
        model["experimentList"] = experimentRepository.findAll().joinToString(
            prefix = "<li>",
            postfix = "</li>",
            separator = "</li><li>"
        ) { it.name }
    }
}