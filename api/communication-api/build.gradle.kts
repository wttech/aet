plugins {
    id("com.cognifide.aet.java-conventions")
	id("net.idlestate.gradle-duplicate-classes-check")
    id("biz.aQute.bnd.builder")
}

dependencies {
    testImplementation("com.googlecode.zohhak:zohhak:1.1.1")
    testImplementation("junit:junit:4.11")
    testImplementation("org.hamcrest:hamcrest-all:1.3")
    testImplementation("commons-io:commons-io:2.6")
    testImplementation("com.google.guava:guava:25.1-jre")
    implementation("javax.validation:validation-api:1.1.0.Final")
    implementation("org.hibernate:hibernate-validator:4.3.2.Final")
    implementation("com.google.code.gson:gson:2.8.5")
    compileOnly("org.apache.activemq:activemq-osgi:5.15.2")
    compileOnly("org.apache.commons:commons-lang3:3.7")
    compileOnly("org.jboss.logging:jboss-logging:3.3.2.Final")
    compileOnly("commons-io:commons-io:2.6")
}

tasks.jar {
    manifest {
        attributes(
            Pair("Bundle-Vendor", "Cognifide Ltd."),
            Pair(
                "Export-Package",
                "com.cognifide.aet.communication.api;version='3.4.1',com.cognifide.aet.communication.api.exceptions;version='3.4.1',com.cognifide.aet.communication.api.execution;version='3.4.1';uses:='com.cognifide.aet.communication.api.messages',com.cognifide.aet.communication.api.job;version='3.4.1';uses:='com.cognifide.aet.communication.api,com.cognifide.aet.communication.api.metadata',com.cognifide.aet.communication.api.messages;version='3.4.1';uses:='com.cognifide.aet.communication.api',com.cognifide.aet.communication.api.metadata;version='3.4.1';uses:='com.cognifide.aet.communication.api.metadata.exclude,javax.validation',com.cognifide.aet.communication.api.metadata.exclude;version='3.4.1',com.cognifide.aet.communication.api.metadata.gson;version='3.4.1';uses:='com.cognifide.aet.communication.api.metadata,com.google.gson',com.cognifide.aet.communication.api.queues;version='3.4.1';uses:='javax.jms',com.cognifide.aet.communication.api.util;version='3.4.1';uses:='com.cognifide.aet.communication.api.metadata,javax.validation',com.cognifide.aet.communication.api.wrappers;version='3.4.1';uses:='com.cognifide.aet.communication.api.metadata'"
            ),
            Pair(
                "Import-Package",
                "com.cognifide.aet.communication.api;version='[3.4,4)',com.cognifide.aet.communication.api.messages;version='[3.4,4)',com.cognifide.aet.communication.api.metadata;version='[3.4,4)',com.cognifide.aet.communication.api.metadata.exclude;version='[3.4,4)',com.cognifide.aet.communication.api.metadata.gson;version='[3.4,4)',com.cognifide.aet.communication.api.util;version='[3.4,4)',com.google.common.base;version='[25.1,26)',com.google.common.collect;version='[25.1,26)',com.google.gson;version='[2.8,3)',com.google.gson.reflect;version='[2.8,3)',javax.jms;version='[1.1,2)',javax.validation;version='[1.1,2)',javax.validation.bootstrap;version='[1.1,2)',javax.validation.constraints;version='[1.1,2)',javax.validation.spi;version='[1.1,2)',org.apache.commons.lang3;version='[3.7,4)',org.apache.commons.lang3.builder;version='[3.7,4)',org.hibernate.validator;version='[4.3,5)',org.hibernate.validator.constraints;version='[4.3,5)',org.slf4j;version='[1.7,2)'"
            )
        )
    }
}

description = "AET :: API :: Communication API"
