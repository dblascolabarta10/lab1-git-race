package es.unizar.webeng.hello.domain

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource

/**
 * Interfaz para devolver el resultado de la agregación por idioma.
 */
interface LanguageStats {
    val language: String
    val count: Long
}

/**
 * Interfaz para devolver el resultado de la agregación por zona horaria.
 */
interface TimezoneStats {
    val timezone: String
    val count: Long
}

/**
 * PagingAndSortingRepository da soporte de paginación.
 * @RepositoryRestResource expone automáticamente esto como una API REST en la ruta /history.
 */
@RepositoryRestResource(path = "history")
interface GreetingLogRepository : PagingAndSortingRepository<GreetingLog, Long>, CrudRepository<GreetingLog, Long> {
    
    // Cuenta cuántos saludos se han hecho por idioma
    @RestResource(exported = false) // Evita que Spring Data REST colapse
    @Query("SELECT g.language as language, COUNT(g) as count FROM GreetingLog g GROUP BY g.language")
    fun countGreetingsByLanguage(): List<LanguageStats>

    // Cuenta cuántos saludos se han hecho por zona horaria
    @RestResource(exported = false) // Evita que Spring Data REST colapse
    @Query("SELECT g.timezone as timezone, COUNT(g) as count FROM GreetingLog g GROUP BY g.timezone")
    fun countGreetingsByTimezone(): List<TimezoneStats>
}