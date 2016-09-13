package org.sigmah.server.servlet.exporter.utils;

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
