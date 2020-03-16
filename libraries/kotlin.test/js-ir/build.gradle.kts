import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.plugin.KotlinJsCompilerType.IR

plugins {
    kotlin("multiplatform")
}

val commonMainSources by task<Sync> {
    from(
        "$rootDir/libraries/kotlin.test/common",
        "$rootDir/libraries/kotlin.test/annotations-common"
    )
    into("$buildDir/commonMainSources")
}

val jsMainSources by task<Sync> {
    from("$rootDir/libraries/kotlin.test/js")
    into("$buildDir/jsMainSources")
}

kotlin {
    js(IR) {
        nodejs()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":kotlin-stdlib-js-ir-new"))
            }
            kotlin.srcDir(commonMainSources.get().destinationDir)
        }
        val jsMain by getting {
            dependencies {
                api(project(":kotlin-stdlib-js-ir-new"))
            }
            kotlin.srcDir(jsMainSources.get().destinationDir)
        }
    }
}

tasks.withType<KotlinCompile<*>>().configureEach {
    kotlinOptions.freeCompilerArgs += listOf(
        "-Xallow-kotlin-package",
        "-Xallow-result-return-type",
        "-Xuse-experimental=kotlin.Experimental",
        "-Xuse-experimental=kotlin.ExperimentalMultiplatform",
        "-Xuse-experimental=kotlin.contracts.ExperimentalContracts",
        "-Xinline-classes"
    )
}

tasks.named("compileKotlinJs") {
    dependsOn(commonMainSources)
    dependsOn(jsMainSources)
}
