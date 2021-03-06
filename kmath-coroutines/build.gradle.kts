plugins {
    id("scientifik.mpp")
    //id("scientifik.atomic")
}

kotlin.sourceSets {
    commonMain {
        dependencies {
            api(project(":kmath-core"))
            api("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:${Scientifik.coroutinesVersion}")
        }
    }
    jvmMain {
        dependencies {
            api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Scientifik.coroutinesVersion}")
        }
    }
    jsMain {
        dependencies {
            api("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:${Scientifik.coroutinesVersion}")
        }
    }
}
