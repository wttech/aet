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

import com.cognifide.aet.accessibility.report.models.ReportRow
import java.io.IOException

interface BaseFileWriter {

  fun writeHeader()

  fun writeCodeHeader(code: String)

  fun writeMessage(message: String)

  fun writeRow(reportRow: ReportRow)

  fun writeIssueSeparator()

  fun writeSolutions(solutions: String)

  @Throws(IOException::class)
  fun toByteArray(): ByteArray
}
