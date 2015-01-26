package org.sigmah.shared.dto;

import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.layout.LayoutDTO;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

/**
 * ProjectDetailsDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ProjectDetailsDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 3304868140991425311L;

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "ProjectDetails";

	// DTO attributes keys.
	public static final String NAME = "name";
	public static final String LAYOUT = "layout";
	public static final String PROJECT_MODEL = "projectModel";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	// Name (only client-side).
	public void setName() {
		if (GWT.isClient()) {
			set(NAME, I18N.CONSTANTS.Admin_PROJECT_DETAILS());
		}
	}

	public String getName() {
		if (GWT.isClient()) {
			return I18N.CONSTANTS.Admin_PROJECT_DETAILS();
		} else {
			return get(NAME);
		}
	}

	// Reference to the Layout
	public LayoutDTO getLayout() {
		return get(LAYOUT);
	}

	public void setLayout(LayoutDTO layout) {
		set(LAYOUT, layout);
	}

	// Reference to the Project Model
	public ProjectModelDTO getProjectModel() {
		return get(PROJECT_MODEL);
	}

	public void setProjectModel(ProjectModelDTO projectModel) {
		set(PROJECT_MODEL, projectModel);
	}

	public Widget getWidget() {
		return getLayout().getWidget();
	}
}
