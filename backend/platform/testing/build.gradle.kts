plugins {
    id("qticket.spring-library")
}

dependencies {
    api(libs.assertj.core)
    api(libs.mockito.core)
    api(libs.testcontainers.junit)
    api(libs.testcontainers.mysql)
}