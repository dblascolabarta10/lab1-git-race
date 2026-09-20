package es.unizar.webeng.hello.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime

/**
 * Entidad que representa la tabla en la base de datos H2.
 * Guarda el registro de cada saludo generado.
 */
@Entity
class GreetingLog(
    var name: String = "",       
    var language: String = "",   
    var timezone: String = "",
    var timestamp: LocalDateTime = LocalDateTime.now(),
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
)