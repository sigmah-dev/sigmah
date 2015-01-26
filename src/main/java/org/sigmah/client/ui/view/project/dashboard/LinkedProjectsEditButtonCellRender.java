package org.sigmah.client.ui.view.project.dashboard;

import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.shared.dto.ProjectFundingDTO;
import org.sigmah.shared.dto.ProjectFundingDTO.LinkedProjectType;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.PushButton;

/**
 * The render for the edit icon button in linked projects grid. The button has a click handler to response the On-Click
 * event.
 * 
 * @author HUZHE(zhe.hu32@gmail.com)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
final class LinkedProjectsEditButtonCellRender extends LinkedProjectsAbstractProvider implements GridCellRenderer<ProjectFundingDTO> {

	// CSS style names.
	private static final String STYLE_EDIT_BUTTON = "project-linkedProject-editButton";

	public LinkedProjectsEditButtonCellRender(final ProjectDashboardView view, final LinkedProjectType projectType) {
		super(view, projectType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object render(final ProjectFundingDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
			final ListStore<ProjectFundingDTO> store, final Grid<ProjectFundingDTO> grid) {

		// Creates the button with its icon.
		final PushButton editButton = new PushButton(IconImageBundle.ICONS.editLinkedProject().createImage());
		editButton.setStylePrimaryName(STYLE_EDIT_BUTTON);

		// Sets the button click handler.
		editButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				view.getPresenterHandler().onLinkedProjectEditClickEvent(model, projectType);
			}
		});

		// Returns this button.
		return editButton;
	}

}
