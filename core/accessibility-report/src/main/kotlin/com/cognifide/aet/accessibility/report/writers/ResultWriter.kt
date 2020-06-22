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
package com.cognifide.aet.accessibility.report.writers

import com.cognifide.aet.accessibility.report.models.AccessibilityIssue
import com.cognifide.aet.accessibility.report.models.FileType
import com.cognifide.aet.accessibility.report.models.ReportRow
import com.cognifide.aet.accessibility.report.service.AccessibilityReportService.ServiceTask
import org.apache.commons.collections4.CollectionUtils
import java.io.IOException

class ResultWriter(private val writer: BaseFileWriter) {

  @Throws(IOException::class)
  fun write(task: ServiceTask, issueList: List<AccessibilityIssue>): ByteArray {
    if (CollectionUtils.isEmpty(issueList)) return ByteArray(0)

    // change mime type only on success
    return writeToFile(issueList)
        .also { task.setter.accept(task.fileType.mimeType) }
  }

  @Throws(IOException::class)
  private fun writeToFile(issueList: List<AccessibilityIssue>): ByteArray {
    writer.writeHeader()

    issueList
        .groupBy { it.getAccessibilityCode().code }
        .forEach { (code, codeIssues) ->
          val joinedSolutions = getSolutionsForIssue(codeIssues)

          writer.writeCodeHeader(code)

          codeIssues
              .groupBy { it.message }
              .forEach { (message, messageIssues) ->
                writer.writeMessage(message)

                messageIssues
                    .groupBy { it.url }
                    .entries
                    .sortedBy { it.key }
                    .forEach { entry ->
                      entry.value
                          .sortedBy { it.lineNumber }
                          .forEach { issue ->
                            ReportRow(code, message, issue, joinedSolutions)
                                .let { writer.writeRow(it) }
                          }

                      writer.writeIssueSeparator()
                    }
              }

          writer.writeSolutions(joinedSolutions)
        }

    return writer.toByteArray()
  }

  private fun getSolutionsForIssue(issues: List<AccessibilityIssue>): String {
    return issues
        .flatMap { it.getAccessibilityCode().solutions }
        .distinct()
        .joinToString(", ")
  }

  companion object Factory {
    fun forTask(task: ServiceTask): ResultWriter {
      val filename = task.filename //fixme should this be filename or sth else?

      val writer: BaseFileWriter =
          when (task.fileType) {
            FileType.TEXT -> PlainFileWriter(task)
            FileType.XSLX -> XslxFileWriter(task)
          }

      return ResultWriter(writer)
    }
  }
}
