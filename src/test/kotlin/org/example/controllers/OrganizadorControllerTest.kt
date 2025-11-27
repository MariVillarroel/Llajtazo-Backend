package org.example.controllers
import org.example.LlajtazoBackendApplication
import org.example.src.models.Organizador
import org.example.src.repositories.OrganizadorRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest(classes = [LlajtazoBackendApplication::class])
@AutoConfigureMockMvc
class OrganizadorControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var repository: OrganizadorRepository

    @Test
    fun `crear organizador`() {
        val json = """
            {
              "username": "mariana",
              "correo": "mariana@eventos.com",
              "password": "1234",
              "profilePic": "pic.png",
              "nombreOrg": "Eventos Mariana",
              "numero": "7654321"
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/organizadores")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("mariana"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.role").value("organizador"))
    }

    @Test
    fun `eliminar organizador`() {
        // Arrange: crear organizador en BD
        val organizador = Organizador(
            username = "juan",
            correo = "juan@eventos.com",
            password = "1234",
            profile_pic = "pic.png",
            nombre_org = "Eventos Juan",
            numero = "123456"
        )
        val saved = repository.save(organizador)

        // Act: eliminar
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/organizadores/${saved.id}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNoContent)

        // Assert: verificar que ya no existe
        Assertions.assertFalse(repository.existsById(saved.id))
    }

    @Test
    fun `actualizar organizador`() {
        // Arrange: crear organizador en BD
        val organizador = Organizador(
            username = "ana",
            correo = "ana@eventos.com",
            password = "1234",
            profile_pic = "pic.png",
            nombre_org = "Eventos Ana",
            numero = "111111"
        )
        val saved = repository.save(organizador)

        val jsonUpdate = """
            {
              "username": "ana_actualizada",
              "correo": "ana@eventos.com",
              "password": "1234",
              "profilePic": "pic.png",
              "nombreOrg": "Eventos Ana Updated",
              "numero": "222222"
            }
        """.trimIndent()

        // Act: actualizar
        mockMvc.perform(
            MockMvcRequestBuilders.put("/organizadores/${saved.id}")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonUpdate))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("ana_actualizada"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.nombreOrg").value("Eventos Ana Updated"))

        // Assert: verificar en BD
        val updated = repository.findById(saved.id).get()
        Assertions.assertEquals("ana_actualizada", updated.username)
        Assertions.assertEquals("Eventos Ana Updated", updated.nombre_org)
    }
}