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
