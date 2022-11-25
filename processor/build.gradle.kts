plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp") version "1.7.20-1.0.7"
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}

dependencies {
    implementation(project(":annotation"))
    implementation("com.google.devtools.ksp:symbol-processing-api:1.7.20-1.0.7")
    implementation("com.squareup:kotlinpoet:1.10.1")
    implementation("com.squareup:kotlinpoet-ksp:1.10.1")
}