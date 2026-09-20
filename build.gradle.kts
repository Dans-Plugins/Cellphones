plugins {
    kotlin("jvm") version "2.2.20"
    id("com.gradleup.shadow") version "9.0.2"
}

group = "com.dansplugins"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.11-R0.1-SNAPSHOT")

    testImplementation("org.spigotmc:spigot-api:1.21.11-R0.1-SNAPSHOT")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

tasks.processResources {
    // plugin.yml reads the version from here so the build file is the only place it is set.
    val props = mapOf("version" to project.version)
    inputs.properties(props)
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.test {
    useJUnitPlatform()
}

// The shaded jar (with the Kotlin stdlib bundled) is the plugin jar; the plain jar is not built.
tasks.jar {
    enabled = false
}

tasks.shadowJar {
    archiveBaseName.set("Cellphones")
    archiveClassifier.set("")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
