package org.sigmah.client.ui.view.project.projectcore;

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



import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.project.projectcore.ProjectCoreDiffPresenter;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.layout.Layouts.Margin;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.shared.dto.AmendmentDTO;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.google.inject.Singleton;
import java.util.Arrays;

/**
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 */
public class ProjectCoreDiffView extends AbstractPopupView<PopupWidget> implements ProjectCoreDiffPresenter.View {

	private ContentPanel mainPanel;

	private Grid<DiffEntry> projectFields;
	private ComboBox<AmendmentDTO> amendmentsComboBox1;
	private ComboBox<AmendmentDTO> amendmentsComboBox2;

	public ProjectCoreDiffView() {
		super(new PopupWidget(true), 550);
	}

	@Override
	public void initialize() {

		mainPanel = Panels.content("");
		mainPanel.setHeaderVisible(false);

		Label label = new Label(I18N.CONSTANTS.projectCoreSelectVersion());

		mainPanel.add(label);

		amendmentsComboBox1 = new ComboBox<AmendmentDTO>();
		amendmentsComboBox1.setStore(new ListStore<AmendmentDTO>());
		amendmentsComboBox1.setDisplayField("name");
		amendmentsComboBox1.setTriggerAction(ComboBox.TriggerAction.ALL);

		amendmentsComboBox2 = new ComboBox<AmendmentDTO>();
		amendmentsComboBox2.setStore(new ListStore<AmendmentDTO>());
		amendmentsComboBox2.setDisplayField("name");
		amendmentsComboBox2.setTriggerAction(ComboBox.TriggerAction.ALL);

		final LayoutContainer container = Layouts.hBox(HBoxLayoutAlign.TOP);

		container.add(amendmentsComboBox1, Layouts.hBoxData(Margin.LEFT));
		container.add(amendmentsComboBox2, Layouts.hBoxData(Margin.LEFT));

		projectFields = buildGrid();

		mainPanel.setScrollMode(Scroll.AUTO);
		mainPanel.add(container);
		mainPanel.add(projectFields);

		initPopup(mainPanel);
	}

	public Grid<DiffEntry> buildGrid() {

		final ColumnModel columnModel = new ColumnModel(Arrays.asList(new ColumnConfig[] {
			new ColumnConfig(DiffEntry.FIELD_NAME, 200),
			new ColumnConfig(DiffEntry.DISPLAY_VALUE_1, 200),
			new ColumnConfig(DiffEntry.DISPLAY_VALUE_2, 200)
		}));

		final Grid<DiffEntry> grid = new Grid<DiffEntry>(new ListStore<DiffEntry>(), columnModel);
		grid.setHideHeaders(true);
		grid.setHeight(500);
		grid.setAutoWidth(true);
		grid.getView().setForceFit(true);
		
		return grid;

	}

	@Override
	public ContentPanel getMainPanel() {
		return mainPanel;
	}

	@Override
	public ComboBox<AmendmentDTO> getAmendmentsComboBox1() {
		return amendmentsComboBox1;
	}

	@Override
	public ListStore<AmendmentDTO> getAmendmentStore1() {
		return amendmentsComboBox1.getStore();
	}

	@Override
	public ComboBox<AmendmentDTO> getAmendmentsComboBox2() {
		return amendmentsComboBox2;
	}

	@Override
	public ListStore<AmendmentDTO> getAmendmentStore2() {
		return amendmentsComboBox2.getStore();
	}

	@Override
	public Grid<DiffEntry> getProjectFields() {
		return projectFields;
	}

	@Override
	public ListStore<DiffEntry> getProjectFieldsValueStore() {
		return projectFields.getStore();
	}

	private Text createGridText(String content) {
		final Text label = new Text(content);
		label.addStyleName("label-small");
		return label;
	}

}
