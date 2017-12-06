package org.sigmah.server.servlet.exporter;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.google.inject.Injector;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.server.servlet.base.ServletExecutionContext;
import org.sigmah.server.servlet.exporter.base.Exporter;
import org.sigmah.server.servlet.exporter.template.ExportTemplate;
import org.sigmah.server.servlet.exporter.utils.ContactsSynthesisCalcTemplate;
import org.sigmah.server.servlet.exporter.utils.ContactsSynthesisExcelTemplate;
import org.sigmah.server.servlet.exporter.utils.ContactsSynthesisUtils;
import org.sigmah.server.servlet.exporter.utils.ExportConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContactListExporter extends Exporter {

  /**
   * Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(ContactListExporter.class);

  public ContactListExporter(final Injector injector, final HttpServletRequest req, ServletExecutionContext context) throws Exception {
    super(injector, req, context);
  }

  @Override
  public String getFileName() {
    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
    return localize("contactList") + "_" + format.format(new Date()) + getExtention();
  }

  @Override
  public void export(OutputStream output) throws Exception {
    // The container id.
    final String containerIdString = requireParameter(RequestParameter.ID);
    Integer containerId;
    try {
      containerId = Integer.parseInt(containerIdString);
    } catch (NumberFormatException e) {
      LOG.error("[export] The id '" + containerIdString + "' is invalid.", e);
      throw new Exception("The id '" + containerIdString + "' is invalid.", e);
    }
    // The layout group id.
    final String layoutGroupIdString = requireParameter(RequestParameter.LAYOUT_GROUP_ID);
    Integer layoutGroupId;
    try {
      layoutGroupId = Integer.parseInt(layoutGroupIdString);
    } catch (NumberFormatException e) {
      LOG.error("[export] The id '" + layoutGroupIdString + "' is invalid.", e);
      throw new Exception("The id '" + layoutGroupIdString + "' is invalid.", e);
    }
    // The iteration id (can be null).
    final String iterationIdString = requireParameter(RequestParameter.ITERATION_ID);
    Integer iterationId;
    try {
      iterationId = Integer.parseInt(iterationIdString);
    } catch (NumberFormatException e) {
      LOG.error("[export] The id '" + iterationIdString + "' is invalid.", e);
      throw new Exception("The id '" + iterationIdString + "' is invalid.", e);
    }
    if (iterationId == -1) {
      iterationId = null;
    }
    // The contact list id (can be null).
    final String contactListIdString = requireParameter(RequestParameter.CONTACT_LIST_ID);
    Integer contactListId;
    try {
      contactListId = Integer.parseInt(contactListIdString);
    } catch (NumberFormatException e) {
      LOG.error("[export] The id '" + contactListIdString + "' is invalid.", e);
      throw new Exception("The id '" + contactListIdString + "' is invalid.", e);
    }

    try {
      // data
      List<ContactsSynthesisUtils.ContactSheetData> contactSheetDatas = ContactsSynthesisUtils.createContactListData(containerId, layoutGroupId, contactListId, iterationId, this, getI18ntranslator(), getLanguage());

      ExportTemplate template;
      switch (exportFormat) {

        case XLS: {
          final HSSFWorkbook wb = new HSSFWorkbook();
          template = new ContactsSynthesisExcelTemplate(contactSheetDatas, wb, ExportConstants.CONTACT_SHEET_PREFIX);
          ((ContactsSynthesisExcelTemplate) template).generate();
        }
        break;

        case ODS: {
          final SpreadsheetDocument doc = SpreadsheetDocument.newSpreadsheetDocument();
          template = new ContactsSynthesisCalcTemplate(contactSheetDatas, doc, ExportConstants.CONTACT_SHEET_PREFIX);
          ((ContactsSynthesisCalcTemplate)template).generate();
        }
        break;

        default:
          LOG.error("[export] The export format '" + exportFormat + "' is unknown.");
          throw new ServletException("The export format '" + exportFormat + "' is unknown.");
      }
      template.write(output);

    } catch (Throwable e) {
      LOG.error("[export] Error during the workbook writing.", e);
      throw new Exception("Error during the workbook writing.", e);
    }
  }
}
