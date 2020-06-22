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
package com.cognifide.aet.accessibility.report.service

import com.cognifide.aet.accessibility.report.models.AccessibilityIssue
import com.cognifide.aet.accessibility.report.models.AccessibilityIssue.IssueType
import com.cognifide.aet.accessibility.report.models.AccessibilityReport
import com.cognifide.aet.accessibility.report.models.FileType
import com.cognifide.aet.accessibility.report.writers.ResultWriter
import com.cognifide.aet.vs.ArtifactsDAO
import com.cognifide.aet.vs.DBKey
import com.cognifide.aet.vs.MetadataDAO
import com.cognifide.aet.vs.StorageException
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference
import org.slf4j.LoggerFactory
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.function.Consumer
import java.util.stream.Collectors


@Component(service = [AccessibilityReportService::class], immediate = true)
class AccessibilityReportService {

  @Reference
  private lateinit var metadataDAO: MetadataDAO

  @Reference
  private lateinit var artifactsDAO: ArtifactsDAO

  fun newTask() = ServiceTask()

  private fun isAvailable(task: ServiceTask): Boolean =
      retrieveToMap(task.dbKey, task.correlationId)
          .isNotEmpty()

  private fun getReport(task: ServiceTask): ByteArray {
    val issuesForTask = issuesForTask(task)
    val resultWriter = ResultWriter.forTask(task)

    return resultWriter.write(task, issuesForTask)
  }

  private fun issuesForTask(task: ServiceTask): List<AccessibilityIssue> =
      retrieveToMap(task.dbKey, task.correlationId)
          .entries
          .flatMap { entry ->
            entry.value
                .mapNotNull { objectId ->
                  retrieve(task.dbKey, objectId)
                }
                .flatMap { report ->
                  report.nonExcludedIssues
                      .filter { it.type == task.verbosityLevel }
                      .onEach { it.url = entry.key }
                }
          }
          .sortedBy { it.lineNumber }

  private fun retrieveToMap(dbKey: DBKey, correlationId: String): Map<String, MutableList<String>> =
      metadataDAO
          .getSuite(dbKey, correlationId)
          .tests
          .stream() //operate on stream as we need the Java .collect() method
          .flatMap { test -> test.urls.stream() }
          .map { url ->
            Pair(
                url.domain + url.url,
                url.steps
                    .asSequence()
                    .filter { it.name == "accessibility" }
                    .map { it.comparators }
                    .flatMap { it.asSequence() }
                    .filter { it.type == "accessibility" }
                    .map { it.stepResult.artifactId }
                    .toMutableList()
            )
          }
          .filter { pair -> pair.second.isNotEmpty() }
          .collect(
              Collectors.toMap(
                  { it.first },
                  { it.second },
                  { left, right -> left.apply { this.addAll(right) } }))

  private fun retrieve(dbKey: DBKey, objectId: String): AccessibilityReport? =
      try {
        artifactsDAO.getJsonFormatArtifact(dbKey, objectId, AccessibilityReport::class.java)
      } catch (e: IOException) {
        LOG.error("Exception while retrieving report", e)
        null
      }

  inner class ServiceTask {
    lateinit var dbKey: DBKey
    lateinit var correlationId: String
    lateinit var verbosityLevel: IssueType
    lateinit var fileType: FileType
    lateinit var setter: Consumer<String>
    val filename: String by lazy {
      correlationId +
          "-" +
          verbosityLevel.toString().toLowerCase() +
          "-" +
          LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmss")) +
          "." +
          fileType.extension
    }

    fun withDbKey(dbKey: DBKey): ServiceTask {
      this.dbKey = dbKey
      return this
    }

    fun withObjectId(objectId: String): ServiceTask {
      this.correlationId = objectId
      return this
    }

    @Throws(IllegalArgumentException::class)
    fun withVerbosity(verbosityLevel: String): ServiceTask {
      this.verbosityLevel = IssueType.valueOf(verbosityLevel.toUpperCase())
      return this
    }

    @Throws(IllegalArgumentException::class)
    fun withExtension(extension: String): ServiceTask {
      this.fileType = FileType.fromExtension(extension)
      return this
    }

    fun withMimetypeSetter(setter: Consumer<String>): ServiceTask {
      this.setter = setter
      return this
    }

    @Throws(IOException::class, StorageException::class)
    fun invokeReport(): ByteArray = getReport(this)

    @Throws(IOException::class, StorageException::class)
    fun invokeIsAvailable(): Boolean = isAvailable(this)
  }

  companion object {
    private val LOG by lazy { LoggerFactory.getLogger(AccessibilityReportService::class.java) }
  }
}
