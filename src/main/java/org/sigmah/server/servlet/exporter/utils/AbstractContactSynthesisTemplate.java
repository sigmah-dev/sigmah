package org.sigmah.server.servlet.exporter.utils;

import java.util.List;

import org.sigmah.server.servlet.exporter.data.cells.ExportDataCell;
import org.sigmah.server.servlet.exporter.template.ExportTemplate;

public abstract class AbstractContactSynthesisTemplate implements ExportTemplate {

  private final List<ContactSynthesisUtils.ContactSheetData> data;

  public AbstractContactSynthesisTemplate(final List<ContactSynthesisUtils.ContactSheetData> data) throws Throwable {
    this.data = data;
  }

  public void generate() throws Throwable {

    for (ContactSynthesisUtils.ContactSheetData contactSheetData : data) {

      if (contactSheetData.getLines().isEmpty()) {
        return;
      }

      createSheet(ExportConstants.CONTACT_SHEET_PREFIX + contactSheetData.getTitle());

      int rowIndex = 0;
      int colIndex = 0;

      // headers
      for (ExportDataCell header : contactSheetData.getHeaders()) {
        putDataCell(0, colIndex++, header);
      }

      rowIndex++;

      // lines
      for (List<ExportDataCell> line : contactSheetData.getLines()) {
        colIndex = 0;
        for (ExportDataCell cell : line) {
          putDataCell(rowIndex, colIndex++, cell);
        }
        rowIndex++;
      }
    }
  }

  protected abstract void createSheet(String title) throws Throwable ;

  protected abstract void putDataCell(final int rowIndex, final int colIndex, final ExportDataCell dataCell) throws Throwable ;
}
