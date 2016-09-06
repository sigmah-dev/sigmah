package org.sigmah.server.util;

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

import org.sigmah.server.domain.element.CheckboxElement;
import org.sigmah.server.domain.element.ComputationElement;
import org.sigmah.server.domain.element.CoreVersionElement;
import org.sigmah.server.domain.element.DefaultFlexibleElement;
import org.sigmah.server.domain.element.FilesListElement;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.element.IndicatorsListElement;
import org.sigmah.server.domain.element.MessageElement;
import org.sigmah.server.domain.element.QuestionElement;
import org.sigmah.server.domain.element.ReportElement;
import org.sigmah.server.domain.element.ReportListElement;
import org.sigmah.server.domain.element.TextAreaElement;
import org.sigmah.server.domain.element.TripletsListElement;
import org.sigmah.shared.dto.referential.ElementTypeEnum;
import org.sigmah.shared.dto.referential.LogicalElementType;
import org.sigmah.shared.dto.referential.NoElementType;
import org.sigmah.shared.dto.referential.TextAreaType;

/**
 * Utility class to retrieve instances of <code>LogicalElementType</code> from
 * entity objects.
 * 
 * @see org.sigmah.shared.dto.referential.LogicalElementTypes
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.2
 */
public final class EntityLogicalElementTypes {
	
	/**
     * Private constructor.
     */
    private EntityLogicalElementTypes() {
        // No initialization.
    }
	
	public static LogicalElementType of(final FlexibleElement element) {
		
		final LogicalElementType type;
		
		if (element == null) {
			type = NoElementType.INSTANCE;
		} else if (element instanceof TextAreaElement) {
			type = TextAreaType.fromCode(((TextAreaElement) element).getType());
		} else if (element instanceof CheckboxElement) {
			type = ElementTypeEnum.CHECKBOX;
		} else if (element instanceof DefaultFlexibleElement) {
			type = ((DefaultFlexibleElement) element).getType();
		} else if (element instanceof FilesListElement) {
			type = ElementTypeEnum.FILES_LIST;
		} else if (element instanceof IndicatorsListElement) {
			type = ElementTypeEnum.INDICATORS;
		} else if (element instanceof MessageElement) {
			type = ElementTypeEnum.MESSAGE;
		} else if (element instanceof QuestionElement) {
			type = ElementTypeEnum.QUESTION;
		} else if (element instanceof ReportElement) {
			type = ElementTypeEnum.REPORT;
		} else if (element instanceof ReportListElement) {
			type = ElementTypeEnum.REPORT_LIST;
		} else if (element instanceof TripletsListElement) {
			type = ElementTypeEnum.TRIPLETS;
		} else if (element instanceof CoreVersionElement) {
			type = ElementTypeEnum.CORE_VERSION;
		} else if (element instanceof ComputationElement) {
			type = ElementTypeEnum.COMPUTATION;
		} else {
			throw new UnsupportedOperationException("Type '" + element.getClass() + "' is not supported.");
		}
		
		return type;
	}
	
}
