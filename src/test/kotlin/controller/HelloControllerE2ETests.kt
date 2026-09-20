package es.unizar.webeng.hello

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
// Los imports especiales que exige el IntegrationTest de tu profesor
import org.springframework.boot.resttestclient.TestRestTemplate
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class HelloControllerE2ETests {
    
    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun `E2E - should save greetings and support pagination`() {
        restTemplate.getForEntity("http://localhost:$port/api/hello?name=Dani", String::class.java)
        restTemplate.getForEntity("http://localhost:$port/api/hello?name=Pepe", String::class.java)
        restTemplate.getForEntity("http://localhost:$port/api/hello?name=Luis", String::class.java)

        val response = restTemplate.getForEntity("http://localhost:$port/history?page=0&size=2", String::class.java)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val body = response.body ?: ""
        assertThat(body).contains("\"size\" : 2")
        assertThat(body).contains("\"totalPages\"")
    }

    @Test
    fun `E2E - should aggregate greetings by language`() {
        restTemplate.getForEntity("http://localhost:$port/api/hello?language=it", String::class.java)
        val response = restTemplate.getForEntity("http://localhost:$port/api/stats/language", String::class.java)
        
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val body = response.body ?: ""
        assertThat(body).contains("it").contains("language")
    }

    @Test
    fun `E2E - should aggregate greetings by timezone`() {
        restTemplate.getForEntity("http://localhost:$port/api/hello?timezone=Asia/Tokyo", String::class.java)
        val response = restTemplate.getForEntity("http://localhost:$port/api/stats/timezone", String::class.java)
        
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val body = response.body ?: ""
        assertThat(body).contains("Asia/Tokyo").contains("timezone")
    }
}