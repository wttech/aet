/*
 * AET
 *
 * Copyright (C) 2020 Cognifide Limited
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
package com.cognifide.aet.accessibility.report.models

import java.io.Serializable
import java.util.Objects

class AccessibilityIssue(
    val type: IssueType,
    val message: String,
    val code: String,
    val elementString: String,
    val elementStringAbbreviated: String) : Serializable {

  var lineNumber = 0
  var columnNumber = 0
  var isExcluded = false
    private set
  var url: String = ""

  fun getAccessibilityCode(): AccessibilityCode = AccessibilityCode(code)

  override fun equals(other: Any?): Boolean {
    if (this === other) {
      return true
    }
    if (other == null || javaClass != other.javaClass) {
      return false
    }

    val that = other as AccessibilityIssue
    return (lineNumber == that.lineNumber
        && columnNumber == that.columnNumber
        && isExcluded == that.isExcluded
        && type == that.type
        && message == that.message
        && code == that.code
        && elementString == that.elementString
        && elementStringAbbreviated == that.elementStringAbbreviated
        && url == that.url)
  }

  override fun hashCode(): Int {
    return Objects.hash(
        type,
        message,
        code,
        elementString,
        elementStringAbbreviated,
        lineNumber,
        columnNumber,
        isExcluded,
        url)
  }

  enum class IssueType {
    ERROR, WARN, NOTICE, UNKNOWN;
  }

  companion object {
    private const val serialVersionUID = -53665467524179701L
  }
}
