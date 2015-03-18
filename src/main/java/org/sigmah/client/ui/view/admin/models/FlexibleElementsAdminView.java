package org.sigmah.client.ui.view.admin.models;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.admin.models.FlexibleElementsAdminPresenter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.shared.dto.element.FlexibleElementDTO;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

/**
 * {@link FlexibleElementsAdminPresenter}'s view implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class FlexibleElementsAdminView extends AbstractView implements FlexibleElementsAdminPresenter.View {

	private Grid<FlexibleElementDTO> grid;
	private ToolBar toolbar;
	private Button addButton;
	private Button addGroupButton;
	private Button deleteButton;
	private Button enableButton;
	private Button disableButton;

	private boolean editable;
	private GridEventHandler<FlexibleElementDTO> gridEventHandler;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		final ContentPanel mainPanel = Panels.content(null);

		mainPanel.add(createGrid());
		mainPanel.setTopComponent(createToolBar());

		add(mainPanel);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Grid<FlexibleElementDTO> getGrid() {
		return grid;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListStore<FlexibleElementDTO> getStore() {
		return grid.getStore();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setGridEventHandler(final GridEventHandler<FlexibleElementDTO> handler) {
		this.gridEventHandler = handler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setModelEditable(final boolean editable) {
		this.editable = editable;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getAddButton() {
		return addButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getAddGroupButton() {
		return addGroupButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getDeleteButton() {
		return deleteButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getEnableButton() {
		return enableButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getDisableButton() {
		return disableButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setToolbarEnabled(final boolean enabled) {
		if (enabled) {
			toolbar.show();
		} else {
			toolbar.hide();
		}
		toolbar.setEnabled(enabled);
		addButton.setEnabled(enabled);
		addGroupButton.setEnabled(enabled);
		// Only with selection.
		deleteButton.setEnabled(false); 
		enableButton.setEnabled(false);
		disableButton.setEnabled(false);
	}

	// ---------------------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------------------------------

	/**
	 * Creates the grid component.
	 * 
	 * @return The grid component.
	 */
	private Component createGrid() {

		grid = new Grid<FlexibleElementDTO>(new ListStore<FlexibleElementDTO>(), new FlexibleElementsColumnsProvider() {

			@Override
			protected boolean isEditable() {
				return editable;
			}

			@Override
			protected GridEventHandler<FlexibleElementDTO> getGridEventHandler() {
				return gridEventHandler;
			}

		}.getColumnModel());

		grid.setAutoHeight(true);
		grid.getView().setForceFit(true);
		grid.getStore().setSortField(FlexibleElementDTO.CONTAINER);

		final GridSelectionModel<FlexibleElementDTO> selectionModel = new GridSelectionModel<FlexibleElementDTO>();
		selectionModel.setSelectionMode(SelectionMode.MULTI);
		grid.setSelectionModel(selectionModel);
		
		return grid;
	}

	/**
	 * Creates the toolbar component and its buttons.
	 * 
	 * @return The toolbar component.
	 */
	private Component createToolBar() {

		toolbar = new ToolBar();

		addButton = Forms.button(I18N.CONSTANTS.addItem(), IconImageBundle.ICONS.add());
		toolbar.add(addButton);

		addGroupButton = Forms.button(I18N.CONSTANTS.adminFlexibleAddGroup(), IconImageBundle.ICONS.add());
		toolbar.add(addGroupButton);

		deleteButton = Forms.button(I18N.CONSTANTS.adminFlexibleDeleteFlexibleElements(), IconImageBundle.ICONS.delete());
		deleteButton.disable();
		toolbar.add(deleteButton);
		
		enableButton = Forms.button(I18N.CONSTANTS.adminFlexibleEnableFlexibleElements(), IconImageBundle.ICONS.checked());
		enableButton.disable();
		toolbar.add(enableButton);
		
		disableButton = Forms.button(I18N.CONSTANTS.adminFlexibleDisableFlexibleElements(), IconImageBundle.ICONS.disable());
		disableButton.disable();
		toolbar.add(disableButton);

		return toolbar;
	}

}
