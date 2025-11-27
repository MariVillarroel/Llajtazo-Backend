[1mdiff --git a/.idea/gradle.xml b/.idea/gradle.xml[m
[1mindex 2a65317..9d62f93 100644[m
[1m--- a/.idea/gradle.xml[m
[1m+++ b/.idea/gradle.xml[m
[36m@@ -1,16 +1,10 @@[m
 <?xml version="1.0" encoding="UTF-8"?>[m
 <project version="4">[m
[31m-  <component name="GradleMigrationSettings" migrationVersion="1" />[m
   <component name="GradleSettings">[m
     <option name="linkedExternalProjectsSettings">[m
       <GradleProjectSettings>[m
         <option name="externalProjectPath" value="$PROJECT_DIR$" />[m
         <option name="gradleHome" value="" />[m
[31m-        <option name="modules">[m
[31m-          <set>[m
[31m-            <option value="$PROJECT_DIR$" />[m
[31m-          </set>[m
[31m-        </option>[m
       </GradleProjectSettings>[m
     </option>[m
   </component>[m
[1mdiff --git a/.idea/misc.xml b/.idea/misc.xml[m
[1mindex 5c3809d..d5d5cd9 100644[m
[1m--- a/.idea/misc.xml[m
[1m+++ b/.idea/misc.xml[m
[36m@@ -1,7 +1,7 @@[m
 <?xml version="1.0" encoding="UTF-8"?>[m
 <project version="4">[m
   <component name="ExternalStorageConfigurationManager" enabled="true" />[m
[31m-  <component name="ProjectRootManager" version="2" languageLevel="JDK_17" project-jdk-name="openjdk-24" project-jdk-type="JavaSDK">[m
[32m+[m[32m  <component name="ProjectRootManager" version="2" languageLevel="JDK_24" default="true" project-jdk-name="openjdk-24" project-jdk-type="JavaSDK">[m
     <output url="file://$PROJECT_DIR$/out" />[m
   </component>[m
 </project>[m
\ No newline at end of file[m
[1mdiff --git a/build.gradle.kts b/build.gradle.kts[m
[1mindex 8ae178a..9ac1829 100644[m
[1m--- a/build.gradle.kts[m
[1m+++ b/build.gradle.kts[m
[36m@@ -1,16 +1,9 @@[m
 plugins {[m
     kotlin("jvm") version "2.2.0"[m
[31m-    kotlin("plugin.spring") version "2.2.0"[m
[31m-    id("org.springframework.boot") version "3.2.0"[m
[31m-    id("io.spring.dependency-management") version "1.1.4"[m
[31m-[m
[31m-[m
 }[m
 [m
[31m-group = "org.llajtazo"[m
[31m-version = "1.0.0"[m
[31m-java.sourceCompatibility = JavaVersion.VERSION_17[m
[31m-[m
[32m+[m[32mgroup = "org.example"[m
[32m+[m[32mversion = "1.0-SNAPSHOT"[m
 [m
 repositories {[m
     mavenCentral()[m
[36m@@ -18,26 +11,11 @@[m [mrepositories {[m
 [m
 dependencies {[m
     testImplementation(kotlin("test"))[m
[31m-    // Spring Boot core[m
[31m-    implementation("org.springframework.boot:spring-boot-starter-web")[m
[31m-    implementation("org.springframework.boot:spring-boot-starter-data-jpa")[m
[31m-    implementation("org.springframework.boot:spring-boot-starter-validation")[m
[31m-    // MySQL connector[m
[31m-    runtimeOnly("com.mysql:mysql-connector-j")[m
[31m-[m
[31m-    runtimeOnly("com.h2database:h2")[m
[31m-[m
[31m-    // Kotlin support[m
[31m-    implementation("org.jetbrains.kotlin:kotlin-reflect")[m
[31m-    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")[m
[31m-[m
[31m-    // Testing[m
[31m-    testImplementation("org.springframework.boot:spring-boot-starter-test")[m
 }[m
 [m
 tasks.test {[m
     useJUnitPlatform()[m
 }[m
 kotlin {[m
[31m-    jvmToolchain(17)[m
[32m+[m[32m    jvmToolchain(24)[m
 }[m
\ No newline at end of file[m
[1mdiff --git a/src/main/kotlin/Main.kt b/src/main/kotlin/Main.kt[m
[1mnew file mode 100644[m
[1mindex 0000000..16a7cde[m
[1m--- /dev/null[m
[1m+++ b/src/main/kotlin/Main.kt[m
[36m@@ -0,0 +1,16 @@[m
[32m+[m[32mpackage org.example[m
[32m+[m
[32m+[m[32m//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or[m
[32m+[m[32m// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.[m
[32m+[m[32mfun main() {[m
[32m+[m[32m    val name = "Kotlin"[m
[32m+[m[32m    //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text[m
[32m+[m[32m    // to see how IntelliJ IDEA suggests fixing it.[m
[32m+[m[32m    println("Hello, " + name + "!")[m
[32m+[m
[32m+[m[32m    for (i in 1..5) {[m
[32m+[m[32m        //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint[m
[32m+[m[32m        // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.[m
[32m+[m[32m        println("i = $i")[m
[32m+[m[32m    }[m
[32m+[m[32m}[m
\ No newline at end of file[m
[1mdiff --git a/src/main/kotlin/org/example/LlajtazoBackendApplication.kt b/src/main/kotlin/org/example/LlajtazoBackendApplication.kt[m
[1mdeleted file mode 100644[m
[1mindex 33d1537..0000000[m
[1m--- a/src/main/kotlin/org/example/LlajtazoBackendApplication.kt[m
[1m+++ /dev/null[m
[36m@@ -1,11 +0,0 @@[m
[31m-package org.example[m
[31m-[m
[31m-import org.springframework.boot.autoconfigure.SpringBootApplication[m
[31m-import org.springframework.boot.runApplication[m
[31m-[m
[31m-@SpringBootApplication[m
[31m-class LlajtazoBackendApplication[m
[31m-[m
[31m-fun main(args: Array<String>) {[m
[31m-    runApplication<LlajtazoBackendApplication>(*args)[m
[31m-}[m
\ No newline at end of file[m
[1mdiff --git a/src/main/kotlin/org/example/src/controllers/OrganizadorController.kt b/src/main/kotlin/org/example/src/controllers/OrganizadorController.kt[m
[1mdeleted file mode 100644[m
[1mindex b344f7f..0000000[m
[1m--- a/src/main/kotlin/org/example/src/controllers/OrganizadorController.kt[m
[1m+++ /dev/null[m
[36m@@ -1,63 +0,0 @@[m
[31m-package org.example.src.controllers[m
[31m-[m
[31m-import org.example.src.dto.OrganizadorRequest[m
[31m-import org.example.src.dto.OrganizadorResponse[m
[31m-import org.example.src.dto.UpdateOrganizadorRequest[m
[31m-import org.example.src.services.OrganizadorService[m
[31m-import org.springframework.http.HttpStatus[m
[31m-import org.springframework.http.ResponseEntity[m
[31m-import org.springframework.web.bind.annotation.*[m
[31m-[m
[31m-@RestController[m
[31m-@RequestMapping("/organizadores")[m
[31m-class OrganizadorController(private val service: OrganizadorService) {[m
[31m-[m
[31m-    @PostMapping[m
[31m-    fun crearOrganizador(@RequestBody request: OrganizadorRequest): ResponseEntity<Any> {[m
[31m-        return try {[m
[31m-            val response = service.crearOrganizador(request)[m
[31m-            ResponseEntity.status(HttpStatus.CREATED).body(response)[m
[31m-        } catch (e: IllegalArgumentException) {[m
[31m-            ResponseEntity.badRequest().body(mapOf("error" to e.message))[m
[31m-        }[m
[31m-    }[m
[31m-[m
[31m-    @GetMapping[m
[31m-    fun listarOrganizadores(): ResponseEntity<List<OrganizadorResponse>> {[m
[31m-        val lista = service.listarOrganizadores()[m
[31m-        return ResponseEntity.ok(lista)[m
[31m-    }[m
[31m-[m
[31m-    @GetMapping("/{id}")[m
[31m-    fun obtenerOrganizadorPorId(@PathVariable id: Int): ResponseEntity<Any> {[m
[31m-        return try {[m
[31m-            val organizador = service.obtenerOrganizadorPorId(id)[m
[31m-            if (organizador != null) ResponseEntity.ok(organizador)[m
[31m-            else ResponseEntity.notFound().build()[m
[31m-        } catch (e: Exception) {[m
[31m-            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)[m
[31m-                .body(mapOf("error" to "Error al obtener organizador"))[m
[31m-        }[m
[31m-    }[m
[31m-[m
[31m-    @PutMapping("/{id}")[m
[31m-    fun actualizarOrganizador([m
[31m-        @PathVariable id: Int,[m
[31m-        @RequestBody request: UpdateOrganizadorRequest  // ← Cambiado aquí[m
[31m-    ): ResponseEntity<Any> {[m
[31m-        return try {[m
[31m-            val response = service.actualizarOrganizador(id, request)[m
[31m-            ResponseEntity.ok(response)[m
[31m-        } catch (e: NoSuchElementException) {[m
[31m-            ResponseEntity.notFound().build()[m
[31m-        } catch (e: IllegalArgumentException) {[m
[31m-            ResponseEntity.badRequest().body(mapOf("error" to e.message))[m
[31m-        }[m
[31m-    }[m
[31m-[m
[31m-    @DeleteMapping("/{id}")[m
[31m-    fun eliminarOrganizador(@PathVariable id: Int): ResponseEntity<Void> {[m
[31m-        return if (service.eliminarOrganizador(id)) ResponseEntity.noContent().build()[m
[31m-        else ResponseEntity.notFound().build()[m
[31m-    }[m
[31m-}[m
\ No newline at end of file[m
[1mdiff --git a/src/main/kotlin/org/example/src/dto/OrganizadorRequest.kt b/src/main/kotlin/org/example/src/dto/OrganizadorRequest.kt[m
[1mdeleted file mode 100644[m
[1mindex 4302dfa..0000000[m
[1m--- a/src/main/kotlin/org/example/src/dto/OrganizadorRequest.kt[m
[1m+++ /dev/null[m
[36m@@ -1,29 +0,0 @@[m
[31m-package org.example.src.dto[m
[31m-[m
[31m-import jakarta.validation.constraints.Email[m
[31m-import jakarta.validation.constraints.NotBlank[m
[31m-import jakarta.validation.constraints.Size[m
[31m-[m
[31m-data class OrganizadorRequest([m
[31m-    @field:NotBlank(message = "Username es requerido")[m
[31m-    @field:Size(min = 3, max = 50, message = "Username debe tener entre 3 y 50 caracteres")[m
[31m-    val username: String,[m
[31m-[m
[31m-    @field:NotBlank(message = "Correo es requerido")[m
[31m-    @field:Email(message = "Formato de correo inválido")[m
[31m-    val correo: String,[m
[31m-[m
[31m-    @field:NotBlank(message = "Password es requerido")[m
[31m-    @field:Size(min = 6, message = "Password debe tener al menos 6 caracteres")[m
[31m-    val password: String,[m
[31m-[m
[31m-    val profilePic: String = "",[m
[31m-[m
[31m-    @field:NotBlank(message = "Nombre de organización es requerido")[m
[31m-    @field:Size(max = 100, message = "Nombre de organización no puede exceder 100 caracteres")[m
[31m-    val nombreOrg: String,[m
[31m-[m
[31m-    @field:NotBlank(message = "Número es requerido")[m
[31m-    @field:Size(min = 8, max = 15, message = "Número debe tener entre 8 y 15 caracteres")[m
[31m-    val numero: String[m
[31m-)[m
\ No newline at end of file[m
[1mdiff --git a/src/main/kotlin/org/example/src/dto/OrganizadorResponse.kt b/src/main/kotlin/org/example/src/dto/OrganizadorResponse.kt[m
[1mdeleted file mode 100644[m
[1mindex 489a891..0000000[m
[1m--- a/src/main/kotlin/org/example/src/dto/OrganizadorResponse.kt[m
[1m+++ /dev/null[m
[36m@@ -1,37 +0,0 @@[m
[31m-package org.example.src.dto[m
[31m-[m
[31m-import org.example.src.models.Organizador[m
[31m-[m
[31m-data class OrganizadorResponse([m
[31m-    val id: Int,[m
[31m-    val username: String,[m
[31m-    val correo: String,[m
[31m-    val profilePic: String,[m
[31m-    val nombreOrg: String,[m
[31m-    val numero: String,[m
[31m-    val role: String,[m
[31m-    val totalSeguidores: Int,[m
[31m-    val totalEventos: Int,[m
[31m-    val fechaCreacion: String,[m
[31m-    val fechaActualizacion: String,[m
[31m-    val seguidoresIds: List<Int>  // Nuevo campo: IDs de los asistentes seguidores[m
[31m-) {[m
[31m-    companion object {[m
[31m-        fun fromEntity(organizador: Organizador): OrganizadorResponse {[m
[31m-            return OrganizadorResponse([m
[31m-                id = organizador.id,[m
[31m-                username = organizador.username,[m
[31m-                correo = organizador.correo,[m
[31m-                profilePic = organizador.profile_pic,[m
[31m-                nombreOrg = organizador.nombre_org,[m
[31m-                numero = organizador.numero,[m
[31m-                role = organizador.get_Role(),[m
[31m-                totalSeguidores = organizador.totalSeguidores(),[m
[31m-                totalEventos = organizador.totalEventos(),[m
[31m-                fechaCreacion = organizador.fechaCreacion.toString(),[m
[31m-                fechaActualizacion = organizador.fechaActualizacion.toString(),[m
[31m-                seguidoresIds = organizador.obtenerIdsSeguidores()  // Usa el nuevo método[m
[31m-            )[m
[31m-        }[m
[31m-    }[m
[31m-}[m
\ No newline at end of file[m
[1mdiff --git a/src/main/kotlin/org/example/src/dto/UpdateOrganizadorRequest.kt b/src/main/kotlin/org/example/src/dto/UpdateOrganizadorRequest.kt[m
[1mdeleted file mode 100644[m
[1mindex 8c12437..0000000[m
[1m--- a/src/main/kotlin/org/example/src/dto/UpdateOrganizadorRequest.kt[m
[1m+++ /dev/null[m
[36m@@ -1,10 +0,0 @@[m
[31m-package org.example.src.dto[m
[31m-[m
[31m-data class UpdateOrganizadorRequest([m
[31m-    val username: String? = null,[m
[31m-    val correo: String? = null,[m
[31m-    val password: String? = null,[m
[31m-    val profilePic: String? = null,[m
[31m-    val nombreOrg: String? = null,[m
[31m-    val numero: String? = null[m
[31m-)[m
\ No newline at end of file[m
[1mdiff --git a/src/main/kotlin/org/example/src/models/CreadorUser.kt b/src/main/kotlin/org/example/src/models/CreadorUser.kt[m
[1mdeleted file mode 100644[m
[1mindex 19e3ca7..0000000[m
[1m--- a/src/main/kotlin/org/example/src/models/CreadorUser.kt[m
[1m+++ /dev/null[m
[36m@@ -1,28 +0,0 @@[m
[31m-package org.example.src.models[m
[31m-[m
[31m-class CreadorUser {[m
[31m-[m
[31m-    fun crearUser([m
[31m-        tipo: String,[m
[31m-        username: String,[m
[31m-        correo: String,[m
[31m-        password: String,[m
[31m-        profile_pic: String = "",[m
[31m-        nombre_org: String = "",[m
[31m-        numero: String = "",[m
[31m-        followers: Int = 0[m
[31m-    ): User {[m
[31m-        return when (tipo.lowercase()) {[m
[31m-            "organizador" -> Organizador([m
[31m-                username = username,[m
[31m-                correo = correo,[m
[31m-                password = password,[m
[31m-                profile_pic = profile_pic,[m
[31m-                nombre_org = nombre_org,[m
[31m-                numero = numero,[m
[31m-                //followers = followers[m
[31m-            )[m
[31m-            else -> throw IllegalArgumentException("Tipo de usuario no soportado: $tipo")[m
[31m-        }[m
[31m-    }[m
[31m-}[m
\ No newline at end of file[m
[1mdiff --git a/src/main/kotlin/org/example/src/models/Evento.kt b/src/main/kotlin/org/example/src/models/Evento.kt[m
[1mdeleted file mode 100644[m
[1mindex 13be28a..0000000[m
[1m--- a/src/main/kotlin/org/example/src/models/Evento.kt[m
[1m+++ /dev/null[m
[36m@@ -1,26 +0,0 @@[m
[31m-package org.example.src.models[m
[31m-[m
[31m-import jakarta.persistence.GeneratedValue[m
[31m-import jakarta.persistence.GenerationType[m
[31m-import jakarta.persistence.Id[m
[31m-import jakarta.persistence.MappedSuperclass[m
[31m-import java.time.LocalDate[m
[31m-import java.time.LocalTime[m
[31m-@MappedSuperclass[m
[31m-public abstract class Evento {[m
[31m-[m
[31m-    @Id[m
[31m-    @GeneratedValue(strategy = GenerationType.IDENTITY)[m
[31m-    open val id_evento: Int = 0[m
[31m-    abstract val organizador: Organizador[m
[31m-    abstract val fecha: LocalDate[m
[31m-    abstract val hora: LocalTime[m
[31m-    //val comentarios: MutableList<Comentario>[m
[31m-    abstract val lugar: Location[m
[31m-   // val tags: List<Categoria>[m
[31m-[m
[31m-    abstract fun getInfo(): String[m
[31m-    //fun agregarComentario(comentario: Comentario)[m
[31m-    abstract fun mostrarUbicacion(): String[m
[31m-    abstract fun esEventoFuturo(): Boolean[m
[31m-}[m
\ No newline at end of file[m
[1mdiff --git a/src/main/kotlin/org/example/src/models/Location.kt b/src/main/kotlin/org/example/src/models/Location.kt[m
[1mdeleted file mode 100644[m
[1mindex c926021..0000000[m
[1m--- a/src/main/kotlin/org/example/src/models/Location.kt[m
[1m+++ /dev/null[m
[36m@@ -1,11 +0,0 @@[m
[31m-package org.example.src.models[m
[31m-[m
[31m-import jakarta.persistence.Embeddable[m
[31m-[m
[31m-@Embeddable[m
[31m-data class Location ([m
[31m-    val id_location: Int,[m
[31m-    val name: String,[m
[31m-    val latitude: Double? = null,[m
[31m-    val altitude: Double? = null[m
[31m-)[m
\ No newline at end of file[m
[1mdiff --git a/src/main/kotlin/org/example/src/models/Organizador.kt b/src/main/kotlin/org/example/src/models/Organizador.kt[m
[1mdeleted file mode 100644[m
[1mindex 8a205bc..0000000[m
[1m--- a/src/main/kotlin/org/example/src/models/Organizador.kt[m
[1m+++ /dev/null[m
[36m@@ -1,127 +0,0 @@[m
[31m-package org.example.src.models[m
[31m-[m
[31m-import jakarta.persistence.*[m
[31m-import kotlin.collections.map[m
[31m-import kotlin.collections.toList[m
[31m-[m
[31m-@Entity[m
[31m-@Table(name = "organizadores")[m
[31m-class Organizador([m
[31m-    @Id[m
[31m-    @GeneratedValue(strategy = GenerationType.IDENTITY)[m
[31m-    final override var id: Int = 0,[m
[31m-[m
[31m-    @Column(nullable = false, unique = true)[m
[31m-    final override var username: String,[m
[31m-[m
[31m-    @Column(nullable = false, unique = true)[m
[31m-    final override var correo: String,[m
[31m-[m
[31m-    @Column(nullable = false)[m
[31m-    final override var password: String,[m
[31m-[m
[31m-    @Column(name = "profile_pic")[m
[31m-    final override var profile_pic: String = "",[m
[31m-[m
[31m-    @Column(name = "nombre_org", nullable = false)[m
[31m-    var nombre_org: String,[m
[31m-[m
[31m-    @Column(nullable = false)[m
[31m-    var numero: String[m
[31m-[m
[31m-) : User() {[m
[31m-[m
[31m-    // Relación con eventos - LAZY para evitar problemas de carga[m
[31m-    @OneToMany(mappedBy = "organizador", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)[m
[31m-    var eventosCreados: MutableList<Evento> = mutableListOf()[m
[31m-[m
[31m-    // Seguidores - relación ManyToMany con ASISTENTES[m
[31m-    @ManyToMany(fetch = FetchType.LAZY)[m
[31m-    @JoinTable([m
[31m-        name = "organizador_seguidores",[m
[31m-        joinColumns = [JoinColumn(name = "organizador_id")],[m
[31m-        inverseJoinColumns = [JoinColumn(name = "asistente_id")]  // Cambiado a asistente_id[m
[31m-    )[m
[31m-    var followers: MutableList<Asistente> = mutableListOf()[m
[31m-[m
[31m-    @Column(name = "fecha_actualizacion")[m
[31m-    var fechaActualizacion: java.time.LocalDateTime = java.time.LocalDateTime.now()[m
[31m-[m
[31m-    override fun get_Role(): String = "organizador"[m
[31m-[m
[31m-    fun crearEvento(evento: Evento) {[m
[31m-        eventosCreados.add(evento)[m
[31m-        actualizarFecha()[m
[31m-    }[m
[31m-[m
[31m-    fun deleteEvento(evento: Evento) {[m
[31m-        eventosCreados.remove(evento)[m
[31m-        actualizarFecha()[m
[31m-    }[m
[31m-[m
[31m-    fun listarEventos(): List<Evento> = eventosCreados.toList()[m
[31m-[m
[31m-    // ✅ CORRECCIÓN: Método que actualiza la instancia actual[m
[31m-    fun actualizarPerfil([m
[31m-        nuevoUsername: String? = null,[m
[31m-        nuevoCorreo: String? = null,[m
[31m-        nuevoPassword: String? = null,[m
[31m-        nuevaProfilePic: String? = null,[m
[31m-        nuevoNombreOrg: String? = null,[m
[31m-        nuevoNumero: String? = null[m
[31m-    ) {[m
[31m-        nuevoUsername?.let { this.username = it }[m
[31m-        nuevoCorreo?.let { this.correo = it }[m
[31m-        nuevoPassword?.let { this.password = it }[m
[31m-        nuevaProfilePic?.let { this.profile_pic = it }[m
[31m-        nuevoNombreOrg?.let { this.nombre_org = it }[m
[31m-        nuevoNumero?.let { this.numero = it }[m
[31m-[m
[31m-        actualizarFecha()[m
[31m-    }[m
[31m-[m
[31m-    private fun actualizarFecha() {[m
[31m-        this.fechaActualizacion = java.time.LocalDateTime.now()[m
[31m-    }[m
[31m-[m
[31m-    // ✅ MÉTODOS CON ASISTENTE (no Usuario)[m
[31m-    fun agregarSeguidor(asistente: Asistente) {[m
[31m-        if (!followers.contains(asistente)) {[m
[31m-            followers.add(asistente)[m
[31m-            actualizarFecha()[m
[31m-        }[m
[31m-    }[m
[31m-[m
[31m-    fun removerSeguidor(asistente: Asistente) {[m
[31m-        followers.remove(asistente)[m
[31m-        actualizarFecha()[m
[31m-    }[m
[31m-[m
[31m-    fun totalSeguidores(): Int