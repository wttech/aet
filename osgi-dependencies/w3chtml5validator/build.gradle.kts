plugins {
    id("com.cognifide.aet.java-conventions")
	id("com.cognifide.aet.test-coverage")
    id("biz.aQute.bnd.builder")
}

dependencies {
    implementation("nu.validator:validator:17.11.1")
    implementation("com.cybozu.labs:langdetect:1.1-20120112")
    implementation("com.shapesecurity:salvation:2.3.0")
    implementation("net.arnx:jsonic:1.3.9")
    implementation("org.eclipse.jetty:jetty-util-ajax:9.2.9.v20150224")
    implementation("com.ibm.icu:icu4j:58.2")
    implementation("nu.validator:galimatias:0.1.2")
    implementation("isorelax:isorelax:20030108")
    implementation("net.sf.saxon:Saxon-HE:9.6.0-4")
    implementation("nu.validator:htmlparser:1.4.7")
    implementation("nu.validator:jing:20171006VNU")
    implementation("org.mozilla:rhino:1.7R5")
    implementation("xom:xom:1.1")
    implementation("net.sourceforge.jchardet:jchardet:1.0")
    compileOnly("commons-fileupload:commons-fileupload:1.3.3")
    compileOnly("org.eclipse.jetty:jetty-http:9.3.14.v20161028")
    compileOnly("org.eclipse.jetty:jetty-io:9.3.14.v20161028")
    compileOnly("org.eclipse.jetty:jetty-security:9.3.14.v20161028")
    compileOnly("org.eclipse.jetty:jetty-server:9.3.14.v20161028")
    compileOnly("org.eclipse.jetty:jetty-servlets:9.3.14.v20161028")
    compileOnly("org.eclipse.jetty:jetty-util:9.3.14.v20161028")
}

tasks.jar {
    manifest {
        attributes(
            Pair("Bundle-Vendor", "Cognifide Ltd."),
            Pair("Export-Package", "nu.validator.*"),
            Pair(
                "Include-Resource",
                "validator-17.11.1.jar,langdetect-1.1-20120112.jar,salvation-2.3.0.jar,jsonic-1.3.9.jar,jetty-util-ajax-9.2.9.v20150224.jar,icu4j-58.2.jar,galimatias-0.1.2.jar,isorelax-20030108.jar,Saxon-HE-9.6.0-4.jar,htmlparser-1.4.7.jar,jing-20171006VNU.jar,rhino-1.7R5.jar,xom-1.1.jar,jchardet-1.0.jar"
            ),
            Pair(
                "Bundle-ClassPath",
                ".,validator-17.11.1.jar,langdetect-1.1-20120112.jar,salvation-2.3.0.jar,jsonic-1.3.9.jar,jetty-util-ajax-9.2.9.v20150224.jar,icu4j-58.2.jar,galimatias-0.1.2.jar,isorelax-20030108.jar,Saxon-HE-9.6.0-4.jar,htmlparser-1.4.7.jar,jing-20171006VNU.jar,rhino-1.7R5.jar,xom-1.1.jar,jchardet-1.0.jar"
            ),
            Pair(
                "Import-Package",
                "com.icl.saxon;resolution:=optional,com.sun.org.apache.xerces.internal.*;resolution:=optional,javax.portlet;resolution:=optional,jp.co.swiftinc.relax.*;resolution:=optional,junit.framework;resolution:=optional,org.gjt.xpp;resolution:=optional,org.xmlpull.v1;resolution:=optional,org.apache.xmlbeans;resolution:=optional,org.apache.xerces.*;resolution:=optional,com.google.inject;resolution:=optional,org.fest.assertions;resolution:=optional,org.junit.*;resolution:=optional,org.seasar.framework.*;resolution:=optional,org.springframework.web.context.*;resolution:=optional,*"
            )
        )
    }
}

description = "AET :: OSGi Dependencies :: HTML validator"
