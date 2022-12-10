plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp") version "1.7.20-1.0.7"
    id("maven-publish")
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["kotlin"])
                artifactId = "processor"
                groupId = "com.github.lukasanda.navikator"
                version = "1.0.0"
            }
        }
    }
}


dependencies {
    implementation(project(":annotation"))
    implementation("com.google.devtools.ksp:symbol-processing-api:1.7.20-1.0.7")
    implementation("com.squareup:kotlinpoet:1.10.1")
    implementation("com.squareup:kotlinpoet-ksp:1.10.1")
}