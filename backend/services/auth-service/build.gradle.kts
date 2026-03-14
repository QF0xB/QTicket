plugins {
    id("qticket.spring-service")
}

val genDir = layout.buildDirectory.dir("generated/openapi/server-auth/src/main/java")

sourceSets {
    named("main") {
        java.srcDir(genDir)
    }
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation("org.springframework.boot:spring-boot-starter-restclient")
    implementation(libs.h2.console)

    runtimeOnly(libs.mysql)
    runtimeOnly(libs.h2) // For dev


    implementation(libs.spring.cloud.eureka.client)

    implementation(project(":backend:platform:observability"))
    implementation(project(":backend:platform:error-contract"))
    implementation(project(":backend:platform:security"))
    implementation(project(":backend:platform:events"))

    testImplementation(project(":backend:platform:testing"))
}
