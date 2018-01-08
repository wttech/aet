/**
 * AET
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.cognifide.aet.job.common.datafilters.removenodes;

import com.cognifide.aet.job.api.datafilter.AbstractDataModifierJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class RemoveNodesDataModifier extends AbstractDataModifierJob<String> {

  public static final String NAME = "remove-nodes";

  private static final String PARAM_XPATH = "xpath";

  private XPathExpression expr;

  private String xpathString;

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    if (params.containsKey(PARAM_XPATH)) {
      xpathString = params.get(PARAM_XPATH);
      XPathFactory xPathfactory = XPathFactory.newInstance();
      XPath xpath = xPathfactory.newXPath();
      try {
        expr = xpath.compile(xpathString);
      } catch (XPathExpressionException e) {
        throw new ParametersException(e.getMessage(), e);
      }
    } else {
      throw new ParametersException("XPath must be provided");
    }

  }

  @Override
  public String modifyData(String source) throws ProcessingException {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    Document document = getDocument(source, dbf);
    removeNodes(document);
    return transform(document).getWriter().toString();
  }

  @Override
  public String getInfo() {
    return NAME + " DataModifier with parameters: " + PARAM_XPATH + ": " + xpathString;
  }

  private StreamResult transform(Document document) throws ProcessingException {
    StreamResult result = new StreamResult(new StringWriter());
    try {
      DOMSource domSource = new DOMSource(document);
      TransformerFactory tf = TransformerFactory.newInstance();
      Transformer transformer = tf.newTransformer();
      transformer.transform(domSource, result);
    } catch (TransformerException e) {
      throw new ProcessingException(e.getMessage(), e);
    }
    return result;
  }

  private void removeNodes(Document document) throws ProcessingException {
    try {
      NodeList nl = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
      for (int i = 0; i < nl.getLength(); i++) {
        Node node = nl.item(i);
        node.getParentNode().removeChild(node);
      }
    } catch (XPathExpressionException e) {
      throw new ProcessingException(e.getMessage(), e);
    }
  }

  private Document getDocument(String source, DocumentBuilderFactory dbf)
      throws ProcessingException {
    try {
      DocumentBuilder db = dbf.newDocumentBuilder();
      return db.parse(IOUtils.toInputStream(source, "UTF-8"));
    } catch (IOException | ParserConfigurationException | SAXException e) {
      throw new ProcessingException(e.getMessage(), e);
    }
  }
}
