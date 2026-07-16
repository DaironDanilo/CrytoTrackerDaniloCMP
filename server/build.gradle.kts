plugins {
    application
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

application {
    mainClass.set("com.cryptodanilo.project.server.ApplicationKt")
}

ktor {
    fatJar {
        archiveFileName.set("server.jar")
    }
}

dependencies {
    implementation(projects.core)

    implementation(libs.bundles.ktor.server)
    implementation(libs.bundles.exposed)
    implementation(libs.hikaricp)
    implementation(libs.postgresql)
    implementation(libs.auth0.java.jwt)
    implementation(libs.logback.classic)
    implementation(libs.kotlinx.serialization.json)

    // Used by the Cube client (plain HTTP calls to Cube Core's REST API) --
    // reusing the same client bundle the app already uses, since :server is
    // a plain JVM module and can depend on it just like any other JVM lib.
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)

    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlin.test.junit)
}
