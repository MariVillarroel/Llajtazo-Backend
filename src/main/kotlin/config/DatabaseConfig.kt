package org.example.config

import java.sql.Connection
import java.sql.DriverManager

object DatabaseConfig {

    private const val URL = "jdbc:mysql://localhost:3306/llajtazo"
    private const val USER = "root"
    private const val PASSWORD = "12345"

    fun getConnection(): Connection =
        DriverManager.getConnection(URL, USER, PASSWORD)
}