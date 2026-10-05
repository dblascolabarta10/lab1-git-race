package es.unizar.webeng.hello.controller

import es.unizar.webeng.hello.domain.GreetingLogRepository
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class HelloControllerMVCTests {

    private lateinit var mockMvc: MockMvc
    private val greetingController = GreetingController()
    
    // Creamos el mock de la base de datos a mano
    private val greetingLogRepository = mock(GreetingLogRepository::class.java)

    @BeforeEach
    fun setup() {
        // Levantamos los controladores en modo "Standalone" (aislados, sin levantar Spring Boot entero)
        val helloController = HelloController(greetingController)
        val helloApiController = HelloApiController(greetingController, greetingLogRepository)
        
        mockMvc = MockMvcBuilders.standaloneSetup(helloController, helloApiController).build()
    }

    @Test
    fun `should return home page with default message`() {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk)
            .andExpect(view().name("welcome"))
            .andExpect(model().attribute("message", endsWith("Student!")))
            .andExpect(model().attribute("name", equalTo("")))
    }
    
    @Test
    fun `should return home page with personalized message`() {
        mockMvc.perform(get("/").param("name", "Developer"))
            .andExpect(status().isOk)
            .andExpect(view().name("welcome"))
            .andExpect(model().attribute("message", endsWith("Developer!")))
            .andExpect(model().attribute("name", equalTo("Developer")))
    }
    
    @Test
    fun `should return API response as JSON`() {
        mockMvc.perform(get("/api/hello").param("name", "Test"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message", endsWith("Test!")))
            .andExpect(jsonPath("$.timestamp").exists())
    }

    @Test
    fun `should handle language and timezone parameters in API`() {
        mockMvc.perform(
            get("/api/hello")
                .param("name", "Viajero")
                .param("language", "it")
                .param("timezone", "Asia/Tokyo")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message", endsWith("Viajero!")))
            .andExpect(jsonPath("$.timestamp").exists())
    }
}