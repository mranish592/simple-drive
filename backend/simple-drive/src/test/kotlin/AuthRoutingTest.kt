//import com.fasterxml.jackson.module.kotlin.jsonMapper
//import com.simpledrive.*
//import io.ktor.client.*
//import io.ktor.client.call.*
//import io.ktor.client.plugins.contentnegotiation.*
//import io.ktor.client.request.*
//import io.ktor.http.*
//import io.ktor.serialization.jackson.*
//import io.ktor.server.testing.*
//import io.mockk.*
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.launch
//import org.junit.jupiter.api.Assertions.*
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.litote.kmongo.coroutine.CoroutineClient
//import org.mindrot.jbcrypt.BCrypt
//
//class AuthRoutingTest {
//
//    val name = "name"
//    val password = "pass"
//    val email = "user@example.com"
//
//    @BeforeEach
//    fun mockDB(){
//        val mockClient = mockk<CoroutineClient>()
//        mockkObject(DB)
//        every { DB["createMongoClient"]() } returns mockClient
//        coEvery { DB }
//        coEvery { DB.createUser(any()) }
//        coEvery { DB.getUserByEmail(any())} returns User(name, email, BCrypt.hashpw(password, BCrypt.gensalt()) )
//    }
//
//    @Test
//    fun testSignup() = testApplication {
//        application {
//            module()
//        }
//
//        val response = client.post("/auth/signup") {
//            val email2 = "user2@example.com"
//            contentType(ContentType.Application.Json)
//            val body = jsonMapper().writeValueAsString(SignupUserRequest(email2, password, name))
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