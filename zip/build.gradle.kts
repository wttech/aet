plugins {
    id("com.cognifide.aet.java-conventions")
    id("com.cognifide.aet.test-coverage")
}

val packages = "${buildDir}/packages"

tasks.register<Copy>("packageBundles") {
    dependsOn(rootProject.getTasksByName("build", true))
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
        into("${packages}/bundles")
    }
}

tasks.register<Zip>("zipBundles") {
    dependsOn(tasks["packageBundles"])
    archiveFileName.set("bundles.zip")
    destinationDirectory.set(file(packages))
    includeEmptyDirs = false
    from("${packages}/bundles")
}

tasks.register<Copy>("packageConfigs") {
    from("${project(":configs").projectDir}/src/main/resources")
    into("${packages}/configs")
}

tasks.register<Zip>("zipConfigs") {
    dependsOn(tasks["packageConfigs"])
    destinationDirectory.set(file(packages))
    archiveFileName.set("configs.zip")
    from("${packages}/configs")
}

tasks.register<Copy>("packageFeatures") {
    includeEmptyDirs = false
    from("${rootDir}/osgi-dependencies") {
        include("**/aet-features.xml")
        include("**/aet-webconsole.xml")
    }
    into("${packages}/features")
}

tasks.register<Zip>("zipFeatures") {
    dependsOn(tasks["packageFeatures"])
    destinationDirectory.set(file(packages))
    archiveFileName.set("features.zip")
    includeEmptyDirs = false
    from("${packages}/features")
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
    from("${project(":sample-site").buildDir}/libs/sample-site-${rootProject.version}.war") {
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
