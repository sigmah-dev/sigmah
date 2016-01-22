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

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.project.projectcore.ProjectCoreRenameVersionPresenter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.layout.Layouts.Margin;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.shared.dto.AmendmentDTO;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.inject.Singleton;

/**
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 */

@Singleton
public class ProjectCoreRenameVersionView extends AbstractPopupView<PopupWidget> implements ProjectCoreRenameVersionPresenter.View {

	private ContentPanel mainPanel;
	private Button saveButton;
	private Button canncelButton;
	private Grid<AmendmentDTO> gridContentAmendment;
	private ListStore<AmendmentDTO> amendmentStore;

	public ProjectCoreRenameVersionView() {
		super(new PopupWidget(true), 550);
	}

	@Override
	public void initialize() {

		mainPanel = Panels.content("");
		mainPanel.setHeaderVisible(false);

		gridContentAmendment = buildGrid();

		saveButton = Forms.button(I18N.CONSTANTS.save(), IconImageBundle.ICONS.save());
		canncelButton = Forms.button(I18N.CONSTANTS.cancel(), IconImageBundle.ICONS.cancel());

		mainPanel.add(gridContentAmendment);

		final LayoutContainer container = Layouts.hBox(HBoxLayoutAlign.TOP);

		container.add(saveButton);
		container.add(canncelButton, Layouts.hBoxData(Margin.LEFT));

		mainPanel.add(container);

		initPopup(mainPanel);

	}

	public Grid<AmendmentDTO> buildGrid() {

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig("version", I18N.CONSTANTS.projectCoreVersionNum(), 100);
		column.setRenderer(new GridCellRenderer<AmendmentDTO>() {

			@Override
			public Object render(AmendmentDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<AmendmentDTO> store,
					Grid<AmendmentDTO> grid) {
				final Text text = createGridText(model.getVersion().toString());
				return text;

			}
		});

		configs.add(column);

		column = new ColumnConfig("history_date", I18N.CONSTANTS.date(), 100);
		column.setRenderer(new GridCellRenderer<AmendmentDTO>() {

			@Override
			public Object render(AmendmentDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<AmendmentDTO> store,
					Grid<AmendmentDTO> grid) {

				final Text text = createGridText(DateTimeFormat.getShortDateFormat().format(model.getDate()));
				return text;
			}
		});

		configs.add(column);

		column = new ColumnConfig("name", I18N.CONSTANTS.name(), 200);
		column.setRenderer(new GridCellRenderer<AmendmentDTO>() {

			@Override
			public Object render(final AmendmentDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<AmendmentDTO> store,
					Grid<AmendmentDTO> grid) {
				final TextField<String> name = new TextField<String>();
				name.setValue(model.getName());
				name.addListener(Events.OnChange, new Listener<BaseEvent>() {

					@Override
					public void handleEvent(BaseEvent be) {

						model.setName(name.getValue());
						amendmentStore.update(model);

					};
				});

				return name;
			}
		});

		configs.add(column);

		amendmentStore = new ListStore<AmendmentDTO>();

		ColumnModel cm = new ColumnModel(configs);

		Grid<AmendmentDTO> grid = new Grid<AmendmentDTO>(amendmentStore, cm);
		grid.setAutoHeight(true);
		grid.setAutoWidth(true);
		grid.getView().setForceFit(true);
		return grid;

	}

	@Override
	public ContentPanel getMainPanel() {
		return mainPanel;
	}

	@Override
	public Button getSaveButton() {
		return saveButton;
	}

	@Override
	public Button getCanncelButton() {
		return canncelButton;
	}

	@Override
	public Grid<AmendmentDTO> getGridContentAmendment() {
		return gridContentAmendment;
	}

	@Override
	public ListStore<AmendmentDTO> getAmendmentStore() {
		return amendmentStore;
	}

	private Text createGridText(String content) {
		final Text label = new Text(content);
		label.addStyleName("label-small");
		return label;
	}
}
