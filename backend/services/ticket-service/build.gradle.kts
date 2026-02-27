plugins {
    id("qticket.spring-service")
}

val genDir = layout.buildDirectory.dir("generated/openapi/server-ticket/src/main/java")

sourceSets {
    named("main") {
        java.srcDir(genDir)
    }
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.data.jpa)
    runtimeOnly(libs.mysql)

    implementation(libs.spring.cloud.eureka.client)

    implementation(libs.spring.boot.starter.cache)
    implementation(libs.lettuce)

    implementation(project(":backend:platform:observability"))
    implementation(project(":backend:platform:error-contract"))
    implementation(project(":backend:platform:security"))
    implementation(project(":backend:platform:events"))

    testImplementation(project(":backend:platform:testing"))
}
