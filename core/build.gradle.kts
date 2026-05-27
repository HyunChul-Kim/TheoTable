import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm

plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.vanniktech.maven.publish)
}

dependencies {
    testImplementation(libs.junit)
}

mavenPublishing {
    configure(
        KotlinJvm(
            javadocJar = JavadocJar.Empty(),
            sourcesJar = true,
        )
    )

    coordinates(
        groupId = project.group.toString(),
        artifactId = "theotable-core",
        version = project.version.toString(),
    )

    pom {
        name.set("TheoTable Core")
        description.set("Core table sorting and selection logic for TheoTable.")
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