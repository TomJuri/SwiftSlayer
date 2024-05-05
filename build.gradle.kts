plugins {
    kotlin("jvm") version "1.9.23"
    id("cc.polyfrost.loom") version "0.10.0.5"
    id("dev.architectury.architectury-pack200") version "0.1.3"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "dev.macrohq"
version = "1.0.0"

repositories {
    maven("https://repo.polyfrost.cc/releases")
    maven("https://repo.spongepowered.org/repository/maven-public")
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
}

val embed: Configuration by configurations.creating
configurations.implementation.get().extendsFrom(embed)

dependencies {
    minecraft("com.mojang:minecraft:1.8.9")
    mappings("de.oceanlabs.mcp:mcp_stable:22-1.8.9")
    forge("net.minecraftforge:forge:1.8.9-11.15.1.2318-1.8.9")
    compileOnly("cc.polyfrost:oneconfig-1.8.9-forge:0.2.0-alpha+")

    compileOnly("org.spongepowered:mixin:0.7.11-SNAPSHOT")
    annotationProcessor("org.spongepowered:mixin:0.8.5-SNAPSHOT:processor")
    modRuntimeOnly("me.djtheredstoner:DevAuth-forge-legacy:1.1.2")

    runtimeOnly("cc.polyfrost:oneconfig-wrapper-launchwrapper:1.0.0-beta+")
    embed("com.github.KevinPriv:keventbus:master") {
        exclude("org.jetbrains.kotlin")
    }
}

loom {
    runConfigs {
        named("client") {
            ideConfigGenerated(true)
        }
    }

    launchConfigs {
        getByName("client") {
            arg("--tweakClass", "cc.polyfrost.oneconfig.loader.stage0.LaunchWrapperTweaker")
            property("devauth.enabled", "false")
            property("fml.coreMods.load", "dev.macrohq.swiftslayer.DevMixinLoader")
        }
    }

    forge {
        pack200Provider.set(dev.architectury.pack200.java.Pack200Adapter())
        mixinConfig("mixins.swiftslayer.json")
        mixin.defaultRefmapName.set("mixins.swiftslayer.refmap.json")
    }
}

tasks {
    jar.get().dependsOn(shadowJar)

    remapJar {
        input.set(shadowJar.get().archiveFile)
        archiveClassifier.set("")
    }

    shadowJar {
        isEnableRelocation = true
        relocationPrefix = "dev.macrohq.swiftslayer.relocate"
        configurations = listOf(embed)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

java.toolchain.languageVersion = JavaLanguageVersion.of(8)
kotlin.jvmToolchain(8)