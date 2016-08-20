package org.sigmah.server.servlet.exporter.data;

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


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.sigmah.server.domain.category.CategoryElement;
import org.sigmah.server.domain.category.CategoryType;
import org.sigmah.server.i18n.I18nServer;
import org.sigmah.server.servlet.base.ServletExecutionContext;
import org.sigmah.server.servlet.exporter.data.cells.GlobalExportDataCell;
import org.sigmah.server.servlet.exporter.data.cells.GlobalExportStringCell;
import org.sigmah.shared.Language;

import com.google.inject.Singleton;

/**
 * Utility class Provides global export data
 * 
 * @author sherzod (v1.3)
 */
@Singleton
public abstract class GlobalExportDataProvider {

	public abstract Map<String, List<GlobalExportDataCell[]>> generateGlobalExportData(final Integer organizationId, EntityManager entityManager, final I18nServer i18nTranslator,
			final Language language, final ServletExecutionContext context) throws Exception;

	protected void addCategories(Set<CategoryType> categories, Map<String, List<GlobalExportDataCell[]>> exportDataMap, I18nServer i18nTranslator, Language language) {
		for(CategoryType category : categories) {
			List<GlobalExportDataCell[]> data = new ArrayList<>();

			// titles
			GlobalExportDataCell[] row = new GlobalExportDataCell[2];
			row[0] = new GlobalExportStringCell(String.valueOf(i18nTranslator.t(language, "categoryElementId")));
			row[1] = new GlobalExportStringCell(String.valueOf(i18nTranslator.t(language, "categoryElementLabel")));
			data.add(row);

			for(CategoryElement c : category.getElements()) {
				row = new GlobalExportDataCell[2];
				row[0] = new GlobalExportStringCell(String.valueOf(c.getId()));
				row[1] = new GlobalExportStringCell(c.getLabel());

				data.add(row);
			}

			exportDataMap.put(i18nTranslator.t(language, "category") + " " + category.getLabel(), data);
		}
	}

}
