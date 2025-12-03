package org.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class LlajtazoBackendApplication

fun main(args: Array<String>) {
    runApplication<LlajtazoBackendApplication>(*args)
}
