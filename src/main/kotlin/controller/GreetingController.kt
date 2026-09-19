package es.unizar.webeng.hello.controller

import java.time.LocalTime
import org.springframework.stereotype.Service
@Service
class `GreetingController` {
    fun getGreeting(name: String, language: String = "en",time: LocalTime = LocalTime.now()): String {

        val esEsp = language.lowercase() == "es"
        val finalName = if (name.isNotBlank()) {
            name
        } else {
            "Student" // o el nombre por defecto que quieras ponerle
        }
        if(esEsp) {
            if (time.hour>6 && time.hour<12) {
                return "¡Buenos días, $finalName!"
            }
            else if (12<=time.hour && time.hour <= 21) {
                return "¡Buenas tardes, $finalName!"
            }
            return "¡Buenas noches $finalName!"
        }
        else {
            if (time.hour > 6 && time.hour < 12) {
                return "Good morning, $finalName!"
            } else if (12 <= time.hour && time.hour <= 21) {
                return "Good afternoon, $finalName!"
            }
            return "Good night $finalName!"
        }
    }
}