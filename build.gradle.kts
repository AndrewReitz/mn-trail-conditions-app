plugins {
    val kotlinVersion = "1.3.72"
    id("com.android.application") version "3.6.2"
    id("kotlin-android") version kotlinVersion
    id("kotlin-kapt") version kotlinVersion
    id("com.github.triplet.play") version "2.7.5"
    id("com.github.ben-manes.versions") version "0.28.0"
    id("com.google.gms.google-services") version "4.3.3"
    id("com.google.firebase.crashlytics") version "2.0.0"
    id("build-number")
    id("android-signing-config")
}

repositories {
    google()
    mavenCentral()
    jcenter()
}

play {
    serviceAccountCredentials = file(properties["cash.andrew.mntrail.publishKey"] ?: "keys/publish-key.json")
    track = "internal"
    defaultToAppBundles = true
}

android {
    compileSdkVersion(29)
    buildToolsVersion("29.0.3")

    signingConfigs {
        getByName("debug") {
            storeFile = file("keys/debug.keystore")
            storePassword = "android"
            keyAlias = "android"
            keyPassword = "android"
        }
        create("release") {
            val keystoreLocation: String by project
            val keystorePassword: String by project
            val storeKeyAlias: String by project
            val aliasKeyPassword: String by project

            storeFile = file(keystoreLocation)
            storePassword = keystorePassword
            keyAlias = storeKeyAlias
            keyPassword = aliasKeyPassword
        }
    }

    defaultConfig {
        applicationId = "com.andrewreitz.cash.andrew.mntrailconditions"
        minSdkVersion(23)
        targetSdkVersion(29)

        val buildNumber: String by project
        versionCode = buildNumber.toInt()
        versionName = "一"
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            signingConfig = signingConfigs.getByName("debug")
            isMinifyEnabled = false
            isShrinkResources = false
            extra["alwaysUpdateBuildId"] = false
            extra["enableCrashlytics"] = false
        }
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.txt")
        }
    }

    flavorDimensions("environment")

    productFlavors {
        create("internal") {
            dimension = "environment"
            applicationIdSuffix = ".internal"
        }
        create("production") {
            dimension = "environment"
        }
    }

    variantFilter {
        val names = flavors.map { it.name }
        if (buildType.name == "release" && "internal" in names) {
            setIgnore(true)
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    packagingOptions {
        exclude("LICENSE.txt")
        exclude("META-INF/LICENSE.txt")
    }

    viewBinding {
        isEnabled = true
    }
}

val stethoVersion by extra("1.5.1")
val retrofitVersion by extra("2.8.1")

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.5")

    implementation("androidx.core:core-ktx:1.2.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.0-beta4")
    implementation("androidx.annotation:annotation:1.1.0")
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.0.0")


    implementation("android.arch.navigation:navigation-fragment-ktx:1.0.0")
    implementation("android.arch.navigation:navigation-ui-ktx:1.0.0")
    implementation("com.google.android.material:material:1.1.0")

    implementation("com.google.firebase:firebase-core:17.3.0")
    implementation("com.google.firebase:firebase-messaging:20.1.5")
    implementation("com.google.firebase:firebase-analytics:17.4.0")
    implementation("com.google.firebase:firebase-crashlytics:17.0.0")

    debugImplementation("com.jakewharton.dagger:dagger-reflect:0.2.0")

    kaptRelease("com.google.dagger:dagger-compiler:2.27")
    releaseImplementation("com.jakewharton.dagger:dagger-codegen:0.2.0")
    implementation("com.google.dagger:dagger:2.27")

    implementation("com.squareup.okhttp3:okhttp:4.5.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.5.0")
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofitVersion")

    implementation("com.squareup.moshi:moshi:1.9.2")
    debugImplementation("com.squareup.moshi:moshi-kotlin:1.9.2")
    kaptRelease("com.squareup.moshi:moshi-kotlin-codegen:1.9.2")

    implementation("com.jakewharton:process-phoenix:2.0.0")
    implementation("com.jakewharton.timber:timber:4.7.1")
    implementation("com.jakewharton.byteunits:byteunits:0.9.1")

    debugImplementation("com.readystatesoftware.chuck:library:1.1.0")
    releaseImplementation("com.readystatesoftware.chuck:library-no-op:1.1.0")

    implementation("com.jakewharton.threetenabp:threetenabp:1.2.3")

    "internalImplementation"("com.facebook.stetho:stetho:$stethoVersion")
    "internalImplementation"("com.facebook.stetho:stetho-okhttp3:$stethoVersion")
    "internalImplementation"("com.facebook.stetho:stetho-timber:$stethoVersion@aar")

    implementation("com.crashlytics.sdk.android:crashlytics:2.10.1")

    testImplementation("org.amshove.kluent:kluent-android:1.61")
    testImplementation("junit:junit:4.13")
}

val installAll = tasks.register("installAll") {
    description = "Install all applications."
    group = "install"
    dependsOn(android.applicationVariants.map { it.installProvider })
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
    kotlinOptions.jvmTarget = "1.8"
}
