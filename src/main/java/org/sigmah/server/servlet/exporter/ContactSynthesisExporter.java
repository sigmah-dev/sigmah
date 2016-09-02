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
import org.sigmah.server.servlet.exporter.data.ContactSynthesisData;
import org.sigmah.server.servlet.exporter.template.ContactSynthesisCalcTemplate;
import org.sigmah.server.servlet.exporter.template.ContactSynthesisExcelTemplate;
import org.sigmah.server.servlet.exporter.template.ExportTemplate;
import org.sigmah.server.servlet.exporter.utils.ContactsSynthesisCalcTemplate;
import org.sigmah.server.servlet.exporter.utils.ContactsSynthesisExcelTemplate;
import org.sigmah.server.servlet.exporter.utils.ContactsSynthesisUtils;
import org.sigmah.server.servlet.exporter.utils.ContactsSynthesisUtils.ContactSheetData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContactSynthesisExporter extends Exporter {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ContactSynthesisExporter.class);

	public ContactSynthesisExporter(final Injector injector, final HttpServletRequest req, ServletExecutionContext context) throws Exception {
		super(injector, req, context);
	}

	@Override
	public String getFileName() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		return localize("contactSynthesis") + "_" + format.format(new Date()) + getExtention();
	}

	@Override
	public void export(OutputStream output) throws Exception {
		// The contact id.
		final String idString = requireParameter(RequestParameter.ID);
		final Integer contactId;

		try {

			contactId = Integer.parseInt(idString);

		} catch (NumberFormatException e) {
			LOG.error("[export] The id '" + idString + "' is invalid.", e);
			throw new Exception("The id '" + idString + "' is invalid.", e);
		}

		try {

			// appending options
			final boolean withCharacteristics = "true".equals(requireParameter(RequestParameter.WITH_CHARACTERISTICS));
			final boolean withAllRelations = "true".equals(requireParameter(RequestParameter.WITH_ALL_RELATIONS));
			final boolean withFrameworkRelations = "true".equals(requireParameter(RequestParameter.WITH_FRAMEWORK_RELATIONS));
			final boolean withRelationsByElement = "true".equals(requireParameter(RequestParameter.WITH_RELATIONS_BY_ELEMENT));

			// data
			ContactSynthesisData synthesisData = null;
			ContactSheetData allRelationsExportData = null;
			List<ContactSheetData> frameworkRelationsExportData = null;
			List<ContactSheetData> relationsByElementsData = null;


			if (withCharacteristics) {
				synthesisData = prepareSynthesisData(contactId);
			}

			if (withAllRelations) {
				allRelationsExportData = ContactsSynthesisUtils.createAllRelationsData(contactId, this, getI18ntranslator(), getLanguage());
			}

			if (withFrameworkRelations) {
				frameworkRelationsExportData = ContactsSynthesisUtils.createFrameworkRelationsData(contactId, this, getI18ntranslator(), getLanguage());
			}

			if (withRelationsByElement) {
				relationsByElementsData = ContactsSynthesisUtils.createRelationsByElementData(contactId, this, getI18ntranslator(), getLanguage());
			}

			ExportTemplate template = null;
			switch (exportFormat) {

				case XLS: {
					final HSSFWorkbook wb = new HSSFWorkbook();
					if (synthesisData != null) {
						template = new ContactSynthesisExcelTemplate(synthesisData, wb, getContext(), getI18ntranslator(), getLanguage(), injector);
					}
					if (allRelationsExportData != null) {
						template = new ContactsSynthesisExcelTemplate(allRelationsExportData, wb, "");
						((ContactsSynthesisExcelTemplate) template).generate();
					}
					if (frameworkRelationsExportData != null) {
						template = new ContactsSynthesisExcelTemplate(frameworkRelationsExportData, wb, "");
						((ContactsSynthesisExcelTemplate) template).generate();
					}
					if (relationsByElementsData != null) {
						template = new ContactsSynthesisExcelTemplate(relationsByElementsData, wb, "");
						((ContactsSynthesisExcelTemplate) template).generate();
					}
				}
					break;

				case ODS: {
					final SpreadsheetDocument doc = SpreadsheetDocument.newSpreadsheetDocument();
					if (synthesisData != null) {
						template = new ContactSynthesisCalcTemplate(synthesisData, doc, getContext(), getI18ntranslator(), getLanguage(), injector);
					}
					if (allRelationsExportData != null) {
						template = new ContactsSynthesisCalcTemplate(allRelationsExportData, doc, "");
						((ContactsSynthesisCalcTemplate) template).generate();
					}
					if (frameworkRelationsExportData != null) {
						template = new ContactsSynthesisCalcTemplate(frameworkRelationsExportData, doc, "");
						((ContactsSynthesisCalcTemplate) template).generate();
					}
					if (relationsByElementsData != null) {
						template = new ContactsSynthesisCalcTemplate(relationsByElementsData, doc, "");
						((ContactsSynthesisCalcTemplate) template).generate();
					}
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

	private ContactSynthesisData prepareSynthesisData(Integer contactId) throws Throwable {
		return new ContactSynthesisData(this, contactId, injector);
	}

}
