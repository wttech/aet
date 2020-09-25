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
import com.cognifide.aet.accessibility.report.models.XslxColumnModel
import com.cognifide.aet.accessibility.report.service.AccessibilityReportService
import org.apache.poi.common.usermodel.HyperlinkType
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger

class XslxFileWriter(task: AccessibilityReportService.ServiceTask) : BaseFileWriter {
  private val workbook: Workbook = XSSFWorkbook()
  private val sheet: Sheet = workbook.createSheet("Accessibility Issues - ${task.filename}")
  private val rowsCounter: AtomicInteger = AtomicInteger()


  override fun writeHeader() {
    val headerRow = sheet.createRow(0)
    rowsCounter.incrementAndGet()

    XslxColumnModel.forEach { column ->
      val cell = headerRow.createCell(column.ordinal)
      cell.setCellValue(column.columnName)
    }
  }

  override fun writeCodeHeader(code: String) {
    // noop
  }

  override fun writeMessage(message: String) {
    // noop
  }

  override fun writeRow(reportRow: ReportRow) {
    val row = sheet.createRow(rowsCounter.getAndIncrement())
    row.createCell(XslxColumnModel.ERROR_CODE.ordinal).setCellValue(reportRow.code)
    row.createCell(XslxColumnModel.MESSAGE.ordinal).setCellValue(reportRow.message)
    row.createCell(XslxColumnModel.PATH.ordinal).setCellValue(reportRow.path)

    val link = workbook.creationHelper.createHyperlink(HyperlinkType.URL)
    link.address = reportRow.url

    val linkCell = row.createCell(XslxColumnModel.URL.ordinal)
    linkCell.hyperlink = link
    linkCell.setCellValue(reportRow.url)

    row.createCell(XslxColumnModel.LINE_NUMBER.ordinal).setCellValue(reportRow.lineNumber.toDouble())
    row.createCell(XslxColumnModel.SNIPPET.ordinal).setCellValue(reportRow.snippet)
    row.createCell(XslxColumnModel.SOLUTIONS.ordinal).setCellValue(reportRow.solutions)
  }

  override fun writeIssueSeparator() {
    // noop
  }

  override fun writeSolutions(solutions: String) {
    // noop
  }

  @Throws(IOException::class)
  override fun toByteArray(): ByteArray {
    // FIXME: will not work under current docker image due to a bug with X & fonts
    // This should be retested after updating alpine jdk8 image
    // XslxColumnModel.forEach { column -> sheet.autoSizeColumn(column.ordinal) }

    sheet.setAutoFilter(
        CellRangeAddress(0, rowsCounter.get(), 0, XslxColumnModel.itemsTotal))

    return ByteArrayOutputStream()
        .also { workbook.write(it) }
        .toByteArray()
  }
}
