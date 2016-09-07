package org.sigmah.server.servlet.exporter.utils;

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
