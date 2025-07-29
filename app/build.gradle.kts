plugins {
    id("buildsrc.convention.kotlin-jvm")
    id("com.gradleup.shadow") version "9.0.0-rc1"

    application
    alias(libs.plugins.kotlinPluginSerialization)
}

dependencies {
    implementation(project(":utils"))
    implementation(project(":lobby"))
    implementation(project(":kotlinx-serialization-nbt"))
    implementation(project(":resources:data"))
    implementation(libs.bundles.kotlinxEcosystem)
    implementation(libs.bundles.adventure)
    implementation(libs.bundles.utils)
    implementation(libs.bundles.luckperms)
    implementation(libs.minestom)
    implementation(libs.spark)
    implementation("dev.hollowcube:schem:2.0")
    implementation("org.joml:joml:1.10.8")
    implementation("net.worldseed.multipart:WorldSeedEntityEngine:11.4.2")

    implementation("org.everbuild.blocksandstuff:blocksandstuff-common:1.2.0-SNAPSHOT")
    implementation("org.everbuild.blocksandstuff:blocksandstuff-blocks:1.2.0-SNAPSHOT")
    implementation("org.everbuild.blocksandstuff:blocksandstuff-fluids:1.2.0-SNAPSHOT")

    implementation(libs.ambrosia)
    implementation(libs.mongodb)



    implementation(libs.mccoroutineMinestomApi)
    implementation(libs.mccoroutineMinestomCore)

    implementation("xyz.xenondevs.invui:invui-kotlin:1.48")
    implementation(libs.evalEx)

    implementation("com.github.ben-manes.caffeine:caffeine:3.2.1")
    implementation("com.catppuccin:catppuccin-palette:${libs.versions.catppuccin.get()}")
    implementation(libs.polar)

    implementation(libs.minestomPVP)
}

application {
    mainClass = "llc.redstone.playground.PlaygroundKt"
}

tasks.named<JavaExec>("run") {
    workingDir = file("../run")
}

val includeSchematics = tasks.register<Copy>("includeSchematics") {
    from("../schematics")
    into(layout.buildDirectory.dir("resources/main"))
}

tasks.processResources {
    dependsOn(includeSchematics)
}