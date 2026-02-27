import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    id("qticket.java-library")
    alias(libs.plugins.openapiGenerator)
}

val specRoot = layout.projectDirectory.dir("../../contracts/openapi/specs")
val tsClientOut = layout.projectDirectory.dir("../../clients/packages/api-client")

tasks.register<GenerateTask>("generateTsClient") {
    group = "openapi"
    description = "Generate TypeScript API client into clients/packages/api-client"

    inputSpec.set(specRoot.file("gateway.yaml").asFile.absolutePath)

    generatorName.set("typescript-fetch")
    outputDir.set(tsClientOut.asFile.absolutePath)

    configOptions.set(
        mapOf(
            "supportsES6" to "true",
            "typescriptThreePlus" to "true",
            "npmName" to "@qticket/api-client",
            "npmVersion" to "0.1.0",
        ),
    )
}
