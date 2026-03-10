plugins {
    id("qticket.spring-library")
}

dependencies {
    api(libs.swagger.annotations)

    implementation(libs.spring.boot.starter.web)
}