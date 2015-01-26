package org.sigmah.client.ui.view.admin.orgunits;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.admin.orgunits.MoveOrgUnitAdminPresenter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.google.inject.Singleton;

/**
 * {@link MoveOrgUnitAdminPresenter}'s view implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class MoveOrgUnitAdminView extends AbstractPopupView<PopupWidget> implements MoveOrgUnitAdminPresenter.View {

	private FormPanel form;
	private ComboBox<OrgUnitDTO> parentField;
	private Button moveButton;

	/**
	 * Builds the view.
	 */
	public MoveOrgUnitAdminView() {
		super(new PopupWidget(true), 500);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		// --
		// Form.
		// --

		form = Forms.panel(90);

		// --
		// Parent field.
		// --

		parentField = Forms.combobox(I18N.CONSTANTS.adminOrgUnitMoveNewParent(), true, OrgUnitDTO.ID, OrgUnitDTO.COMPLETE_NAME);
		parentField.setEmptyText(I18N.CONSTANTS.orgunitEmptyChoice());
		parentField.setTriggerAction(TriggerAction.ALL);

		parentField.getStore().addStoreListener(new StoreListener<OrgUnitDTO>() {

			@Override
			public void storeAdd(final StoreEvent<OrgUnitDTO> se) {
				parentField.setEnabled(true);
			}

			@Override
			public void storeClear(final StoreEvent<OrgUnitDTO> se) {
				parentField.setEnabled(false);
			}

		});

		// --
		// Move button.
		// --

		moveButton = Forms.button(I18N.CONSTANTS.adminOrgUnitMove(), IconImageBundle.ICONS.up());

		// --
		// View initialization.
		// --

		form.add(parentField);
		form.addButton(moveButton);

		initPopup(form);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FormPanel getForm() {
		return form;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComboBox<OrgUnitDTO> getParentField() {
		return parentField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getMoveButton() {
		return moveButton;
	}

}
