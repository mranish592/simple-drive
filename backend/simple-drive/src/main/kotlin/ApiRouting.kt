package com.simpledrive

//import io.ktor.server.application.*
//import io.ktor.server.auth.*
//import io.ktor.server.response.*
//import io.ktor.server.routing.*

//fun Application.apiRouting() {
//
//    routing {
//        route("/") {
//            get {
//                call.respond("Simple Drive API")
//            }
//        }
//        authenticate("bearer") {
//            route("/api") {
//                get("/list") {
//                    println("Accessing /api/list") // add this
//                    println("Authorization header: ${call.request.headers["Authorization"]}") // add this
//                    call.respond(listOf("item1", "item2", "item3"))
//                }
//                get("/download/{id}") {
//                    val id = call.parameters["id"]
//                    call.respond("Downloading item $id")
//                }
//            }
//        }
//    }
//}