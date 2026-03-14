plugins {
    id("qticket.spring-service")
}

dependencies {
    implementation(libs.spring.cloud.eureka.server)

    implementation(project(":backend:platform:observability"))
    implementation(project(":backend:platform:error-contract"))
    implementation(project(":backend:platform:security"))
    implementation(project(":backend:platform:events"))

    testImplementation(project(":backend:platform:testing"))
}

