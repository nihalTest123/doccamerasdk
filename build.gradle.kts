import java.util.Properties

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    id("maven-publish")
}




val githubProperties = Properties().apply {
    val propertiesFile = rootProject.file("github.properties")
    if (propertiesFile.exists()) {
        load(propertiesFile.inputStream()) // Load properties file if it exists
    }
}

fun getVersionName(): String {
    return "1.0.2" // Replace with version name
}

fun getArtifactId(): String {
    return "sampleAndroidLib" // Replace with library name ID
}

publishing {
    publications {
        create<MavenPublication>("bar") {
            groupId = "com.enefce.libraries" // Replace with group ID
            artifactId = getArtifactId()
            version = getVersionName()
            artifact("$buildDir/outputs/aar/${getArtifactId()}-release.aar")
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            // Configure the URL of the package repository on GitHub
            url = uri("https://maven.pkg.github.com/GITHUB_USERID/REPOSITORY")
            credentials {
                username = githubProperties["gpr.usr"] as String? ?: System.getenv("GPR_USER")
                password = githubProperties["gpr.key"] as String? ?: System.getenv("GPR_API_KEY")
            }
        }
    }
}

