import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.JavadocJar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.vanniktech.maven.publish)
}

android {
    namespace = "com.theo.theotable.compose"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
}

dependencies {
    api(project(":core"))

    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.runtime)
    api(libs.androidx.compose.runtime.saveable)
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.graphics)

    testImplementation(libs.junit)

    debugImplementation(libs.androidx.compose.ui.tooling)
}

mavenPublishing {
    configure(
        AndroidSingleVariantLibrary(
            variant = "release",
            sourcesJar = true,
            publishJavadocJar = true,
        )
    )

    coordinates(
        groupId = project.group.toString(),
        artifactId = "theotable-compose",
        version = project.version.toString(),
    )

    pom {
        name.set("TheoTable Compose")
        description.set("Jetpack Compose table UI components for TheoTable.")
        inceptionYear.set("2026")
        url.set("https://github.com/HyunChul-Kim/TheoTable")

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("repo")
            }
        }

        developers {
            developer {
                id.set("HyunChul-Kim")
                name.set("HyunChul Kim")
                url.set("https://github.com/HyunChul-Kim")
            }
        }

        scm {
            url.set("https://github.com/HyunChul-Kim/TheoTable")
            connection.set("scm:git:https://github.com/HyunChul-Kim/TheoTable.git")
            developerConnection.set("scm:git:ssh://git@github.com/HyunChul-Kim/TheoTable.git")
        }
    }
}
