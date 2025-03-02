import com.simpledrive.ServerVerticle
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.http.HttpClientResponse
import io.vertx.core.http.HttpMethod
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.coAwait
import org.apache.logging.log4j.LogManager
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit


class Test {
    var vertx: Vertx = Vertx.vertx()
    val log = LogManager.getLogger(this.javaClass)

//    @Test
//    @Throws(Throwable::class)
//    fun `ServerVerticle should start successfully`() {
//        val testContext = VertxTestContext()
//        vertx.deployVerticle(ServerVerticle())
//            .onComplete(testContext.succeedingThenComplete())
//
//        Assertions.assertTrue(testContext.awaitCompletion(5, TimeUnit.SECONDS))
//        if (testContext.failed()) {
//            throw testContext.causeOfFailure()
//        }
//    }

    @Test
    @Throws(Throwable::class)
    fun `ServerVerticle should fail when same port`() {
        val testContext = VertxTestContext()
        val serverStarted = testContext.checkpoint();
        val responsesReceived = testContext.checkpoint();
        vertx.deployVerticle(ServerVerticle())
            .onComplete(testContext.failing{
                log.info("verticle deployed successfully")
                serverStarted.flag()
                val client = vertx.createHttpClient()

                client.request(HttpMethod.GET, 3000, "localhost", "/")
                    .compose { req -> req.send().compose(HttpClientResponse::body) }
                    .onComplete(testContext.failing { buffer ->
                        log.info("response received ${buffer}")
                        Assertions.assertEquals(buffer, false)
                        testContext.verify {
                            Assertions.assertEquals(buffer, "Ok")
                            responsesReceived.flag()
                        }
                    })
            })
        testContext.awaitCompletion(5, TimeUnit.SECONDS)
        assert(false)
//        testContext.awaitCompletion(5, TimeUnit.SECONDS)
//        log.info("verticle deployed")
//        val client = WebClient.create(vertx)
//        val client = vertx.createHttpClient()
//        log.info("client created")
//
//        client.request(HttpMethod.GET, 3000, "localhost", "/")
//            .compose { req -> req.send().compose(HttpClientResponse::body) }
//            .onComplete(testContext.succeeding { buffer ->
//                log.info("response received")
//                testContext.verify {
//                    Assertions.assertEquals(buffer.toString(), "{}")
//                    testContext.completeNow()
//                }
//            })
//        val res = client.request(HttpMethod.GET, 3000, "localhost", "/").send()
//        log.info("request sent ${res}")
//        Thread.sleep(1000)
//        log.info("closing test")



    }

//    fun testServer(vertx: Vertx, testContext: VertxTestContext) {
//        val serverStarted = testContext.checkpoint()
//        val requestsServed = testContext.checkpoint(10)
//        val responsesReceived = testContext.checkpoint(10)
//
//        vertx.createHttpServer()
//            .requestHandler { req ->
//                req.response().end("Ok")
//                requestsServed.flag()
//            }
//            .listen(8888)
//            .onComplete(testContext.succeeding { httpServer ->
//                serverStarted.flag()
//
//                val client = vertx.createHttpClient()
//                for (i in 0 until 10) {
//                    client.request(HttpMethod.GET, 3000, "localhost", "/")
//                        .compose { req -> req.send().compose(HttpClientResponse::body) }
//                        .onComplete(testContext.succeeding { buffer ->
//                            testContext.verify {
//                                Assertions.assertEquals(buffer, "Ok")
//                                responsesReceived.flag()
//                            }
//                        })
//                }
//            })
//    }
}