package org.sigmah.client.ui.view.project.indicator;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import java.util.ArrayList;
import java.util.List;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.project.indicator.EditSitePresenter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.CoordinateField;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.map.Pin;
import org.sigmah.client.ui.widget.map.WorldMap;
import org.sigmah.client.ui.widget.map.WorldMapFactory;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.shared.dto.AdminEntityDTO;
import org.sigmah.shared.dto.AdminLevelDTO;
import org.sigmah.shared.dto.BoundingBoxDTO;
import org.sigmah.shared.dto.SiteDTO;
import org.sigmah.shared.dto.country.CountryDTO;

/**
 * Site edition popup.
 * 
 * @author sherzod (v1.3)
 */
public class EditSiteView extends AbstractPopupView<PopupWidget> implements EditSitePresenter.View {
	
	private FormPanel form;
	
	private FieldSet locationFieldSet;
	private ContentPanel mapPanel;
	private Field<String> locationNameField;
	private Field<String> locationAxeField;
	private WorldMap worldMap;
	private Pin pin;
	private Field<Double> latitudeField;
	private Field<Double> longitudeField;
	private ComboBox<AdminEntityDTO> topLevelComboBox;
	private List<ComboBox<AdminEntityDTO>> adminLevelBoxes;
	
	private Button saveButton;
	private Button cancelButton;
	
	public EditSiteView() {
		super(new PopupWidget(true), 800);
	}
	
	@Override
	public void initialize() {
		form = Forms.panel(130);
		
		// Location name field.
		locationNameField = Forms.text(I18N.CONSTANTS.location(), true);
		locationNameField.setName(SiteDTO.LOCATION_NAME);
		
		// Location axe field.
        locationAxeField = Forms.text(I18N.CONSTANTS.axeFieldLabel(), false);
		locationAxeField.setName(SiteDTO.LOCATION_AXE);
		
		// Admin levels.
		adminLevelBoxes = new ArrayList<ComboBox<AdminEntityDTO>>();
		
		// Map.
		mapPanel = Panels.content(null, new FitLayout());
		latitudeField = new CoordinateField(CoordinateField.Axis.LATITUDE);
		longitudeField = new CoordinateField(CoordinateField.Axis.LONGITUDE);
		
		worldMap = WorldMapFactory.createInstance();
		worldMap.setSize("100%", "300px");
		
		pin = new Pin(true);
		worldMap.addPin(pin);
		
		final ToolBar coordinateBar = new ToolBar();
		coordinateBar.add(new LabelToolItem(I18N.CONSTANTS.lat()));
		coordinateBar.add(latitudeField);
		coordinateBar.add(new LabelToolItem(I18N.CONSTANTS.lng()));
		coordinateBar.add(longitudeField);
		
		mapPanel.add(worldMap.asWidget());
		mapPanel.setBottomComponent(coordinateBar);
		
		// Save button.
		saveButton = Forms.button(I18N.CONSTANTS.save(), IconImageBundle.ICONS.save());
		
		// Cancel button.
		cancelButton = Forms.button(I18N.CONSTANTS.cancel(), IconImageBundle.ICONS.cancel());
		
		// Building form
		form.add(locationNameField, Forms.data());
		form.add(locationAxeField, Forms.data());
		
		final Grid mainContainer = new Grid(2, 2);
		mainContainer.setWidth("100%");
		mainContainer.getElement().getStyle().setTableLayout(Style.TableLayout.FIXED);
		mainContainer.getColumnFormatter().getElement(0).getStyle().setProperty("width", "50%");
		mainContainer.getColumnFormatter().getElement(1).getStyle().setProperty("width", "50%");
		mainContainer.getRowFormatter().setVerticalAlign(1, HasVerticalAlignment.ALIGN_TOP);

		mainContainer.setWidget(0, 0, Forms.label(I18N.CONSTANTS.locationTitle()));
		mainContainer.setWidget(0, 1, Forms.label(I18N.CONSTANTS.geoPosition()));
		mainContainer.setWidget(1, 0, form);
		mainContainer.setWidget(1, 1, mapPanel);
		getPopup().addButton(cancelButton);
		getPopup().addButton(saveButton);
		
		initPopup(mainContainer);
	}

	@Override
	public void setCountry(CountryDTO country, Listener<FieldEvent> selectionListener) {
		topLevelComboBox = null;
		
		for(final ComboBox<?> box : adminLevelBoxes) {
			box.removeAllListeners();
			form.remove(box);
		}
		adminLevelBoxes.clear();
		
		if(country != null) {
			ComboBox<AdminEntityDTO> parentComboBox = null;
			
			int row = 2;
			
			for(final AdminLevelDTO level : country.getAdminLevels()) {
				final ComboBox<AdminEntityDTO> comboBox = Forms.combobox(level.getName(), false, AdminEntityDTO.ID, AdminEntityDTO.NAME);
				comboBox.setName(AdminEntityDTO.getPropertyName(level.getId()));
				comboBox.setEnabled(false);
				comboBox.setTriggerAction(ComboBox.TriggerAction.ALL);
				comboBox.setData("level", level);
				
				if(parentComboBox != null) {
					comboBox.setData("parent", parentComboBox);
					parentComboBox.setData("child", comboBox);
				} else {
					topLevelComboBox = comboBox;
				}
				
				comboBox.addListener(Events.Select, selectionListener);
				comboBox.addListener(Events.KeyUp, selectionListener);
				
				parentComboBox = comboBox;
				
				adminLevelBoxes.add(comboBox);
				form.insert(comboBox, row++, Forms.data());
			}
			form.layout();
		}
	}
	
	@Override
	public FormPanel getForm() {
		return form;
	}

	@Override
	public ComboBox<AdminEntityDTO> getTopLevelComboBox() {
		return topLevelComboBox;
	}

	@Override
	public FieldSet getLocationFieldSet() {
		return locationFieldSet;
	}

	@Override
	public Field<Double> getLatitudeField() {
		return latitudeField;
	}

	@Override
	public Field<Double> getLongitudeField() {
		return longitudeField;
	}

	@Override
	public Pin getPin() {
		return pin;
	}

	@Override
	public Button getSaveButton() {
		return saveButton;
	}

	@Override
	public Button getCancelButton() {
		return cancelButton;
	}

	@Override
	public BoundingBoxDTO getMapBounds() {
		return worldMap.getBounds();
	}

	@Override
	public void setMapBounds(final BoundingBoxDTO bounds) {
		worldMap.setBounds(bounds);
	}

	@Override
	public void panTo(double latitude, double longitude) {
		worldMap.panTo(latitude, longitude);
	}

	@Override
	public void setPinPosition(double latitude, double longitude) {
		pin.setPosition(latitude, longitude);
	}
	
}
