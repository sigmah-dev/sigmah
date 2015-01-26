package org.sigmah.shared.dto;

import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.layout.LayoutDTO;

import com.google.gwt.user.client.ui.Widget;

/**
 * ProjectBannerDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ProjectBannerDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 3304868140991425311L;

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "ProjectBanner";

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

	public String getName() {
		return I18N.CONSTANTS.Admin_BANNER();
	}

	// Reference to the Layout
	public LayoutDTO getLayout() {
		return get(LAYOUT);
	}

	public void setLayout(LayoutDTO layout) {
		set(LAYOUT, layout);
	}

	// Reference to the Project Model
	public ProjectModelDTO getProjectModelDTO() {
		return get(PROJECT_MODEL);
	}

	public void setProjectModelDTO(ProjectModelDTO projectModel) {
		set(PROJECT_MODEL, projectModel);
	}

	public Widget getWidget() {
		return getLayout().getWidget();
	}

}
