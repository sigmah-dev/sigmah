package org.sigmah.client.ui.view.project.projectcore;


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
@Singleton
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
