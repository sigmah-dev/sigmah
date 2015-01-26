package org.sigmah.shared.dto;

import java.util.List;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.layout.LayoutDTO;

import com.google.gwt.user.client.ui.Widget;

/**
 * PhaseModelDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class PhaseModelDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8520711106031085130L;

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "PhaseModel";

	// DTO attributes keys.
	public static final String NAME = "name";
	public static final String DISPLAY_ORDER = "displayOrder";
	public static final String ROOT = "root";
	public static final String GUIDE = "guide";
	public static final String PARENT_PROJECT_MODEL = "parentProjectModel";
	public static final String LAYOUT = "layout";
	public static final String SUCCESSORS = "successors";
	public static final String DEFINITION = "definition";

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
		builder.append(NAME, getName());
		builder.append(DISPLAY_ORDER, getDisplayOrder());
		builder.append(ROOT, getRoot());
		builder.append(GUIDE, getGuide());
	}

	/**
	 * Returns if a guide is available for this phase model.
	 * 
	 * @return If a guide is available for this phase model.
	 */
	public boolean isGuideAvailable() {
		final String guide = get(GUIDE);
		return guide != null && !"".equals(guide.trim());
	}

	public Widget getWidget() {
		return getLayout().getWidget();
	}

	// Phase model name
	public String getName() {
		return get(NAME);
	}

	public void setName(String name) {
		set(NAME, name);
	}

	// Reference to parent project model DTO
	public ProjectModelDTO getParentProjectModel() {
		return get(PARENT_PROJECT_MODEL);
	}

	public void setParentProjectModel(ProjectModelDTO parentProjectModel) {
		set(PARENT_PROJECT_MODEL, parentProjectModel);
	}

	// Reference to layout
	public LayoutDTO getLayout() {
		return get(LAYOUT);
	}

	public void setLayout(LayoutDTO layout) {
		set(LAYOUT, layout);
	}

	// Reference to the phases successors
	public List<PhaseModelDTO> getSuccessors() {
		return get(SUCCESSORS);
	}

	public void setSuccessors(List<PhaseModelDTO> successors) {
		set(SUCCESSORS, successors);
	}

	// Display order
	public Integer getDisplayOrder() {
		return (Integer) get(DISPLAY_ORDER);
	}

	public void setDisplayOrder(Integer displayOrder) {
		set(DISPLAY_ORDER, displayOrder);
	}

	// Definition
	public PhaseModelDefinitionDTO getDefinition() {
		return get(DEFINITION);
	}

	public void setDefinition(PhaseModelDefinitionDTO definition) {
		set(DEFINITION, definition);
	}

	// Guide
	public String getGuide() {
		return get(GUIDE);
	}

	public void setGuide(String guide) {
		set(GUIDE, guide);
	}

	// Root.
	public Boolean getRoot() {
		return get(ROOT);
	}

	public void setRoot(Boolean root) {
		set(ROOT, root);
	}

}
