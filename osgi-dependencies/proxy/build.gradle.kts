plugins {
    id("com.cognifide.aet.java-conventions")
	id("com.cognifide.aet.test-coverage")
    id("biz.aQute.bnd.builder")
}

dependencies {
    implementation("com.github.detro:browsermob-proxy-client:0.1.3")
    projectCompile(project(":jobs-api"))
    projectCompile(project(":selenium"))
    compileOnly("org.osgi:org.osgi.core:4.3.0")
    compileOnly("org.osgi:org.osgi.service.component.annotations:1.3.0")
    compileOnly("org.osgi:org.osgi.annotation:6.0.0")
    compileOnly("org.osgi:org.osgi.service.metatype.annotations:1.3.0")
    compileOnly("org.json:json:20180130")
    compileOnly("com.google.code.gson:gson:2.8.5")
    compileOnly("org.apache.httpcomponents:httpclient:4.4")
}

tasks.jar {
    manifest {
        attributes(
            Pair("Bundle-Vendor", "Cognifide Ltd."),
            Pair("Export-Package", "com.cognifide.aet.proxy.*,com.github.detro.browsermobproxyclient.*"),
            Pair("Include-Resource", "browsermob-proxy-client-0.1.3.jar"),
            Pair("Bundle-ClassPath", ".,browsermob-proxy-client-0.1.3.jar"),
            Pair(
                "Import-Package",
                "com.cognifide.aet.job.api.collector;version='[3.4,4)',com.cognifide.aet.job.api.exceptions;version='[3.4,4)',com.cognifide.aet.proxy.configuration;version='[3.4,4)',com.cognifide.aet.proxy.exceptions;version='[3.4,4)',com.google.common.collect;version='[25.1,26)',com.google.gson;version='[2.8,3)',javax.xml.bind,org.apache.http,org.apache.http.client.entity,org.apache.http.client.methods,org.apache.http.client.utils,org.apache.http.entity,org.apache.http.impl.client,org.apache.http.message,org.browsermob.core.har;version='[3.4,4)',org.json;version='[20180130.0,20180131)',org.openqa.selenium;version='[3.8,4)',org.openqa.selenium.net;version='[3.8,4)',org.slf4j;version='[1.7,2)'"
            )
        )
    }
}

description = "AET :: OSGi Dependencies :: Proxy"
