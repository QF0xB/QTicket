import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    base
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.springBoot) apply false
    alias(libs.plugins.springDependencyManagement) apply false
    alias(libs.plugins.openapiGenerator) apply false
    alias(libs.plugins.dependencyAnalysis) apply false
}

allprojects {
    group = "dev.qticket"
    version = "0.0.1"
}

subprojects {
    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        testLogging {
            exceptionFormat = TestExceptionFormat.FULL
            events("FAILED", "SKIPPED")
        }
    }
}

tasks.register("gen") {
    group = "build"
    description = "Generates all code from contracts (server stubs + TS client)."
    dependsOn(
        ":contracts:openapi:generateAll",
        ":codegen:ts-client:generateTsClient",
    )
}

tasks.register("ci") {
    group = "verification"
    description = "Full CI pipeline target."
    dependsOn(
        "gen",
        "check",
        "test"
    )
}
