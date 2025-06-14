import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import java.net.URL

object This {
    const val longName = "Secure Random - Angelos Project™"
    const val longDescription = "Secure Random for Angelos Project™ with access to real entropy."
    const val url = "https://github.com/angelos-project/angelos-project-secrand"
}

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.dokka)
    jacoco
}

group = "org.angproj.sec"
version = "0.10.2"

kotlin {
    explicitApi()
    jvmToolchain(libs.versions.jvm.toolchain.get().toInt())

    jvm()
    js {
        browser()
        nodejs()
    }
    // WASM and similar
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmWasi { nodejs() }
    // Android
    androidTarget {
        /*@OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }*/
        publishLibraryVariants("release")
    }
    androidNativeArm32()
    androidNativeArm64()
    androidNativeX64()
    androidNativeX86()
    // Linux
    linuxArm64()
    linuxX64()
    // macOS
    macosArm64()
    macosX64()
    // MingW
    mingwX64()
    // iOS
    iosArm64()
    iosX64()
    iosSimulatorArm64()
    // tvOS
    tvosArm64()
    tvosX64()
    tvosSimulatorArm64()
    // watchOS
    watchosArm32()
    watchosArm64()
    watchosDeviceArm64()
    watchosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmTest.dependencies {
            implementation(libs.kotlin.mockito)

        }
    }
}

android {
    namespace = group.toString()
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        //sourceCompatibility = JavaVersion.VERSION_11
        //targetCompatibility = JavaVersion.VERSION_11
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    //signAllPublications()

    /**
     * The temporary artifact setup, final is coming later at some point.
     * DO NOT USE FOR SONATYPE NEXUS
     * */
    coordinates(group.toString(), rootProject.name, version.toString())
    //coordinates(group.toString(), version.toString())

    pom {
        name.set(This.longName)
        description.set(This.longDescription)
        inceptionYear.set("2024")
        url.set(This.url)

        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
                distribution.set("repo")
            }
        }
        developers {
            developer {
                name.set("Kristoffer Paulsson")
                email.set("kristoffer.paulsson@talenten.se")
                url.set("https://github.com/kristoffer-paulsson")
            }
        }
        scm {
            url.set(This.url)
            connection.set("scm:git:git://github.com/angelos-project/angelos-project-secrand.git")
            developerConnection.set("scm:git:ssh://github.com:angelos-project/angelos-project-secrand.git")
        }
    }
}

tasks.dokkaHtml {
    dokkaSourceSets {
        named("commonMain"){
            moduleName.set(This.longName)
            includes.from("Module.md")
            sourceLink {
                localDirectory.set(file("src/commonMain/kotlin"))
                remoteUrl.set(URL(This.url + "/tree/master/src/commonMain/kotlin"))
                remoteLineSuffix.set("#L")
            }
        }
    }
}

jacoco {
    toolVersion = "0.8.12"
    reportsDirectory.set(layout.buildDirectory.dir("reports/jacoco"))
}

tasks {
    withType<Test> {
        finalizedBy(withType(JacocoReport::class))
    }
    register("jacocoTestReport", JacocoReport::class) {
        dependsOn(withType(Test::class))
        val coverageSourceDirs = arrayOf(
            "src/commonMain",
        )

        val buildDirectory = layout.buildDirectory

        val classFiles = buildDirectory.dir("classes/kotlin/jvm").get().asFile
            .walkBottomUp()
            .toSet()

        classDirectories.setFrom(classFiles)
        sourceDirectories.setFrom(files(coverageSourceDirs))

        buildDirectory.files("jacoco/jvmTest.exec").let {
            executionData.setFrom(it)
        }

        reports {
            xml.required.set(true)
            csv.required.set(true)
            html.required.set(true)
        }
    }
}
