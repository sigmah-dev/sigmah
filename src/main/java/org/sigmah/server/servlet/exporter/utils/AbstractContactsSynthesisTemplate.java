package org.sigmah.server.servlet.exporter.utils;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.server.servlet.exporter.data.cells.ExportDataCell;
import org.sigmah.server.servlet.exporter.template.ExportTemplate;

public abstract class AbstractContactsSynthesisTemplate implements ExportTemplate {

  private final List<ContactsSynthesisUtils.ContactSheetData> data;
  private final String prefix;

  public AbstractContactsSynthesisTemplate(final List<ContactsSynthesisUtils.ContactSheetData> data, final String prefix) throws Throwable {
    this.data = data;
    this.prefix = prefix;
  }

  public AbstractContactsSynthesisTemplate(final ContactsSynthesisUtils.ContactSheetData data, final String prefix) throws Throwable {
    this.data = new ArrayList<>();
    this.data.add(data);
    this.prefix = prefix;
  }

  public void generate() throws Throwable {

    for (ContactsSynthesisUtils.ContactSheetData contactSheetData : data) {

      if (contactSheetData.getLines().isEmpty()) {
        return;
      }

      createSheet(prefix + contactSheetData.getTitle());

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
