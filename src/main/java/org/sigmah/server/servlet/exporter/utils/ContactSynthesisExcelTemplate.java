package org.sigmah.server.servlet.exporter.utils;

import java.io.OutputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.sigmah.server.servlet.exporter.data.cells.ExportDataCell;
import org.sigmah.server.servlet.exporter.data.cells.ExportLinkCell;

public class ContactSynthesisExcelTemplate extends AbstractContactSynthesisTemplate {

  private final HSSFWorkbook wb;
  private final ExcelUtils utils;
  private HSSFSheet currentSheet;

  public ContactSynthesisExcelTemplate(final List<ContactSynthesisUtils.ContactSheetData> data, final HSSFWorkbook wb) throws Throwable {
    super(data);
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
