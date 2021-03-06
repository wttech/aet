plugins {
    `java-library`
    `maven-publish`
}

repositories {
    maven {
        url = uri("https://repository.jboss.org/nexus/content/groups/public")
    }
    mavenCentral()
    jcenter()
}

group = "com.cognifide.aet"
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