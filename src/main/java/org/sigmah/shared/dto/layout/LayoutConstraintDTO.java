package org.sigmah.shared.dto.layout;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;

/**
 * LayoutConstraintDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class LayoutConstraintDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8520711106031085130L;

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "layout.LayoutConstraint";

	// DTO attributes keys.
	public static final String SORT_ORDER = "sortOrder";
	public static final String PARENT_LAYOUT_GROUP = "parentLayoutGroup";
	public static final String FLEXIBLE_ELEMENT_DTO = "flexibleElementDTO";

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
		builder.append(SORT_ORDER, getSortOrder());
	}

	// Sort order
	public int getSortOrder() {
		return (Integer) get(SORT_ORDER);
	}

	public void setSortOrder(int sortOrder) {
		set(SORT_ORDER, sortOrder);
	}

	// Reference to the layout group parent
	public LayoutGroupDTO getParentLayoutGroup() {
		return get(PARENT_LAYOUT_GROUP);
	}

	public void setParentLayoutGroup(LayoutGroupDTO parentLayoutGroup) {
		set(PARENT_LAYOUT_GROUP, parentLayoutGroup);
	}

	// Reference to the flexible element
	public FlexibleElementDTO getFlexibleElementDTO() {
		return get(FLEXIBLE_ELEMENT_DTO);
	}

	public void setFlexibleElementDTO(FlexibleElementDTO flexibleElementDTO) {
		set(FLEXIBLE_ELEMENT_DTO, flexibleElementDTO);
	}

}
