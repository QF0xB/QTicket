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
    basePackage: String,
    version: String? = null,
    generateUtil: Boolean? = true
) {
    val capName = name.replaceFirstChar { it.uppercase() }
    val normalizedVersion = version?.replace('.', '_')
    val taskName = if (normalizedVersion == null) {
        "generate${capName}Server"
    } else {
        "generate${capName}V${normalizedVersion}Server"
    }

    // Skip registration if the spec file does not exist yet
    val spec = specDir.file(specFile)
    if (!spec.asFile.exists()) {
        logger.lifecycle("Skipping OpenAPI generation for '$name': spec not found at ${spec.asFile}")
        return
    }

    // --- 1) Define generator task in THIS project ---
    tasks.register<GenerateTask>(taskName) {
        group = "openapi"
        description = "Generate Spring server interfaces/models for $name from $specFile"

        inputSpec.set(specDir.file(specFile).asFile.absolutePath)
        generatorName.set("spring")

        val target = project(targetProjectPath)
        val outDir = if (normalizedVersion == null) {
            target.layout.buildDirectory.dir("generated/openapi/server-$name").get().asFile.absolutePath
        } else {
            target.layout.buildDirectory.dir("generated/openapi/server-$name-v$normalizedVersion").get().asFile.absolutePath
        }
        outputDir.set(outDir)

        val effectiveBasePackage = if (normalizedVersion == null) {
            basePackage
        } else {
            "$basePackage.v$normalizedVersion"
        }

        apiPackage.set("$effectiveBasePackage.api")
        modelPackage.set("$effectiveBasePackage.api.model")

        configOptions.set(
            mapOf(
                "interfaceOnly" to "true",
                "useSpringBoot3" to "true",
                "useTags" to "true",
                "openApiNullable" to "false",
                "useLombok" to "true",
                "useJakartaEe" to "true",
                "useBeanValidation" to "true"
            )
        )

        val supportingFiles = if (generateUtil == true) {
            "ApiUtil.java"
        } else {
            "false"
        }
        // generate APIs, models, and the ApiUtil supporting file
        globalProperties.set(
            mapOf(
                "apis" to "",
                "models" to "",
                "supportingFiles" to supportingFiles
            )
        )
    }

    // --- 2) Wire into the TARGET project, but only once it has Java plugin ---
    val target = project(targetProjectPath)

    target.pluginManager.withPlugin("java") {
        // Add generated sources to target main source set
        val sourceSets = target.extensions.getByType<SourceSetContainer>()
        val genSrc = if (normalizedVersion == null) {
            target.layout.buildDirectory.dir("generated/openapi/server-$name/src/main/java")
        } else {
            target.layout.buildDirectory.dir("generated/openapi/server-$name-v$normalizedVersion/src/main/java")
        }

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

/**
 * Generate Java client for api models
 */
fun registerJavaClientGeneration(
    name: String,
    specFile: String,
    targetProjectPath: String,
    basePackage: String,
    version: String? = null
) {
    val capName = name.replaceFirstChar { it.uppercase() }
    val normalizedVersion = version?.replace('.', '_')
    val taskName = if (normalizedVersion == null) {
        "generate${capName}Client"
    } else {
        "generate${capName}V${normalizedVersion}Client"
    }

    // Skip if spec is missing
    val spec = specDir.file(specFile)
    if (!spec.asFile.exists()) {
        logger.lifecycle("Skipping OpenAPI client generation for '$name': spec not found at ${spec.asFile}")
        return
    }

    // --- 1) Define generator task in THIS project ---
    tasks.register<GenerateTask>(taskName) {
        group = "openapi"
        description = "Generate Java client for $name from $specFile"

        inputSpec.set(spec.asFile.absolutePath)
        generatorName.set("java") // Java client generator

        val target = project(targetProjectPath)
        val outDir = if (normalizedVersion == null) {
            target.layout.buildDirectory.dir("generated/openapi/client-$name").get().asFile.absolutePath
        } else {
            target.layout.buildDirectory.dir("generated/openapi/client-$name-v$normalizedVersion").get().asFile.absolutePath
        }
        outputDir.set(outDir)

        val effectiveBasePackage = if (normalizedVersion == null) {
            basePackage
        } else {
            "$basePackage.v$normalizedVersion"
        }

        // Put client under .client.* so it doesn't collide with server stubs
        apiPackage.set("$effectiveBasePackage.client.api")
        modelPackage.set("$effectiveBasePackage.client.model")
        invokerPackage.set("$effectiveBasePackage.client.invoker")

        // WebClient‑style Java client, Jakarta, bean validation, etc.
        configOptions.set(
            mapOf(
                "library" to "resttemplate",   // or "webclient" if you prefer
                "useSpringBoot3" to "true",
                "useTags" to "true",
                "openApiNullable" to "false",
                "useLombok" to "true",
                "useJakartaEe" to "true",
                "useBeanValidation" to "true"
            )
        )
        globalProperties.set(
            mapOf(
                "apis" to "",
                "models" to "",
                "supportingFiles" to ""   // no extra project scaffolding
            )
        )
    }

    // --- 2) Wire into TARGET project once it has Java plugin ---
    val target = project(targetProjectPath)

    target.pluginManager.withPlugin("java") {
        val sourceSets = target.extensions.getByType<SourceSetContainer>()
        val genSrc = if (normalizedVersion == null) {
            target.layout.buildDirectory.dir("generated/openapi/client-$name/src/main/java")
        } else {
            target.layout.buildDirectory.dir("generated/openapi/client-$name-v$normalizedVersion/src/main/java")
        }

        sourceSets.named("main") {
            java.srcDir(genSrc)
        }

        target.tasks.named("compileJava").configure {
            dependsOn(":contracts:openapi:$taskName")
        }
    }

    // 3) Include in generateAll for convenience
    tasks.named("generateAll").configure { dependsOn(taskName) }
}

fun registerPlatformProblemDetailGeneration(
    specFile: String,                 // e.g. "v1/error.yaml"
    targetProjectPath: String,       // e.g. ":backend:platform"
    modelPackage: String             // e.g. "de.qf0xb.qticket.platform.problem"
) {
    tasks.register<GenerateTask>("generatePlatformProblemDetail") {
        group = "openapi"
        description = "Generate shared ProblemDetail model into platform"

        inputSpec.set(specDir.file(specFile).asFile.absolutePath)
        generatorName.set("spring")

        val target = project(targetProjectPath)
        val outDir = target.layout.buildDirectory
            .dir("generated/openapi/platform-problem")
            .get()
            .asFile
            .absolutePath
        outputDir.set(outDir)

        // Only models, no APIs
        globalProperties.set(
            mapOf(
                "useSpringBoot3" to "true",
                "useTags" to "true",
                "useLombok" to "true",
                "models" to "",
                "apis" to "false",
                "supportingFiles" to "false"
            )
        )

        // Put models into platform package
        this.modelPackage.set(modelPackage)

        // Optional: Lombok on generated models
        configOptions.set(
            mapOf(
                "useLombok" to "true",
                "openApiNullable" to "false",
                "useJakartaEe" to "true",
                "useBeanValidation" to "true"
            )
        )
    }
}

registerPlatformProblemDetailGeneration(
    specFile = "v1/error.yaml",
    targetProjectPath = ":backend:platform:error-contract",
    modelPackage = "de.qf0xb.qticket.problem"
)



/* -----------------------------
   Register your service specs
   ----------------------------- */

registerSpringServerGeneration(
    name = "auth",
    specFile = "v1/auth.yaml",
    targetProjectPath = ":backend:services:auth-service",
    basePackage = "de.qf0xb.qticket.auth",
    version = "1"
)

registerSpringServerGeneration(
    name = "accounts",
    specFile = "v1/accounts.yaml",
    targetProjectPath = ":backend:services:auth-service",
    basePackage = "de.qf0xb.qticket.auth",
    version = "1",
    generateUtil = false
)

registerSpringServerGeneration(
    name = "user",
    specFile = "v1/user.yaml",
    targetProjectPath = ":backend:services:user-service",
    basePackage = "de.qf0xb.qticket.user",
    version = "1",
)
registerJavaClientGeneration(
    name = "user",
    specFile = "v1/user.yaml",
    targetProjectPath = ":backend:services:auth-service",
    basePackage = "de.qf0xb.qticket.user",
    version = "1"
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