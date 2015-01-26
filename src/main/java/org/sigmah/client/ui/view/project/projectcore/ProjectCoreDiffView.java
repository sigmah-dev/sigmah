package org.sigmah.client.ui.view.project.projectcore;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.project.projectcore.ProjectCoreDiffPresenter;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.layout.Layouts.Margin;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.shared.dto.AmendmentDTO;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.referential.DefaultFlexibleElementType;
import org.sigmah.shared.dto.referential.ElementTypeEnum;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.google.inject.Singleton;

/**
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 */
@Singleton
public class ProjectCoreDiffView extends AbstractPopupView<PopupWidget> implements ProjectCoreDiffPresenter.View {

	private ContentPanel mainPanel;

	private Grid<ProjectCoreDiffLigne> projectFields;
	private ListStore<ProjectCoreDiffLigne> projectFieldsValueStor;

	private ComboBox<AmendmentDTO> amendmentsComboBox1;
	private ListStore<AmendmentDTO> amendmentStor1;
	private ComboBox<AmendmentDTO> amendmentsComboBox2;
	private ListStore<AmendmentDTO> amendmentStor2;

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
		amendmentStor1 = new ListStore<AmendmentDTO>();
		amendmentsComboBox1.setStore(amendmentStor1);

		amendmentsComboBox2 = new ComboBox<AmendmentDTO>();
		amendmentStor2 = new ListStore<AmendmentDTO>();
		amendmentsComboBox2.setStore(amendmentStor2);

		final LayoutContainer container = Layouts.hBox(HBoxLayoutAlign.TOP);

		container.add(amendmentsComboBox1, Layouts.hBoxData(Margin.LEFT));
		container.add(amendmentsComboBox2, Layouts.hBoxData(Margin.LEFT));

		projectFields = BuildGrid();

		mainPanel.setScrollMode(Scroll.AUTO);
		mainPanel.add(container);
		mainPanel.add(projectFields);

		initPopup(mainPanel);
	}

	public Grid<ProjectCoreDiffLigne> BuildGrid() {

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig("", "", 200);
		column.setRenderer(new GridCellRenderer<ProjectCoreDiffLigne>() {

			@Override
			public Object render(ProjectCoreDiffLigne model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<ProjectCoreDiffLigne> store,
					Grid<ProjectCoreDiffLigne> grid) {

				final String label;
				if (model.getField().getElementType() == ElementTypeEnum.DEFAULT) {
					label = DefaultFlexibleElementType.getName(((DefaultFlexibleElementDTO) model.getField()).getType());
				} else {
					label = model.getField().getLabel();
				}
				return createGridText(label);
			}
		});

		configs.add(column);

		column = new ColumnConfig("", "", 200);
		column.setRenderer(new GridCellRenderer<ProjectCoreDiffLigne>() {

			@Override
			public Object render(ProjectCoreDiffLigne model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<ProjectCoreDiffLigne> store,
					Grid<ProjectCoreDiffLigne> grid) {
				return createGridText(model.getValue1().toString());
			}

		});

		configs.add(column);

		column = new ColumnConfig("", "", 200);
		column.setRenderer(new GridCellRenderer<ProjectCoreDiffLigne>() {

			@Override
			public Object render(ProjectCoreDiffLigne model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<ProjectCoreDiffLigne> store,
					Grid<ProjectCoreDiffLigne> grid) {
				return createGridText(model.getValue2().toString());
			}
		});

		configs.add(column);

		projectFieldsValueStor = new ListStore<ProjectCoreDiffLigne>();

		ColumnModel cm = new ColumnModel(configs);

		Grid<ProjectCoreDiffLigne> grid = new Grid<ProjectCoreDiffLigne>(projectFieldsValueStor, cm);
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
	public ListStore<AmendmentDTO> getAmendmentStor1() {
		return amendmentStor1;
	}

	@Override
	public ComboBox<AmendmentDTO> getAmendmentsComboBox2() {
		return amendmentsComboBox2;
	}

	@Override
	public ListStore<AmendmentDTO> getAmendmentStor2() {
		return amendmentStor2;
	}

	@Override
	public Grid<ProjectCoreDiffLigne> getProjectFields() {
		return projectFields;
	}

	@Override
	public ListStore<ProjectCoreDiffLigne> getProjectFieldsValueStor() {
		return projectFieldsValueStor;
	}

	private Text createGridText(String content) {
		final Text label = new Text(content);
		label.addStyleName("label-small");
		return label;
	}

}
