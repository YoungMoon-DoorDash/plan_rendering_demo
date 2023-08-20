package com.doordash.plan_rendering_demo.controller

import com.doordash.plan_rendering_demo.factory.ScreenElementFactory
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class ScreenRestController {
    @Serializable
    data class ScreenResponse(val status: Int, val message: String? = null)

    @PostMapping("/screen/validate")
    @ResponseStatus(HttpStatus.OK)
    fun screenValidate(
        @RequestBody body: String
    ): String {
        return Json.encodeToString(ScreenResponse.serializer(), validateScreen(body))
    }

    private fun validateScreen(body: String): ScreenResponse =
        try {
            // val json = body.trim().replace('\n', ' ')
            ScreenElementFactory.toScreen(body.trim())
            ScreenResponse(HttpStatus.OK.value())
        } catch (e: Exception) {
            ScreenResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.message)
        }
}