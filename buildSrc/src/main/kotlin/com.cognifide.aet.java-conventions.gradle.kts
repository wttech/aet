plugins {
    `java-library`
    `maven-publish`
    java
    jacoco
}

repositories {
    maven {
        url = uri("http://repository.jboss.org/nexus/content/groups/public")
    }
    mavenCentral()
    jcenter()
}

group = "com.cognifide.aet"
version = "3.4.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

val projectCompile by configurations.creating

sourceSets.main.get().compileClasspath += configurations["projectCompile"]
sourceSets.test.get().compileClasspath += configurations["projectCompile"]
sourceSets.test.get().runtimeClasspath += configurations["projectCompile"]

tasks.jacocoTestReport {
    enabled = false
}

configurations.create("transitiveSourcesElements") {
    isVisible = false
    isCanBeResolved = false
    isCanBeConsumed = true
    extendsFrom(configurations.implementation.get())
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
        attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named("source-folders"))
    }
    sourceSets.main.get().java.srcDirs.forEach {
        outgoing.artifact(it)
    }
}

configurations.create("coverageDataElements") {
    isVisible = false
    isCanBeResolved = false
    isCanBeConsumed = true
    extendsFrom(configurations.implementation.get())
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
        attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named("jacoco-coverage-data"))
    }
    outgoing.artifact(tasks.test.map { task ->
        task.extensions.getByType<JacocoTaskExtension>().destinationFile!!
    })
}