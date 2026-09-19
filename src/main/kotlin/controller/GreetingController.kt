package es.unizar.webeng.hello.controller

import org.springframework.stereotype.Service
import java.time.LocalTime
import java.time.ZoneId

/**
 * Indica a Spring que esta clase contiene la lógica de negocio principal. 
 */
@Service
class GreetingController {

    /**
     * Diccionario que agrupa los saludos organizados primero 
     * por el código del idioma y después por la franja horaria correspondiente.
     */
    private val translations = mapOf(
        "es" to mapOf("morning" to "Buenos días", "afternoon" to "Buenas tardes", "night" to "Buenas noches"),
        "en" to mapOf("morning" to "Good morning", "afternoon" to "Good afternoon", "night" to "Good night"),
        "fr" to mapOf("morning" to "Bonjour", "afternoon" to "Bon après-midi", "night" to "Bonne nuit"),
        "it" to mapOf("morning" to "Buongiorno", "afternoon" to "Buon pomeriggio", "night" to "Buonanotte"),
        "de" to mapOf("morning" to "Guten Morgen", "afternoon" to "Guten Tag", "night" to "Gute Nacht")
    )

    /**
     * Calcula el saludo cruzando el nombre, el idioma y la hora local obtenida a partir 
     * del huso horario. Define valores por defecto para que la aplicación no falle si 
     * el cliente omite algún parámetro en la petición.
     */
    fun getGreeting(name: String, language: String = "en", timezone: String = "Europe/Madrid"): String {
        
        // Comprueba si el nombre está en blanco para asignar el valor Student en su lugar
        val finalName = if (name.isNotBlank()) name else "Student"
        val langKey = language.lowercase()
        
        // Busca el idioma en el diccionario y usa el inglés como medida de seguridad si no existe
        val langMap = translations[langKey] ?: translations["en"]!!

        // Intenta convertir el texto a una zona horaria real de Java atrapando posibles fallos 
        // para devolver un error controlado en lugar de tumbar el servidor
        val zoneId = try {
            ZoneId.of(timezone)
        } catch (e: Exception) {
            return "Error: The timezone '$timezone' is not valid. Try 'Europe/Madrid' or 'Asia/Tokyo'."
        }

        // Consulta la hora exacta en la parte del mundo solicitada
        val time = LocalTime.now(zoneId)

        // Clasifica la hora actual en una de las tres franjas del día definidas en el diccionario
        val timeOfDay = when (time.hour) {
            in 6..11 -> "morning"
            in 12..20 -> "afternoon"
            else -> "night"
        }

        // Extrae la traducción exacta del mapa y la une con el nombre del usuario
        val greetingPrefix = langMap[timeOfDay]
        return "$greetingPrefix, $finalName!"
    }
}