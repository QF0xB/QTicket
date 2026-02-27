rootProject.name = "qticket"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories { mavenCentral() }
}

includeBuild("build-logic")

// --- contracts + codegen ---
include(
    ":contracts:openapi",
    ":codegen:ts-client",
)

// --- backend platform libs (internal shared libraries, NOT services) ---
include(
    ":backend:platform:observability",
    ":backend:platform:error-contract",
    ":backend:platform:security",
    ":backend:platform:testing",
    ":backend:platform:events",
)

// --- backend edge + services ---
include(
    ":backend:edge:api-gateway",
    ":backend:edge:discovery-server",

    ":backend:services:auth-service",
    ":backend:services:ticket-service",
    ":backend:services:file-service",
    ":backend:services:notification-service",
    ":backend:services:mail-service",
    ":backend:services:search-service",
)

// Map Gradle project paths to directories on disk
project(":contracts:openapi").projectDir = file("contracts/openapi")
project(":codegen:ts-client").projectDir = file("codegen/ts-client")

project(":backend:platform:observability").projectDir = file("backend/platform/observability")
project(":backend:platform:error-contract").projectDir = file("backend/platform/error-contract")
project(":backend:platform:security").projectDir = file("backend/platform/security")
project(":backend:platform:testing").projectDir = file("backend/platform/testing")
project(":backend:platform:events").projectDir = file("backend/platform/events")

project(":backend:edge:api-gateway").projectDir = file("backend/edge/api-gateway")
project(":backend:edge:discovery-server").projectDir = file("backend/edge/discovery-server")

project(":backend:services:auth-service").projectDir = file("backend/services/auth-service")
project(":backend:services:ticket-service").projectDir = file("backend/services/ticket-service")
project(":backend:services:file-service").projectDir = file("backend/services/file-service")
project(":backend:services:notification-service").projectDir = file("backend/services/notification-service")
project(":backend:services:mail-service").projectDir = file("backend/services/mail-service")
project(":backend:services:search-service").projectDir = file("backend/services/search-service")
