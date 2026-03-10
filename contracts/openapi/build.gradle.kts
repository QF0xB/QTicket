import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    // no need for java plugin here; this module is just tasks
    alias(libs.plugins.openapiGenerator)
}

val specDir = layout.projectDirectory.dir("specs")

tasks.register("generateAll") {
    group = "openapi"
    description = "Generate OpenAPI server sources for all services."
}

/**
 * Generates Spring server interfaces/models into the target project's build dir and wires
 * compilation to depend on generation, *only after* the target has the Java plugin.
 */
fun registerSpringServerGeneration(
    name: String,
    specFile: String,
    targetProjectPath: String,
    basePackage: String
) {
    val capName = name.replaceFirstChar { it.uppercase() }
    val taskName = "generate${capName}Server"

    // --- 1) Define generator task in THIS project ---
    tasks.register<GenerateTask>(taskName) {
        group = "openapi"
        description = "Generate Spring server interfaces/models for $name from $specFile"

        inputSpec.set(specDir.file(specFile).asFile.absolutePath)
        generatorName.set("spring")

        val target = project(targetProjectPath)
        val outDir = target.layout.buildDirectory.dir("generated/openapi/server-$name").get().asFile.absolutePath
        outputDir.set(outDir)

        apiPackage.set("$basePackage.api")
        modelPackage.set("$basePackage.api.model")

        configOptions.set(
            mapOf(
                "interfaceOnly" to "true",
                "useSpringBoot3" to "true",
                "useTags" to "true",
                "openApiNullable" to "false"
            )
        )

        // generate both APIs + models
        globalProperties.set(
            mapOf(
                "apis" to "",
                "models" to ""
            )
        )
    }

    // --- 2) Wire into the TARGET project, but only once it has Java plugin ---
    val target = project(targetProjectPath)

    target.pluginManager.withPlugin("java") {
        // Add generated sources to target main source set
        val sourceSets = target.extensions.getByType<SourceSetContainer>()
        val genSrc = target.layout.buildDirectory.dir("generated/openapi/server-$name/src/main/java")

        sourceSets.named("main") {
            java.srcDir(genSrc)
        }

        // Ensure compilation triggers generation
        target.tasks.named("compileJava").configure {
            dependsOn(":contracts:openapi:$taskName")
        }
    }

    // Convenience: include in generateAll
    tasks.named("generateAll").configure { dependsOn(taskName) }
}

/* -----------------------------
   Register your service specs
   ----------------------------- */

// Example registrations — adapt names/specs/packages to yours:
registerSpringServerGeneration(
    name = "auth",
    specFile = "auth.yaml",
    targetProjectPath = ":backend:services:auth-service",
    basePackage = "de.qf0xb.qticket.auth"
)

registerSpringServerGeneration(
    name = "ticket",
    specFile = "ticket.yaml",
    targetProjectPath = ":backend:services:ticket-service",
    basePackage = "de.qf0xb.qticket.ticket"
)

registerSpringServerGeneration(
    name = "file",
    specFile = "file.yaml",
    targetProjectPath = ":backend:services:file-service",
    basePackage = "de.qf0xb.qticket.file"
)