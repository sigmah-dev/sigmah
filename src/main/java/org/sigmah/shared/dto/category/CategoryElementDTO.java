package org.sigmah.shared.dto.category;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

/**
 * CategoryElementDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class CategoryElementDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 7879245182808843730L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return "category.CategoryElement";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("label", getLabel());
		builder.append("color", getColor());
		builder.append("iconHtml", getIconHtml());
	}

	// Element label
	public String getLabel() {
		return get("label");
	}

	public void setLabel(String label) {
		set("label", label);
	}

	// Reference to the parent category type
	public CategoryTypeDTO getParentCategoryDTO() {
		return get("parentCategoryDTO");
	}

	public void setParentCategoryDTO(CategoryTypeDTO parentCategoryDTO) {
		set("parentCategoryDTO", parentCategoryDTO);
	}

	// Color
	public String getColor() {
		return get("color");
	}

	public void setColor(String color) {
		set("color", color);
	}

	public void setIconHtml(String iconHtml) {
		set("iconHtml", iconHtml);
	}

	public String getIconHtml() {
		return get("iconHtml");
	}

}
