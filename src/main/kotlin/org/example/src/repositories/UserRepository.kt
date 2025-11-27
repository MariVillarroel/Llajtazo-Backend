package org.example.src.repositories

import org.example.config.DatabaseConfig
import org.example.src.models.Asistente
import org.example.src.models.Categoria
import org.example.models.User
import java.sql.Statement

open class UserRepository {
    open fun findByEmail(email: String): User? {
        val sql = """
            SELECT 
                u.id,
                u.email,
                u.nombre_completo,
                u.password_hash,
                u.avatar_url,
                c.id AS categoria_id,
                c.nombre AS categoria_nombre
            FROM usuarios u
            JOIN intereses_usuarios iu ON iu.usuario_id = u.id
            JOIN categorias c ON c.id = iu.categorias_id
            WHERE u.email = ?
        """.trimIndent()

        DatabaseConfig.getConnection().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, email)
                val rs = stmt.executeQuery()

                var id: Int? = null
                var correo: String? = null
                var username: String? = null
                var passwordHash: String? = null
                var profilePic: String? = null
                val categorias = mutableListOf<Categoria>()

                while (rs.next()) {
                    if (id == null) {
                        id = rs.getInt("id")
                        correo = rs.getString("email")
                        username = rs.getString("nombre_completo")
                        passwordHash = rs.getString("password_hash")
                        profilePic = rs.getString("avatar_url")
                    }
                    categorias.add(
                        Categoria(
                            id = rs.getInt("categoria_id"),
                            nombre = rs.getString("categoria_nombre")
                        )
                    )
                }

                return if (id != null) {
                    Asistente(
                        id = id,
                        correo = correo!!,
                        username = username!!,
                        passwordHash = passwordHash!!,
                        profilePic = profilePic,
                        tags = categorias
                    )
                } else null
            }
        }
    }

    open fun create(user: Asistente): Int {
        val insertUserSql = """
        INSERT INTO usuarios (email, nombre_completo, password_hash, avatar_url, created_at)
        VALUES (?, ?, ?, ?, NOW())
    """.trimIndent()

        val insertInteresSql = """
        INSERT INTO intereses_usuarios (usuario_id, categorias_id)
        VALUES (?, ?)
    """.trimIndent()

        DatabaseConfig.getConnection().use { conn ->
            conn.autoCommit = false

            try {
                val userId = conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS).use { stmt ->
                    stmt.setString(1, user.correo)
                    stmt.setString(2, user.username)
                    stmt.setString(3, user.passwordHash)
                    stmt.setString(4, user.profilePic)

                    val affected = stmt.executeUpdate()
                    if (affected == 0) throw IllegalStateException("No se insertó el usuario")

                    stmt.generatedKeys.use { keys ->
                        if (keys.next()) keys.getInt(1)
                        else throw IllegalStateException("No se obtuvo el ID generado del usuario")
                    }
                }

                if (user.tags.isNotEmpty()) {
                    conn.prepareStatement(insertInteresSql).use { stmtInteres ->
                        for (cat in user.tags) {
                            require(cat.id != 0) { "Categoria '${cat.nombre}' no tiene id válido (id=0). Valida antes de crear." }
                            stmtInteres.setInt(1, userId)
                            stmtInteres.setInt(2, cat.id)
                            stmtInteres.addBatch()
                        }
                        stmtInteres.executeBatch()
                    }
                }

                conn.commit()
                return userId
            } catch (ex: Exception) {
                conn.rollback()
                throw ex
            } finally {
                conn.autoCommit = true
            }
        }
    }
}