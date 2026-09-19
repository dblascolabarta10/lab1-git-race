package es.unizar.webeng.hello.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalTime


/**
 * @author Daniel Blasco Labarta
 * Opciones de idioma (language) soportadas actualmente:
 * - "en" (Inglés, por defecto)
 * - "es" (Español)
 * - "fr" (Francés)
 * - "it" (Italiano)
 * - "de" (Alemán)
 * 
 * Opciones de zona horaria (timezone) - Ejemplos de la base de datos IANA:
 * - "Europe/Madrid" (España peninsular, por defecto)
 * - "Asia/Tokyo" (Japón)
 * - "America/New_York" (EE. UU. - Costa Este)
 * - "Australia/Sydney" (Australia)
 */
@Controller
class HelloController(
        private val greetingController: GreetingController
) {
    
    @GetMapping("/")
    fun welcome(
        model: Model,
        @RequestParam(defaultValue = "") name: String,
        // Añadimos el idioma y la franja horaria
        @RequestParam(defaultValue = "en") language: String,
        @RequestParam(defaultValue = "Europe/Madrid") timezone: String
    ): String {
        val greeting = greetingController.getGreeting(name,language,timezone);
        model.addAttribute("message", greeting)
        model.addAttribute("name", name)
        return "welcome"
    }
}

/**
 * @author Daniel Blasco Labarta
 */
@RestController
class HelloApiController (private val greetingController: GreetingController){
    /**
     * Responde a peticiones GET en la ruta `/api/hello`.
     *
     * @param name Nombre a incluir en el saludo (por defecto "World").
     * @param language Idioma del saludo (por defecto "en").
     * @param timezone Huso horario para calcular el momento del día (por defecto "Europe/Madrid").
     * @return Un mapa clave-valor que Spring Boot convierte automáticamente a JSON.
     */

    @GetMapping("/api/hello", produces = [MediaType.APPLICATION_JSON_VALUE])
    // Valores por defecto
    fun helloApi(@RequestParam(defaultValue = "World",) name: String, 
                @RequestParam(defaultValue = "en") language: String,
                @RequestParam(defaultValue = "Europe/Madrid") timezone: String): Map<String, String> {
        val greeting = greetingController.getGreeting(name, language,timezone)
        return mapOf(
            "message" to greeting,
            "timestamp" to java.time.Instant.now().toString()
        )
    }
}
