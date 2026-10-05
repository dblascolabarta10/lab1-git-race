package es.unizar.webeng.hello.controller

import es.unizar.webeng.hello.domain.GreetingLog
import es.unizar.webeng.hello.domain.GreetingLogRepository
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
class HelloApiController (private val greetingController: GreetingController,private val greetingLogRepository: GreetingLogRepository){
    /**
     * Responde a peticiones GET en la ruta `/api/hello`.
     * Además, persiste un registro del saludo en la base de datos relacional.
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

        val log = GreetingLog(name = name, language = language, timezone = timezone)
        greetingLogRepository.save(log)
        return mapOf(
            "message" to greeting,
            "timestamp" to java.time.Instant.now().toString()
        )
    }
    /**
     * Endpoint. Devuelve el número total de saludos agrupados por idioma.
     */
    @GetMapping("/api/stats/language", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getLanguageStats(): List<es.unizar.webeng.hello.domain.LanguageStats> {
        return greetingLogRepository.countGreetingsByLanguage()
    }
    /**
     * Endpoint. Devuelve el número total de saludos agrupados por zona horaria.
     */
    @GetMapping("/api/stats/timezone", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getTimezoneStats(): List<es.unizar.webeng.hello.domain.TimezoneStats> {
        return greetingLogRepository.countGreetingsByTimezone()
    }
}
