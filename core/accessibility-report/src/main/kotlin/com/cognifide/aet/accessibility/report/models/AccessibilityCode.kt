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

import java.util.Objects

class AccessibilityCode(accessibilityIssueCode: String) {
  val code: String
  val solutions: Iterable<String>

  init {
    val splitIssueCode = accessibilityIssueCode.split(".")
    val issueCode = splitIssueCode[3]
    val splitCode = issueCode.split("_")
    this.code = "${splitCode[0]}_${splitCode[1]}_${splitCode[2]}\n"

    this.solutions = splitIssueCode[4].split(",").asIterable()
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) {
      return true
    }
    if (other == null || javaClass != other.javaClass) {
      return false
    }

    val that = other as AccessibilityCode
    return code == that.code
  }

  override fun hashCode(): Int {
    return Objects.hash(31, code.hashCode())
  }
}
