package org.sigmah.shared.dto.category;

import java.util.List;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.referential.CategoryIcon;

/**
 * CategoryTypeDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class CategoryTypeDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 4190439829705158136L;

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "category.CategoryType";

	// DTO attributes keys.
	public static final String LABEL = "label";
	public static final String ICON = "icon";
	public static final String CATEGORY_ELEMENTS = "categoryElementsDTO";

	public CategoryTypeDTO() {
		// Serialization.
	}

	/**
	 * Initializes a new {@code CategoryTypeDTO} with the given {@code label}.
	 * 
	 * @param label
	 *          The category type label.
	 */
	public CategoryTypeDTO(final String label) {
		setLabel(label);
	}

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
		builder.append(ICON, getIcon());
	}

	// Type label
	public String getLabel() {
		return get(LABEL);
	}

	public void setLabel(String label) {
		set(LABEL, label);
	}

	// Icon name
	public CategoryIcon getIcon() {
		return get(ICON);
	}

	public void setIcon(CategoryIcon icon) {
		set(ICON, icon);
	}

	// Category elements list
	public List<CategoryElementDTO> getCategoryElementsDTO() {
		return get(CATEGORY_ELEMENTS);
	}

	public void setCategoryElementsDTO(List<CategoryElementDTO> categoryElementsDTO) {
		set(CATEGORY_ELEMENTS, categoryElementsDTO);
	}

}
