package org.sigmah.server.domain.element;

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


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.category.CategoryElement;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Question choice element domain entity.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.QUESTION_CHOICE_ELEMENT_TABLE)
public class QuestionChoiceElement extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8162125961144891315L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.QUESTION_CHOICE_ELEMENT_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.QUESTION_CHOICE_ELEMENT_COLUMN_LABEL, nullable = false, length = EntityConstants.QUESTION_CHOICE_ELEMENT_LABEL_MAX_LENGTH)
	@Size(max = EntityConstants.QUESTION_CHOICE_ELEMENT_LABEL_MAX_LENGTH)
	@NotNull
	private String label;

	@Column(name = EntityConstants.QUESTION_CHOICE_ELEMENT_COLUMN_SORT_ORDER, nullable = true)
	private Integer sortOrder;
	
	@Column(name = EntityConstants.QUESTION_CHOICE_ELEMENT_COLUMN_DISABLED, nullable = true)
	private Boolean disabled;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(optional = false)
	@JoinColumn(name = EntityConstants.QUESTION_CHOICE_ELEMENT_COLUMN_ID_QUESTION, nullable = false)
	@NotNull
	private QuestionElement parentQuestion;

	@ManyToOne(optional = true)
	@JoinColumn(name = EntityConstants.QUESTION_CHOICE_ELEMENT_COLUMN_ID_CATEGORY_ELEMENT, nullable = true)
	private CategoryElement categoryElement;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendToString(final ToStringBuilder builder) {
		builder.append("id", id);
		builder.append("label", label);
		builder.append("sortOrder", sortOrder);
	}

	/**
	 * Reset the identifiers of the object.
	 */
	public void resetImport(QuestionElement parentQuestion) {
		this.id = null;
		this.parentQuestion = parentQuestion;
		if (categoryElement != null) {
			categoryElement.resetImport();
		}
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public QuestionElement getParentQuestion() {
		return parentQuestion;
	}

	public void setParentQuestion(QuestionElement parentQuestion) {
		this.parentQuestion = parentQuestion;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public CategoryElement getCategoryElement() {
		return categoryElement;
	}

	public void setCategoryElement(CategoryElement categoryElement) {
		this.categoryElement = categoryElement;
	}
	
    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }
}
