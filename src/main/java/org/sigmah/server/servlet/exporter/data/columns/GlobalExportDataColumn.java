package org.sigmah.server.servlet.exporter.data.columns;

import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;
import org.sigmah.server.dispatch.CommandHandler;
import org.sigmah.server.domain.base.EntityId;
import org.sigmah.server.domain.category.CategoryType;
import org.sigmah.server.i18n.I18nServer;
import org.sigmah.server.servlet.exporter.data.cells.ExportDataCell;
import org.sigmah.shared.Language;
import org.sigmah.shared.command.GetLayoutGroupIterations;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.dto.layout.LayoutGroupIterationDTO;

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

/**
 * Column of an export.
 * 
 * @author sherzod
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public abstract class GlobalExportDataColumn {
	
	/**
	 * Instance of the current container.
	 */
	protected EntityId<Integer> container;
	
	/**
	 * Name of the current model.
	 */
	protected String modelName;
	
	/**
	 * Categories defined in the current model.
	 */
	protected Set<CategoryType> categories;
	
	/**
	 * Instance of the <code>GetValue</code> command handler.
	 */
	protected CommandHandler<GetValue,ValueResult> valueHandler;
	
	/**
	 * Instance of the <code>GetLayoutGroupIterations</code> command handler.
	 */
	protected CommandHandler<GetLayoutGroupIterations, ListResult<LayoutGroupIterationDTO>> iterationsHandler;
	
	/**
	 * Access to localized strings.
	 */
	protected I18nServer i18nTranslator;
	
	/**
	 * Language of the current user.
	 */
	protected Language language;
	
	/**
	 * Instance of the entity manager.
	 */
	protected EntityManager entityManager;
	
	/**
	 * Columns to always add on the first line of the export.
	 */
	protected String[] initialColumns;
	
	/**
	 * Export the values of this column.
	 * 
	 * @param firstLine
	 *			<code>true</code> if the current row is the first line,
	 *			<code>false</code> otherwise.
	 * @param titles
	 *			List of titles.
	 * @param values
	 *			List of values.
	 * @param layoutGroupsData 
	 *			Map associating each group with its values.
	 */
	public abstract void export(final boolean firstLine, final List<ExportDataCell> titles, final List<ExportDataCell> values, final Map<String, List<ExportDataCell[]>> layoutGroupsData);
	
	public void setContainer(final EntityId<Integer> container) {
		this.container = container;
	}

	public void setModelName(final String modelName) {
		this.modelName = modelName;
	}

	public void setCategories(final Set<CategoryType> categories) {
		this.categories = categories;
	}

	public void setValueHandler(final CommandHandler<GetValue, ValueResult> valueHandler) {
		this.valueHandler = valueHandler;
	}

	public void setIterationsHandler(final CommandHandler<GetLayoutGroupIterations, ListResult<LayoutGroupIterationDTO>> iterationsHandler) {
		this.iterationsHandler = iterationsHandler;
	}

	public void setI18nTranslator(final I18nServer i18nTranslator) {
		this.i18nTranslator = i18nTranslator;
	}

	public void setLanguage(final Language language) {
		this.language = language;
	}

	public void setEntityManager(final EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void setInitialColumns(final String... initialColumns) {
		this.initialColumns = initialColumns;
	}

}
