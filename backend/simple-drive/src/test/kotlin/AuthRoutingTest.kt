//import com.fasterxml.jackson.module.kotlin.jsonMapper
//import com.simpledrive.Server
//import com.simpledrive.SignupUserRequest
//import com.simpledrive.module
//import io.ktor.client.*
//import io.ktor.client.call.*
//import io.ktor.client.plugins.contentnegotiation.*
//import io.ktor.client.request.*
//import io.ktor.http.*
//import io.ktor.serialization.jackson.*
//import io.ktor.server.testing.*
//import org.junit.jupiter.api.Assertions.*
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//
//class AuthRoutingTest {
//
//    @BeforeEach
//    fun mockDB(){
//        val mockDB = mockk
//    }
//
//    @Test
//    fun testSignup() = testApplication {
//        application {
//            module()
//        }
//
//        val response = client.post("/auth/signup") {
//            contentType(ContentType.Application.Json)
//            val body = jsonMapper().writeValueAsString(SignupUserRequest("testuser@example.com", "password", "Test User"))
//            setBody(body)
//        }
//        assertEquals(HttpStatusCode.OK, response.status)
//    }
//
//    @Test
//    fun testLogin() = testApplication {
//        val client = createClient {
//            install(ContentNegotiation) {
//                jackson()
//            }
//        }
//        val response = client.post("/auth/login") {
//            contentType(ContentType.Application.Json)
//            setBody(SignupUserRequest("testuser@example.com", "password", "Test User"))
//        }
//        assertEquals(HttpStatusCode.OK, response.status)
//        val responseBody = response.body<Map<String, String>>()
//        assertNotNull(responseBody["accessToken"])
//    }
//}