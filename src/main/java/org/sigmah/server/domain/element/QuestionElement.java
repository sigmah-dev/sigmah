package org.sigmah.server.domain.element;

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
