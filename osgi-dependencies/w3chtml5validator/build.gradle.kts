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
                "Embedded-Artifacts",
                "validator-17.11.1.jar;g=\"nu.validator\";a=\"validator\";v=\"17.11.1\",langdetect-1.1-20120112.jar;g=\"com.cybozu.labs\";a=\"langdetect\";v=\"1.1-20120112\",salvation-2.3.0.jar;g=\"com.shapesecurity\";a=\"salvation\";v=\"2.3.0\",jsonic-1.3.9.jar;g=\"net.arnx\";a=\"jsonic\";v=\"1.3.9\",jetty-util-ajax-9.2.9.v20150224.jar;g=\"org.eclipse.jetty\";a=\"jetty-util-ajax\";v=\"9.2.9.v20150224\",icu4j-58.2.jar;g=\"com.ibm.icu\";a=\"icu4j\";v=\"58.2\",galimatias-0.1.2.jar;g=\"nu.validator\";a=\"galimatias\";v=\"0.1.2\",isorelax-20030108.jar;g=\"isorelax\";a=\"isorelax\";v=\"20030108\",Saxon-HE-9.6.0-4.jar;g=\"net.sf.saxon\";a=\"Saxon-HE\";v=\"9.6.0-4\",htmlparser-1.4.7.jar;g=\"nu.validator\";a=\"htmlparser\";v=\"1.4.7\",jing-20171006VNU.jar;g=\"nu.validator\";a=\"jing\";v=\"20171006VNU\",rhino-1.7R5.jar;g=\"org.mozilla\";a=\"rhino\";v=\"1.7R5\",xom-1.1.jar;g=\"xom\";a=\"xom\";v=\"1.1\",jchardet-1.0.jar;g=\"net.sourceforge.jchardet\";a=\"jchardet\";v=\"1.0\""
            ),
            Pair(
                "Include-Resource",
                "validator-17.11.1.jar;g=\"nu.validator\";a=\"validator\";v=\"17.11.1\",langdetect-1.1-20120112.jar;g=\"com.cybozu.labs\";a=\"langdetect\";v=\"1.1-20120112\",salvation-2.3.0.jar;g=\"com.shapesecurity\";a=\"salvation\";v=\"2.3.0\",jsonic-1.3.9.jar;g=\"net.arnx\";a=\"jsonic\";v=\"1.3.9\",jetty-util-ajax-9.2.9.v20150224.jar;g=\"org.eclipse.jetty\";a=\"jetty-util-ajax\";v=\"9.2.9.v20150224\",icu4j-58.2.jar;g=\"com.ibm.icu\";a=\"icu4j\";v=\"58.2\",galimatias-0.1.2.jar;g=\"nu.validator\";a=\"galimatias\";v=\"0.1.2\",isorelax-20030108.jar;g=\"isorelax\";a=\"isorelax\";v=\"20030108\",Saxon-HE-9.6.0-4.jar;g=\"net.sf.saxon\";a=\"Saxon-HE\";v=\"9.6.0-4\",htmlparser-1.4.7.jar;g=\"nu.validator\";a=\"htmlparser\";v=\"1.4.7\",jing-20171006VNU.jar;g=\"nu.validator\";a=\"jing\";v=\"20171006VNU\",rhino-1.7R5.jar;g=\"org.mozilla\";a=\"rhino\";v=\"1.7R5\",xom-1.1.jar;g=\"xom\";a=\"xom\";v=\"1.1\",jchardet-1.0.jar;g=\"net.sourceforge.jchardet\";a=\"jchardet\";v=\"1.0\""
            ),
            Pair(
                "Bundle-ClassPath",
                ".,validator-17.11.1.jar,langdetect-1.1-20120112.jar,salvation-2.3.0.jar,jsonic-1.3.9.jar,jetty-util-ajax-9.2.9.v20150224.jar,icu4j-58.2.jar,galimatias-0.1.2.jar,isorelax-20030108.jar,Saxon-HE-9.6.0-4.jar,htmlparser-1.4.7.jar,jing-20171006VNU.jar,rhino-1.7R5.jar,xom-1.1.jar,jchardet-1.0.jar"
            ),
            Pair(
                "Import-Package",
                "com.icl.saxon;resolution:=optional,com.sun.org.apache.xerces.internal.parsers;resolution:=optional,com.sun.org.apache.xerces.internal.util;resolution:=optional,com.sun.org.apache.xerces.internal.xni.parser;resolution:=optional,jp.co.swiftinc.relax.schema;resolution:=optional,jp.co.swiftinc.relax.verifier;resolution:=optional,junit.framework;resolution:=optional,org.apache.xmlbeans;resolution:=optional,org.apache.xerces.impl;resolution:=optional,org.apache.xerces.impl.validation;resolution:=optional,org.apache.xerces.impl.xpath.regex;resolution:=optional,org.apache.xerces.impl.xs;resolution:=optional,org.apache.xerces.parsers;resolution:=optional,org.apache.xerces.util;resolution:=optional,org.apache.xerces.xni;resolution:=optional,org.apache.xerces.xni.grammars;resolution:=optional,org.apache.xerces.xni.parser;resolution:=optional,com.google.inject;resolution:=optional,org.fest.assertions;resolution:=optional,org.junit;resolution:=optional,org.junit.experimental.theories;resolution:=optional,org.junit.rules;resolution:=optional,org.junit.runner;resolution:=optional,org.junit.runners;resolution:=optional,org.seasar.framework.container;resolution:=optional,org.seasar.framework.container.factory;resolution:=optional,org.seasar.framework.log;resolution:=optional,org.springframework.web.context;resolution:=optional,org.springframework.web.context.support;resolution:=optional,javax.annotation,javax.net.ssl,javax.script,javax.servlet;version=\"[3.1,4)\",javax.servlet.http;version=\"[3.1,4)\",javax.swing,javax.swing.border,javax.swing.event,javax.swing.filechooser,javax.swing.table,javax.swing.text,javax.swing.tree,javax.xml.datatype,javax.xml.namespace,javax.xml.parsers,javax.xml.stream,javax.xml.stream.events,javax.xml.transform,javax.xml.transform.dom,javax.xml.transform.sax,javax.xml.transform.stax,javax.xml.transform.stream,javax.xml.xpath,nu.validator.checker.table,nu.validator.collections,nu.validator.datatype.data,nu.validator.datatype.tools,nu.validator.gnu.xml.aelfred2,nu.validator.htmlparser.common,nu.validator.htmlparser.impl,nu.validator.htmlparser.io,nu.validator.htmlparser.rewindable,nu.validator.htmlparser.sax,nu.validator.json,nu.validator.localentities,nu.validator.messages.types,nu.validator.saxtree,nu.validator.source,org.apache.commons.beanutils,org.apache.commons.fileupload;version=\"[1.3,2)\",org.apache.commons.fileupload.servlet;version=\"[1.3,2)\",org.apache.commons.logging;version=\"[1.2,2)\",org.apache.http,org.apache.http.client,org.apache.http.client.config,org.apache.http.client.methods,org.apache.http.config,org.apache.http.conn,org.apache.http.conn.socket,org.apache.http.conn.ssl,org.apache.http.impl.client,org.apache.http.impl.conn,org.apache.log4j;version=\"[1.2,2)\",org.apache.tools.ant,org.apache.tools.ant.types,org.apache.xalan.processor;resolution:=optional,org.apache.xml.resolver,org.apache.xml.resolver.helpers,org.eclipse.jetty.server;version=\"[9.3,10)\",org.eclipse.jetty.servlet,org.eclipse.jetty.servlets;version=\"[9.3,10)\",org.eclipse.jetty.util;version=\"[9.3,10)\",org.eclipse.jetty.util.log;version=\"[9.3,10)\",org.eclipse.jetty.util.thread;version=\"[9.3,10)\",org.slf4j,org.springframework.context,org.xml.sax,org.xml.sax.ext,org.xml.sax.helpers,javax.portlet;resolution:=optional,org.gjt.xpp;resolution:=optional,org.xmlpull.v1;resolution:=optional"
            )
        )
    }
}

description = "AET :: OSGi Dependencies :: HTML validator"
