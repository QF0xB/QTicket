plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

kotlin {
    jvmToolchain(25)
}

dependencies {
    // Spotless (already)
    implementation("com.diffplug.spotless:spotless-plugin-gradle:${libs.versions.spotless.get()}")

    // Spring Dependency Management plugin (THIS fixes your error)
    implementation("io.spring.gradle:dependency-management-plugin:${libs.versions.springDependencyManagement.get()}")

    // (Strongly recommended) Spring Boot plugin too, if any convention plugin applies it:
    implementation("org.springframework.boot:spring-boot-gradle-plugin:${libs.versions.springBoot.get()}")
}