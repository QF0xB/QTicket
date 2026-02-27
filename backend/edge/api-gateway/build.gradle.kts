plugins {
    id("qticket.spring-service")
}

dependencies {
    implementation(libs.spring.cloud.gateway)
    implementation(libs.spring.cloud.eureka.client)

    implementation(project(":backend:platform:observability"))
    implementation(project(":backend:platform:error-contract"))
    implementation(project(":backend:platform:security"))
    implementation(project(":backend:platform:events"))

    testImplementation(project(":backend:platform:testing"))
}
