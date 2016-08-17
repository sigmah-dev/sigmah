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
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.sigmah.client.page.RequestParameter;
import org.sigmah.server.dao.impl.GlobalExportSettingsHibernateDAO;
import org.sigmah.server.domain.export.GlobalExportSettings;
import org.sigmah.server.servlet.base.ServletExecutionContext;
import org.sigmah.server.servlet.exporter.base.Exporter;
import org.sigmah.server.servlet.exporter.data.GlobalExportData;
import org.sigmah.server.servlet.exporter.data.GlobalExportDataProvider;
import org.sigmah.server.servlet.exporter.data.cells.GlobalExportDataCell;
import org.sigmah.server.servlet.exporter.template.ExportTemplate;
import org.sigmah.server.servlet.exporter.template.GlobalExportCalcTemplate;
import org.sigmah.server.servlet.exporter.template.GlobalExportExcelTemplate;
import org.sigmah.shared.util.ExportUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;

/**
 * Exporter for Projects list exportation
 * 
 * @author sherzod (v1.3)
 */
public class GlobalExportExporter extends Exporter {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(GlobalExportExporter.class);

	public GlobalExportExporter(final Injector injector, final HttpServletRequest req, ServletExecutionContext context) throws Exception {
		super(injector, req, context);
	}

	@Override
	public String getFileName() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		return localize("globalExport") + "_" + format.format(new Date()) + getExtention();
	}

	@Override
	public void export(OutputStream output) throws Exception {
		try {
			final GlobalExportData data = prepareData();

			ExportTemplate template = null;
			switch (exportFormat) {
				case XLS: {
					template = new GlobalExportExcelTemplate(data);
				}
					break;
				case ODS: {
					template = new GlobalExportCalcTemplate(data);
				}
					break;
				default:
					LOG.error("[export] The export format '" + exportFormat + "' is unknown.");
					throw new ServletException("The export format '" + exportFormat + "' is unknown.");
			}
			template.write(output);
		} catch (Throwable e) {
			LOG.error("[export] Error during the workbook writing.", e);
			throw new Exception("Error during the workbook writing.");
		}
	}

	@Override
	public String getExtention() {

		try {
			Integer organizationId = Integer.parseInt(requireParameter(RequestParameter.ID));
			// data format
			final GlobalExportSettingsHibernateDAO exportSettingDao = injector.getInstance(GlobalExportSettingsHibernateDAO.class);
			final GlobalExportSettings settings = exportSettingDao.getGlobalExportSettingsByOrganization(organizationId);

			exportFormat = settings.getExportFormat();
			if (exportFormat == null)
				exportFormat = settings.getDefaultOrganizationExportFormat();

		} catch (Exception e) {
			LOG.error("[export] Error during the workbook writing.", e);
		}
		return super.getExtention();
	}

	private GlobalExportData prepareData() throws Exception {

		Map<String, List<GlobalExportDataCell[]>> exportData = null;
		final GlobalExportDataProvider dataProvider = injector.getInstance(GlobalExportDataProvider.class);
		final EntityManager em = injector.getInstance(EntityManager.class);

		Integer organizationId = Integer.parseInt(requireParameter(RequestParameter.ID));

		// data version
		final String versionStr = requireParameter(RequestParameter.VERSION);
		final ExportUtils.ExportDataVersion version = ExportUtils.ExportDataVersion.valueOfOrNull(versionStr);

		switch (version) {
			case LIVE_DATA: {
				try {

					exportData = dataProvider.generateGlobalExportData(organizationId, em, getI18ntranslator(), getLanguage(), getContext());
				} catch (Exception e) {
					LOG.error("[export] Failed to generate global export data.", e);
					throw new Exception("Failed to generate global export data.");
				}
			}
				break;

			case BACKED_UP_DATA: {
				Integer globalExportId = Integer.parseInt(requireParameter(RequestParameter.GLOBAL_EXPORT_ID));
				exportData = dataProvider.getBackedupGlobalExportData(em, globalExportId);
			}
				break;
		}

		return new GlobalExportData(exportData);

	}

}
