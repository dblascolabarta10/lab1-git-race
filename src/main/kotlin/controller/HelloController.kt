package es.unizar.webeng.hello.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalTime

@Controller
class HelloController(
        private val greetingController: GreetingController
) {
    
    @GetMapping("/")
    fun welcome(
        model: Model,
        @RequestParam(defaultValue = "") name: String,
        @RequestParam(defaultValue = "en") language: String
    ): String {
        val greeting = greetingController.getGreeting(name,language,LocalTime.now());
        model.addAttribute("message", greeting)
        model.addAttribute("name", name)
        return "welcome"
    }
}

@RestController
class HelloApiController (private val greetingController: GreetingController){

    @GetMapping("/api/hello", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun helloApi(@RequestParam(defaultValue = "World",) name: String, @RequestParam(defaultValue = "en") language: String): Map<String, String> {
        val greeting = greetingController.getGreeting(name, language)
        return mapOf(
            "message" to greeting,
            "timestamp" to java.time.Instant.now().toString()
        )
    }
}
