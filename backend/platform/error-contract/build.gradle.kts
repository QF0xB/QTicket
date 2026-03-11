plugins {
    id("qticket.spring-library")
}

sourceSets {
    named("main") {
        java.srcDir(layout.buildDirectory.dir("generated/openapi/platform-problem/src/main/java"))
    }
}

dependencies {
    api(libs.swagger.annotations)

    implementation(libs.spring.boot.starter.web)
}
