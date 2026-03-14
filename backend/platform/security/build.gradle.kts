
plugins {
    id("qticket.spring-library")
}

dependencies {
    api(libs.spring.boot.starter.security)
    api(libs.spring.boot.starter.oauth2.resource.server)

    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.aop)
    implementation(libs.aspectj.weaver)

    implementation(project(":backend:platform:error-contract"))
}