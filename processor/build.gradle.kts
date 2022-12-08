plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
    id("maven-publish")
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.getByName("main").kotlin.srcDirs)
}


dependencies {
    implementation(project(":annotation"))
    implementation("com.google.devtools.ksp:symbol-processing-api:1.7.20-1.0.8")
    implementation("com.squareup:kotlinpoet:1.10.1")
    implementation("com.squareup:kotlinpoet-ksp:1.10.1")
}