plugins {
    java
    jacoco
}

repositories {
    jcenter()
}

val sourcesPath: Configuration by configurations.creating {
    isVisible = false
    isCanBeResolved = true
    isCanBeConsumed = false
    extendsFrom(configurations.implementation.get())
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
        attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named("source-folders"))
    }
}

val coverageDataPath: Configuration by configurations.creating {
    isVisible = false
    isCanBeResolved = true
    isCanBeConsumed = false
    extendsFrom(configurations.implementation.get())
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
        attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named("jacoco-coverage-data"))
    }
}

val codeCoverageReport by tasks.registering(JacocoReport::class) {
    doFirst {
        delete(fileTree("${buildDir}/classes") {
            include("**/CharacterIteratorWrapper*")
        })
    }
    additionalClassDirs(configurations.runtimeClasspath.get())
    additionalSourceDirs(sourcesPath.incoming.artifactView { lenient(true) }.files)
    executionData(coverageDataPath.incoming.artifactView { lenient(true) }.files.filter { it.exists() })

    reports {
        xml.isEnabled = true
        html.isEnabled = true
    }
}

tasks.check {
    dependsOn(codeCoverageReport)
}

