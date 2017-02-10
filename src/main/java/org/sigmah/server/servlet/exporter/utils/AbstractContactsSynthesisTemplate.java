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
