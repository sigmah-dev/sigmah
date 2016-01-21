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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.category.CategoryType;
import org.sigmah.server.domain.quality.QualityCriterion;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Question element domain entity.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.QUESTION_ELEMENT_TABLE)
public class QuestionElement extends FlexibleElement {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 2228874550756274138L;

	@Column(name = EntityConstants.QUESTION_ELEMENT_COLUMN_IS_MULTIPLE, nullable = true)
	private Boolean multiple = Boolean.FALSE;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(optional = true)
	@JoinColumn(name = EntityConstants.QUESTION_ELEMENT_COLUMN_ID_QUALITY_CRITERION, nullable = true)
	private QualityCriterion qualityCriterion;

	@OneToMany(mappedBy = "parentQuestion", cascade = CascadeType.ALL)
	@OrderBy("sortOrder ASC")
	private List<QuestionChoiceElement> choices = new ArrayList<QuestionChoiceElement>();

	@ManyToOne(optional = true)
	@JoinColumn(name = EntityConstants.QUESTION_ELEMENT_COLUMN_ID_CATEGORY_TYPE, nullable = true)
	private CategoryType categoryType;

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
		builder.append("multiple", multiple);
	}

	/**
	 * Adds a choice to the current question.
	 * 
	 * @param label
	 *          The choice's label.
	 */
	public void addChoice(String label) {

		// Creates the choice and puts it at the end of the choices ordered
		// list.
		final QuestionChoiceElement choice = new QuestionChoiceElement();
		choice.setLabel(label);
		choice.setSortOrder(choices.size());

		choice.setParentQuestion(this);
		choices.add(choice);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transient
	public boolean isHistorable() {
		return true;
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	public void setQualityCriterion(QualityCriterion qualityCriterion) {
		this.qualityCriterion = qualityCriterion;
	}

	public QualityCriterion getQualityCriterion() {
		return qualityCriterion;
	}

	public List<QuestionChoiceElement> getChoices() {
		return choices;
	}

	public void setChoices(List<QuestionChoiceElement> choices) {
		this.choices = choices;
	}

	public Boolean getMultiple() {
		return multiple;
	}

	public void setMultiple(Boolean multiple) {
		this.multiple = multiple;
	}

	public CategoryType getCategoryType() {
		return categoryType;
	}

	public void setCategoryType(CategoryType categoryType) {
		this.categoryType = categoryType;
	}
}
