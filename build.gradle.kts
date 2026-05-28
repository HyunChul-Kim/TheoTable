plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.vanniktech.maven.publish) apply false
}

subprojects {
    group = providers.gradleProperty("theoTableGroup").getOrElse("io.github.hyunchul-kim")
    version = providers.gradleProperty("theoTableVersion").getOrElse("0.3.0")
    plugins.withId("signing") {
        extensions.configure<SigningExtension>("signing") {
            useGpgCmd()
        }
    }
}
