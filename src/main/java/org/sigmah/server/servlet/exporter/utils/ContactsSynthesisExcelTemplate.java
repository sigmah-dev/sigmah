package org.sigmah.server.servlet.exporter.utils;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2017 Groupe URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.io.OutputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.sigmah.server.servlet.exporter.data.cells.ExportDataCell;
import org.sigmah.server.servlet.exporter.data.cells.ExportLinkCell;

public class ContactsSynthesisExcelTemplate extends AbstractContactsSynthesisTemplate {

  private final HSSFWorkbook wb;
  private final ExcelUtils utils;
  private HSSFSheet currentSheet;

  public ContactsSynthesisExcelTemplate(final List<ContactsSynthesisUtils.ContactSheetData> data, final HSSFWorkbook wb, final String prefix) throws Throwable {
    super(data, prefix);
    this.wb = wb;
    this.utils = new ExcelUtils(wb);
  }

  public ContactsSynthesisExcelTemplate(final ContactsSynthesisUtils.ContactSheetData data, final HSSFWorkbook wb, final String prefix) throws Throwable {
    super(data, prefix);
    this.wb = wb;
    this.utils = new ExcelUtils(wb);
  }

  @Override
  protected void createSheet(String title) {
    currentSheet = wb.createSheet(title);
  }

  @Override
  protected void putDataCell(final int rowIndex, final int colIndex, final ExportDataCell dataCell) {
    if (dataCell != null) {
      if (currentSheet.getRow(rowIndex) == null) {
        currentSheet.createRow(rowIndex);
      }

      if (dataCell instanceof ExportLinkCell) {
        utils.createLinkCell(currentSheet.getRow(rowIndex).createCell(colIndex), dataCell.toCSVString(), ((ExportLinkCell) dataCell).getTarget(), true);
      } else {
        utils.putBorderedBasicCell(currentSheet, rowIndex, colIndex, dataCell.toCSVString());
      }
    }
  }

  @Override
  public void write(OutputStream output) throws Throwable {
    wb.write(output);
  }
}
