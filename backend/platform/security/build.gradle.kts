plugins {
    id("qticket.spring-library")
}

dependencies {
    api(libs.spring.boot.starter.security)
    api(libs.spring.boot.starter.oauth2.resource.server)
}