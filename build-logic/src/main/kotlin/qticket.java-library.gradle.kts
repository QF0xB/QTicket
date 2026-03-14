plugins {
    `java-library`
    id("com.diffplug.spotless")
}

val catalogs = extensions.getByType<VersionCatalogsExtension>()
val libs = catalogs.named("libs")
val javaVersion = libs.findVersion("java").get().requiredVersion

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    val lombok = libs.findLibrary("lombok").get()
    compileOnly(lombok)
    annotationProcessor(lombok)

    testCompileOnly(lombok)
    testAnnotationProcessor(lombok)

    // MapStruct (available in all java-library-based modules)
    val mapstruct = libs.findLibrary("mapstruct").get()
    val mapstructProcessor = libs.findLibrary("mapstruct-processor").get()
    implementation(mapstruct)              // mapper API
    annotationProcessor(mapstructProcessor)
    testImplementation(mapstruct)
    testAnnotationProcessor(mapstructProcessor)
}

//spotless {
//    java {
//        googleJavaFormat()
//        target("src/**/*.java")
//    }
//    kotlinGradle {
//        ktlint()
//        target("**/*.gradle.kts")
//    }
//}
