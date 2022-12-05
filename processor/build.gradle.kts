plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp") version "1.7.20-1.0.7"
    id("maven-publish")
//    id("com.github.dcendents.android-maven")
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.getByName("main").kotlin.srcDirs)
}

afterEvaluate {
    publishing {
        publications {
            val release by publications.registering(MavenPublication::class) {
                from(components["kotlin"])
                artifact(sourcesJar.get())
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