package org.sigmah.server.domain.element;

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
}
