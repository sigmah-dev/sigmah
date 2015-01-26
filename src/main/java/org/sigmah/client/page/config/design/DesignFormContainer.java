package org.sigmah.client.page.config.design;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.common.dialog.FormDialogCallback;
import org.sigmah.client.page.common.dialog.FormDialogImpl;
import org.sigmah.client.page.common.dialog.FormDialogTether;
import org.sigmah.shared.dto.ActivityDTO;
import org.sigmah.shared.dto.AttributeDTO;
import org.sigmah.shared.dto.AttributeGroupDTO;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.IndicatorGroup;
import org.sigmah.shared.dto.UserDatabaseDTO;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.treegrid.EditorTreeGrid;

public class DesignFormContainer extends ContentPanel {

	private final Dispatcher service;
	private final UserDatabaseDTO db;
	private final EditorTreeGrid<ModelData> tree;

	public DesignFormContainer(Dispatcher service, UserDatabaseDTO db, EditorTreeGrid<ModelData> tree) {
		this.service = service;
		this.db = db;
		this.tree = tree;
		setHeaderVisible(false);
		setBorders(false);
		setFrame(false);
	}

	public FormDialogTether showNewForm(ModelData entity, FormDialogCallback callback) {
		AbstractDesignForm form = createForm(entity);
		form.getBinding().bind(entity);

		for (FieldBinding field : form.getBinding().getBindings()) {
			field.getField().clearInvalid();
		}

		FormDialogImpl dlg = new FormDialogImpl(form);
		dlg.setWidth(form.getPreferredDialogWidth());
		dlg.setHeight(form.getPreferredDialogHeight());
		dlg.setScrollMode(Style.Scroll.AUTOY);

		if (entity instanceof ActivityDTO) {
			dlg.setHeading(I18N.CONSTANTS.newActivity());
		} else if (entity instanceof AttributeGroupDTO) {
			dlg.setHeading(I18N.CONSTANTS.newAttributeGroup());
		} else if (entity instanceof AttributeDTO) {
			dlg.setHeading(I18N.CONSTANTS.newAttribute());
		} else if (entity instanceof IndicatorDTO) {
			dlg.setHeading(I18N.CONSTANTS.newIndicator());
		}

		dlg.show(callback);
		return dlg;
	}

	public AbstractDesignForm createForm(ModelData sel) {
		if (sel instanceof ActivityDTO) {
			return new ActivityForm(service, db);
		} else if (sel instanceof AttributeGroupDTO) {
			return new AttributeGroupForm();
		} else if (sel instanceof AttributeDTO) {
			return new AttributeForm();
		} else if (sel instanceof IndicatorDTO) {
			return new IndicatorForm(service);
		} else if (sel instanceof IndicatorGroup) {
			return new IndicatorGroupForm();
		}
		return null;
	}

	public void showForm(ModelData model) {
		// do we have the right form?
		Class formClass = formClassForSelection(model);
		AbstractDesignForm currentForm = null;
		if (getItemCount() != 0) {
			currentForm = (AbstractDesignForm) getItem(0);
		}

		if (formClass == null) {
			if (currentForm != null) {
				currentForm.getBinding().unbind();
				removeAll();
			}
			return;
		} else {
			if (currentForm == null || (currentForm != null && !formClass.equals(currentForm.getClass()))) {

				if (currentForm != null) {
					removeAll();
					currentForm.getBinding().unbind();
				}
				currentForm = createForm(model);
				// currentForm.setReadOnly(!db.isDesignAllowed());
				currentForm.setHeaderVisible(false);
				currentForm.setBorders(false);
				currentForm.setFrame(false);
				currentForm.getBinding().setStore(tree.getStore());
				add(currentForm);
				layout();
			}
		}
		currentForm.getBinding().bind(model);
	}

	private Class formClassForSelection(ModelData sel) {
		if (sel instanceof ActivityDTO) {
			return ActivityForm.class;
		} else if (sel instanceof AttributeGroupDTO) {
			return AttributeGroupForm.class;
		} else if (sel instanceof IndicatorDTO) {
			return IndicatorForm.class;
		} else if (sel instanceof AttributeDTO) {
			return AttributeForm.class;
		}
		return null;
	}
}
