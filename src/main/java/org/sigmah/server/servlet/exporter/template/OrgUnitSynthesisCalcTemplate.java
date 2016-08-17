package org.sigmah.server.servlet.exporter.template;

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

import com.google.inject.Injector;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.i18n.I18nServer;
import org.sigmah.server.servlet.base.ServletExecutionContext;
import org.sigmah.server.servlet.exporter.data.OrgUnitSynthesisData;
import org.sigmah.shared.Language;

/**
 * @author sherzod (v1.3)
 */
public class OrgUnitSynthesisCalcTemplate extends BaseSynthesisCalcTemplate {

	public OrgUnitSynthesisCalcTemplate(final OrgUnitSynthesisData data, final SpreadsheetDocument doc, final ServletExecutionContext context, final I18nServer i18nTranslator, final Language language, final Injector injector) throws Throwable {
		super(data, doc, OrgUnit.class, context, i18nTranslator, language, injector);
	}

}
