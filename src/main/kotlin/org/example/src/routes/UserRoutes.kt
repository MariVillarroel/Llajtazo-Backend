package org.example.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.example.src.controllers.UserController
import org.example.src.dto.UserRegisterRequest

fun Route.userRoutes(userController: UserController) {

    route("/users") {

        post("/register") {
            val request = call.receive<UserRegisterRequest>()

            try {
                val response = userController.register(request)
                call.respond(HttpStatusCode.Created, response)
            } catch (e: IllegalArgumentException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to (e.message ?: "Solicitud inválida"))
                )
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Error interno del servidor")
                )
            }
        }
    }
}
