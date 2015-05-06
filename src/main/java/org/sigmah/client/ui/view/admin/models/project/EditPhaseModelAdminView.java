package org.sigmah.client.ui.view.admin.models.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.admin.models.project.EditPhaseModelAdminPresenter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.dto.PhaseModelDTO;

import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.inject.Singleton;

/**
 * {@link EditPhaseModelAdminPresenter}'s view implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class EditPhaseModelAdminView extends AbstractPopupView<PopupWidget> implements EditPhaseModelAdminPresenter.View {

	private Map<CheckBox, PhaseModelDTO> successors;
	private FormPanel form;
	private TextField<String> nameField;
	private FlowPanel successorsPanel;
	private CheckBox rootField;
	private NumberField displayOrderField;
	private TextField<String> guideField;
	private Button saveButton;

	/**
	 * Builds the view.
	 */
	public EditPhaseModelAdminView() {
		super(new PopupWidget(true), 500);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		successors = new HashMap<CheckBox, PhaseModelDTO>();

		// --
		// Form.
		// --

		form = Forms.panel(130);

		// --
		// Name field.
		// --

		nameField = Forms.text(I18N.CONSTANTS.adminPhaseName(), true);

		// --
		// Successors field.
		// Cannot use a CheckBoxGroup for this field due to 'post-render' checkBox(es) insertion.
		// --

		successorsPanel = new FlowPanel();
		final Field<Object> successorsField = Forms.adapter(I18N.CONSTANTS.adminPhaseSuccessors(), successorsPanel);

		// --
		// Root field.
		// --

		rootField = Forms.checkbox(I18N.CONSTANTS.adminPhaseModelRoot(), null, I18N.CONSTANTS.adminPhaseModelRoot(), false);

		// --
		// Display order field.
		// --

		displayOrderField = Forms.number(I18N.CONSTANTS.adminPhaseOrder(), true, false);

		// --
		// Guide field.
		// --

		guideField = Forms.text(I18N.CONSTANTS.projectPhaseGuideHeader(), false);

		// --
		// Save button.
		// --

		saveButton = Forms.button(I18N.CONSTANTS.adminOrgUnitCreateButton(), IconImageBundle.ICONS.save());

		// --
		// View initialization.
		// --

		form.add(nameField);
		form.add(successorsField);
		form.add(rootField);
		form.add(displayOrderField);
		form.add(guideField);
		form.addButton(saveButton);

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
	public Field<String> getNameField() {
		return nameField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> getRootField() {
		return rootField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Number> getDisplayOrderField() {
		return displayOrderField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> getGuideField() {
		return guideField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getSaveButton() {
		return saveButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearSuccessors() {
		successorsPanel.clear();
		successors.clear();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addSuccessor(final PhaseModelDTO successor, boolean selected) {
		if (successor == null) {
			return;
		}

		final CheckBox checkBox = Forms.checkbox(successor.getName(), String.valueOf(successor.getId()), selected);
		successorsPanel.add(checkBox);
		successors.put(checkBox, successor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PhaseModelDTO> getSelectedSuccessors() {

		final List<PhaseModelDTO> selectedSuccessors = new ArrayList<PhaseModelDTO>();

		for (final Entry<CheckBox, PhaseModelDTO> successor : successors.entrySet()) {
			if (ClientUtils.isTrue(successor.getKey().getValue())) {
				selectedSuccessors.add(successor.getValue());
			}
		}

		return selectedSuccessors;
	}

}
