package org.sigmah.shared.dto.element;

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
		setLabel(categoryElement.getLabel());
	}

}
