plugins {
    `kotlin-dsl`
}

repositories {
    jcenter()
    google()
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

dependencies {
    implementation(kotlin("gradle-plugin", "1.4.20"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.0.0")
    implementation("com.android.tools.build:gradle:4.1.1")
    implementation("com.google.gms:google-services:4.3.4")
    implementation("com.google.firebase:firebase-crashlytics-gradle:2.4.1")
    implementation("com.squareup:kotlinpoet:1.7.2")

    testImplementation(gradleKotlinDsl())
    testImplementation(gradleTestKit())
    implementation(kotlin("test-junit", "1.4.0"))
    testImplementation("junit:junit:4.13.1")
}
