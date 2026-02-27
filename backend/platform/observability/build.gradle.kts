plugins {
    id("qticket.spring-library")
}

dependencies {
    api(libs.spring.boot.starter.opentelemetry)
    api(libs.opentelemetry.exporter.otlp)
}