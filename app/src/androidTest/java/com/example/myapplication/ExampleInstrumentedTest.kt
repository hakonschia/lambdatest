package com.example.myapplication

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.http.GET
import java.net.InetAddress

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class 
ExampleInstrumentedTest {

    @Test
    fun useAppContext() {
        val server = MockWebServer()
        server.start(InetAddress.getByName("localhost.lambdatest.com"), 8100)
        println("/something URL: ${server.url("/something")}")

        server.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                println("MockWebServer dispatching ${request.requestUrl} ${server.url("/something")}")

                return when (request.path) {
                    "/something" -> MockResponse().setResponseCode(200).setBody("{ \"test\": \"hello\" }")
                    else -> MockResponse().setResponseCode(404)
                }
            }
        }

        val api = Retrofit.Builder()
            .baseUrl("http://localhost.lambdatest.com:8100")
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(Api::class.java)

        val something = runBlocking { api.getSomething() }

        assert(something.test == "hello")
    }
}

private interface Api {

    @GET("/something")
    suspend fun getSomething(): Something
}

@Serializable
data class Something(
    val test: String
)
