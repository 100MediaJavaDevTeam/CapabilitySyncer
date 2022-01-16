# Capability Syncer
**Capability Syncer** (or **CapSyncer** for short) is a Minecraft Forge library that allows you to easily setup capability registration and syncing.
It is developed by the 100 Media team and made publicly available for anyone to use.

## How to use
### Setting up shading
Capability Syncer is designed to be used in production through shading.
This is typically done through the use of the [Gradle Shadow plugin](https://github.com/johnrengelman/shadow).

First, set up the Shadow plugin by adding it above the `apply plugin: 'net.minecraftforge.gradle'` line near the top of your `build.gradle`.
It should look something like this:
```groovy
plugins {
    id 'com.github.johnrengelman.shadow' version '7.1.2'
}

apply plugin: 'net.minecraftforge.gradle'
```
At the time of writing, 7.1.2 is the latest version of the Shadow plugin for Gradle 7.
See the latest releases [here](https://github.com/johnrengelman/shadow/releases).

Next, add this block of code above the `dependencies {}` block:
```groovy
configurations {
    shade
    implementation.extendsFrom shade
}

dependencies {
    // Dependencies here
}
```
This declares a new `shade` configuration while also telling Gradle that dependencies under `shade` should still be treated like normal in a development environment.

You must also configure the Shadow plugin to output the shaded jar correctly.
To prevent issues, you must ensure that the `jar {}` block contains the line `archiveClassifier = 'slim'`.
You should also configure the Shadow plugin after this `jar {}` block:
```groovy
jar {
    archiveClassifier = 'slim'
    manifest {
        attributes([
                // Attributes here
        ])
    }
}

shadowJar {
    archiveClassifier = ''
    configurations = [project.configurations.shade]
    relocate 'dev._100media.capabilitysyncer', "${project.group}.relocated.capabilitysyncer"
    finalizedBy 'reobfShadowJar'
}

artifacts {
    shadowJar
}

reobf {
    shadowJar {}
}
```

You have now setup shading!
Running `gradlew build` will produce a shadow jar, although it won't be much different until you include some dependencies under `shade`.

### Adding the Capability Syncer dependency
1. Add the 100 Media repository to your `build.gradle`:
```groovy
repositories {
    maven {
        name = '100Media'
        url = 'https://maven.100media.dev/'
    }
}
```
2. Add the Capability Syncer dependency:
```groovy
dependencies {
    // Capability Syncer
    shade fg.deobf("dev._100media.capabilitysyncer:capabilitysyncer:${capabilitysyncer_version}")
}
```
The latest version can be found in [the gradle.properties file](gradle.properties). It must be prefixed with the minecraft version, e.g. `1.18.1-2.0.4`.

## Examples
See the [examples package](src/main/java/dev/_100media/capabilitysyncer/example) for example usages of CapSyncer.