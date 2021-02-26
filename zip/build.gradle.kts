plugins {
    id("com.cognifide.aet.java-conventions")
    id("com.cognifide.aet.test-coverage")
}

val packages = "${buildDir}/packages"

tasks.register<Zip>("zipBundles") {
    dependsOn(rootProject.getTasksByName("build", true))
    archiveFileName.set("bundles.zip")
    destinationDirectory.set(file(packages))
    includeEmptyDirs = false
    rootProject.subprojects.forEach { subproject ->
        from("${subproject.buildDir}/libs") {
            include("**/${subproject.name}*.jar")
            exclude("*aet-maven-plugin*.jar")
            exclude("*-test*.jar")
            exclude("*client*.jar")
            exclude("*sample-site*.jar")
            exclude("*zip*")
            rename { "${project.group}.${subproject.name}.jar" }
        }
    }
}

tasks.register<Zip>("zipConfigs") {
    destinationDirectory.set(file(packages))
    archiveFileName.set("configs.zip")
    from("${project(":configs").projectDir}/src/main/resources")
}

tasks.register<Zip>("zipFeatures") {
    destinationDirectory.set(file(packages))
    archiveFileName.set("features.zip")
    includeEmptyDirs = false
    from("${rootDir}/osgi-dependencies") {
        include("**/aet-features.xml")
        include("**/aet-webconsole.xml")
    }
}

tasks.register<Copy>("zipReport") {
    dependsOn(project(":report").getTasksByName("build", true))
    from("${project(":report").buildDir}/distributions/report-${rootProject.version}.zip") {
        rename { "report.zip" }
    }
    into(packages)
}

tasks.register<Zip>("zipSampleSite") {
    dependsOn(project(":sample-site").getTasksByName("build", true))
    destinationDirectory.set(file(packages))
    archiveFileName.set("sample-site.zip")
    includeEmptyDirs = false
    from(project(":sample-site").buildDir) {
        include("**/*.war")
        rename { "sample-site.war" }
    }
}

tasks.register("make") {
    dependsOn(tasks["zipBundles"])
    dependsOn(tasks["zipConfigs"])
    dependsOn(tasks["zipFeatures"])
    dependsOn(tasks["zipReport"])
    dependsOn(tasks["zipSampleSite"])
}

description = "AET"
