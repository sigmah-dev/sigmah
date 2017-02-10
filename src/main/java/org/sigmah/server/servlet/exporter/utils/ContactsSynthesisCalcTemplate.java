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

import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Table;
import org.sigmah.server.servlet.exporter.data.cells.ExportDataCell;
import org.sigmah.server.servlet.exporter.data.cells.ExportLinkCell;

public class ContactsSynthesisCalcTemplate extends AbstractContactsSynthesisTemplate {

  private SpreadsheetDocument doc;
  private Table currentSheet;

  public ContactsSynthesisCalcTemplate(final List<ContactsSynthesisUtils.ContactSheetData> data, final SpreadsheetDocument doc, final String prefix) throws Throwable {
    super(data, prefix);

    this.doc = doc;
  }

  public ContactsSynthesisCalcTemplate(final ContactsSynthesisUtils.ContactSheetData data, final SpreadsheetDocument doc, final String prefix) throws Throwable {
    super(data, prefix);

    this.doc = doc;
  }

  @Override
  protected void createSheet(String title) throws Throwable {

    if (doc == null) {
      doc = SpreadsheetDocument.newSpreadsheetDocument();
      currentSheet = doc.getSheetByIndex(0);
      currentSheet.setTableName(title);
    } else {
      currentSheet = doc.appendSheet(title);
    }
  }

  @Override
  protected void putDataCell(final int rowIndex, final int colIndex, final ExportDataCell dataCell) throws Throwable {
    if (dataCell instanceof ExportLinkCell) {
      CalcUtils.applyLink(currentSheet.getCellByPosition(colIndex, rowIndex), dataCell.toCSVString(), ((ExportLinkCell)dataCell).getTarget());
    } else {
      CalcUtils.createBasicCell(currentSheet, colIndex, rowIndex, dataCell.toCSVString());
    }
  }

  @Override
  public void write(OutputStream output) throws Throwable {
    doc.save(output);
    doc.close();
  }
}
