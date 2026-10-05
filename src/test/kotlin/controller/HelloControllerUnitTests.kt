package es.unizar.webeng.hello.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.ui.Model
import org.springframework.ui.ExtendedModelMap

import es.unizar.webeng.hello.domain.GreetingLogRepository
import org.mockito.Mockito.mock

class HelloControllerUnitTests {
    private lateinit var controller: HelloController
    private lateinit var model: Model
    private val greetingController = GreetingController()
    private val greetingLogRepository = mock(GreetingLogRepository::class.java)
    @BeforeEach
    fun setup() {
        controller = HelloController(greetingController)
        model = ExtendedModelMap()
    }
    
    @Test
    fun `should return welcome view with default message`() {
        val view = controller.welcome(model, "", "en", "Europe/Madrid")        
        assertThat(view).isEqualTo("welcome")
        assertThat(model.getAttribute("message").toString()).contains("Student!")
        assertThat(model.getAttribute("name")).isEqualTo("")
    }
    
    @Test
    fun `should return welcome view with personalized message`() {
        val view = controller.welcome(model, "Developer", "en", "Europe/Madrid")        
        assertThat(view).isEqualTo("welcome")
        assertThat(model.getAttribute("message").toString()).contains("Developer!")        
        assertThat(model.getAttribute("name")).isEqualTo("Developer")
    }
    
    @Test
    fun `should return API response with timestamp`() {
        val apiController = HelloApiController(greetingController, greetingLogRepository)        
        val response = apiController.helloApi("Test", "en", "Europe/Madrid")        
        assertThat(response).containsKey("message")
        assertThat(response).containsKey("timestamp")
        assertThat(response["message"]).contains("Test!")
        assertThat(response["timestamp"]).isNotNull()
    }

    @Test
    fun `should return controlled error message for invalid timezone`() {
        // Prueba: Comprobamos que el try-catch de nuestro GreetingController funciona
        val apiController = HelloApiController(greetingController, greetingLogRepository)        
        val response = apiController.helloApi("Student", "es", "Mordor/Mount_Doom")
        
        assertThat(response["message"]).contains("Error: The timezone")
        assertThat(response["message"]).contains("is not valid")
    }

    @Test
    fun `should fallback to english if language is unknown`() {
        // Prueba: Si mandamos un idioma raro (ru = Ruso), debe usar el diccionario en inglés
        val apiController = HelloApiController(greetingController, greetingLogRepository)        
        val response = apiController.helloApi("Dani", "ru", "Europe/Madrid")
        
        // No sabemos si será morning, afternoon o night, pero sabemos que terminará con el nombre
        assertThat(response["message"]).endsWith("Dani!")
        // Y sabemos que no estará vacío
        assertThat(response["message"]).isNotBlank()
    }
}
