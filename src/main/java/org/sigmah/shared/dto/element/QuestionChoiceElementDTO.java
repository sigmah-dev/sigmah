package org.sigmah.shared.dto.element;

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


import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.category.CategoryElementDTO;

/**
 * QuestionChoiceElementDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class QuestionChoiceElementDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8520711106031085130L;

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "element.QuestionChoiceElement";

	// DTO attributes keys.
	public static final String LABEL = "label";
	public static final String SORT_ORDER = "sortOrder";
	public static final String PARENT_QUESTION = "parentQuestion";
	public static final String CATEGORY_ELEMENT = "categoryElement";
	public static final String DISABLED = "disabled";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append(LABEL, getLabel());
		builder.append(SORT_ORDER, getSortOrder());
	}

	// Question choice label
	public String getLabel() {
		return get(LABEL);
	}

	public void setLabel(String label) {
		if (getCategoryElement() == null) {
			set(LABEL, label);
		}
	}
	
	// Question choice disabled status
    public boolean isDisabled() {
		final Boolean disabled = get(DISABLED);
        return disabled != null && disabled;
    }

    public void setDisabled(boolean disabled) {
        set(DISABLED, disabled);
    }

	// Question choice sort order
	public Integer getSortOrder() {
		return (Integer) get(SORT_ORDER);
	}

	public void setSortOrder(Integer sortOrder) {
		set(SORT_ORDER, sortOrder);
	}

	// Reference to the parent question element
	public QuestionElementDTO getParentQuestion() {
		return get(PARENT_QUESTION);
	}

	public void setParentQuestion(QuestionElementDTO parentQuestion) {
		set(PARENT_QUESTION, parentQuestion);
	}

	// Question category type
	public CategoryElementDTO getCategoryElement() {
		return get(CATEGORY_ELEMENT);
	}

	public void setCategoryElement(CategoryElementDTO categoryElement) {
		set(CATEGORY_ELEMENT, categoryElement);
		// BUGFIX #704
		set(LABEL, categoryElement.getLabel());
	}

}
