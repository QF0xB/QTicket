import gradle.kotlin.dsl.accessors._632cb05945fa88b9f5ea9daf5444817c.developmentOnly

plugins {
    id("qticket.java-library")
    id("io.spring.dependency-management")
}

val catalogs = extensions.getByType<VersionCatalogsExtension>()
val libs = catalogs.named("libs")
val springBootVersion = libs.findVersion("springBoot").get().requiredVersion

dependencies {
    // Provide Spring's BOM to consumers of this library module
    api(platform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}