/**
 * AET
 *
 * Copyright (C) 2018 Cognifide Limited
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
package com.cognifide.aet.job.common.comparators.source;

import com.cognifide.aet.job.common.comparators.source.visitors.ContentVisitor;
import com.cognifide.aet.job.common.comparators.source.visitors.MarkupVisitor;
import com.cognifide.aet.job.common.comparators.source.visitors.NodeTraversor;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

class CodeFormatter {

  private static final Pattern EMPTY_LINE_PATTERN = Pattern.compile("^[\\s]*$");

  private static final String NEWLINE = "\\r?\\n";

  String format(String code, SourceCompareType sourceCompareType) {
    String result;
    switch (sourceCompareType) {
      case MARKUP:
        result = formatCodeMarkup(code);
        break;
      case CONTENT:
        result = formatCodeContent(code);
        break;
      case ALLFORMATTED:
        result = formatCodeAllFormatted(code);
        break;
      default:
        result = code;
        break;
    }
    return result;
  }

  private String formatCodeMarkup(String code) {
    Document doc = Jsoup.parse(code);
    NodeTraversor traversor = new NodeTraversor(new MarkupVisitor());
    traversor.traverse(doc);
    return doc.html();
  }

  private String formatCodeContent(String code) {
    Document doc = Jsoup.parse(code);
    ContentVisitor visitor = new ContentVisitor();
    NodeTraversor traversor = new NodeTraversor(visitor);
    traversor.traverse(doc);
    return visitor.getFormattedText();
  }

  private String formatCodeAllFormatted(String code) {
    Document doc = Jsoup.parse(code);
    return removeEmptyLines(doc.outerHtml());
  }

  // package scoped for unit test
  String removeEmptyLines(String source) {
    String[] lines = source.split(NEWLINE);
    return Arrays
        .stream(lines)
        .filter(line -> !EMPTY_LINE_PATTERN.matcher(line).matches())
        .collect(Collectors.joining("\n"));
  }

}
