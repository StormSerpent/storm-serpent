import java.io.FileNotFoundException

plugins {
    id("java")
}

group = "me.nullicorn"
version = "0.1.2-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "hytale-release"
        url = uri("https://maven.hytale.com/release/")
    }
    maven {
        name = "hytale-pre-release"
        url = uri("https://maven.hytale.com/pre-release/")
    }
}

dependencies {
    implementation("com.hypixel.hytale:Server:0.5.0-pre.9.1")
}

val pluginProcessResourcesTask = tasks.register<Copy>("pluginProcessResources") {
    description = "Copy non-asset resources for the plugin"
    group = "hytale"

    copy {
        from("src/main/resources/manifest.json")
        into("build/resources/main/")
        filter {
            it.replace("\"IncludesAssetPack\": true,", "")
        }
    }
}

val pluginJarTask = tasks.register<Jar>("pluginJar") {
    description = "Packages the plugin without any assets"
    group = "hytale"

    dependsOn("classes", "pluginProcessResources")
    from("build/classes/java/main/", "build/resources/main/manifest.json")
    archiveClassifier = "bin"
}

// Turn off 'processResources' if 'pluginProcessResources' is executing.
gradle.taskGraph.whenReady {
    if (this.hasTask(pluginProcessResourcesTask.get())) {
        tasks.processResources.get().enabled = false
    }
}

fun runServer(task: JavaExec, vararg extraArgs: String) {
    val hytaleServerArtifact =
        project.configurations.compileClasspath.get().resolvedConfiguration.resolvedArtifacts.find { it.moduleVersion.id.group == "com.hypixel.hytale" && it.moduleVersion.id.name == "Server" }
    if (hytaleServerArtifact == null) {
        throw FileNotFoundException("failed to locate Hytale server dependency and its jar file")
    }

    val hytaleAssetsZip = layout.projectDirectory.file("run/Assets.zip").asFile
    if (!hytaleAssetsZip.isFile) {
        throw FileNotFoundException("please copy the Assets.zip file for version ${hytaleServerArtifact.moduleVersion.id.version} into the `run` folder")
    }

    task.workingDir = layout.projectDirectory.dir("run/").asFile
    task.classpath = files(hytaleServerArtifact.file)
    task.mainClass = "com.hypixel.hytale.Main"
    task.args = listOf(
        "--assets", hytaleAssetsZip.path,
        // Load our mod jar.
        "--mods", layout.buildDirectory.get().dir("libs/").asFile.path,
        // Load our mod asset pack.
        "--mods", layout.projectDirectory.dir("src/main/").asFile.path,
    ) + extraArgs
}

tasks.register<JavaExec>("runServer") {
    dependsOn("pluginJar")
    group = "hytale"

    // Allow Hytale console commands to be input through this Gradle task's stdin.
    standardInput = System.`in`

    runServer(task = this)
}

tasks.register<JavaExec>("generateAssetSchema") {
    dependsOn("pluginJar")
    group = "hytale"

    runServer(
        this,
        "--generate-asset-schema", layout.projectDirectory.dir("src/main/resources/").asFile.path
    )
}
