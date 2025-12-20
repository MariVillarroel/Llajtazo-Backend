package org.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories("org.example.src.repositories")
@EntityScan("org.example.src.models")

open class LlajtazoBackendApplication

fun main(args: Array<String>) {
    runApplication<LlajtazoBackendApplication>(*args)
}
