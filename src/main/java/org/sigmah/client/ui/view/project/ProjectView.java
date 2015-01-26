package org.sigmah.client.ui.view.project;

import java.util.LinkedHashMap;
import java.util.Map;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.Page;
import org.sigmah.client.ui.presenter.project.ProjectPresenter;
import org.sigmah.client.ui.presenter.project.ProjectPresenter.ExportActionHandler;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.res.icon.dashboard.funding.FundingIconProvider;
import org.sigmah.client.ui.res.icon.dashboard.funding.FundingIconProvider.IconSize;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.SubMenuWidget;
import org.sigmah.client.ui.widget.SubMenuWidget.Orientation;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.layout.Layouts.LayoutOptions;
import org.sigmah.client.ui.widget.layout.Layouts.Margin;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.dto.AmendmentDTO;
import org.sigmah.shared.dto.referential.AmendmentAction;
import org.sigmah.shared.dto.referential.ProjectModelType;
import org.sigmah.shared.util.Pair;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.WidgetComponent;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.SplitButton;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout.VBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Singleton;

/**
 * {@link ProjectPresenter} view.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class ProjectView extends AbstractView implements ProjectPresenter.View {

	/**
	 * Project title max <em>displayed</em> length.
	 */
	private static final int PROJECT_TITLE_MAX_LENGTH = 110;

	/**
	 * Amendments panel width.
	 */
	private static final float AMENDMENTS_PANEL_WIDTH = 250f;

	// CSS style names.
	public static final String STYLE_HEADER_BANNER = "banner";
	public static final String STYLE_HEADER_BANNER_LOGO = "banner-logo";
	public static final String STYLE_HEADER_BANNER_FLEX = "banner-flex";
	private static final String STYLE_AMENDMENT_ACTION = "amendment-action";

	// Grids coordinates.
	private static final Pair<Integer, Integer> HEADER_BANNER_LOGO_CELL = new Pair<Integer, Integer>(0, 0);
	private static final Pair<Integer, Integer> HEADER_BANNER_WIDGET_CELL = new Pair<Integer, Integer>(0, 1);

	// UI widgets.
	private ContentPanel projectBannerPanel;
	private Grid projectBannerGrid;

	private ContentPanel amendmentsPanel;
	private Button lockerAmendmentButton;
	private Button validateVersionProjectCoreButton;
	private LayoutContainer amendmentActionsContainer;

	private SubMenuWidget subMenu;
	private LayoutContainer subViewPlaceHolder;

	private SplitButton amendmentsButton;
	private ListStore<AmendmentDTO> listAmendmentStore;
	private ListView<AmendmentDTO> listAmendments;
	private Menu amendmentsMenuActions;

	private Button exportButton;
	private Button deletetButton;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		// --
		// Banner container.
		// --

		final LayoutContainer headerContainer = Layouts.border();
		headerContainer.add(createProjectBannerPanel(), Layouts.borderLayoutData(LayoutRegion.CENTER, Margin.RIGHT));
		headerContainer.add(createAmendmentsPanel(), Layouts.borderLayoutData(LayoutRegion.EAST, AMENDMENTS_PANEL_WIDTH));

		// --
		// Menu container.
		// --

		final Map<Page, String> linksMap = new LinkedHashMap<Page, String>();
		linksMap.put(Page.PROJECT_DASHBOARD, I18N.CONSTANTS.projectTabDashboard());
		linksMap.put(Page.PROJECT_DETAILS, I18N.CONSTANTS.projectDetails());
		linksMap.put(Page.PROJECT_LOGFRAME, I18N.CONSTANTS.projectTabLogFrame());
		linksMap.put(Page.PROJECT_INDICATORS_MANAGEMENT, I18N.CONSTANTS.projectTabIndicators());
		linksMap.put(Page.PROJECT_INDICATORS_MAP, I18N.CONSTANTS.projectTabMap());
		linksMap.put(Page.PROJECT_INDICATORS_ENTRIES, I18N.CONSTANTS.projectTabDataEntry());
		linksMap.put(Page.PROJECT_CALENDAR, I18N.CONSTANTS.projectTabCalendar());
		linksMap.put(Page.PROJECT_REPORTS, I18N.CONSTANTS.projectTabReports());

		final Grid buttonsGrid = new Grid(1, 2);
		exportButton = Forms.button(I18N.CONSTANTS.export(), IconImageBundle.ICONS.excel());
		deletetButton = Forms.button(I18N.CONSTANTS.deleteProjectAnchor(), IconImageBundle.ICONS.remove());
		buttonsGrid.setWidget(0, 0, exportButton);
		buttonsGrid.setWidget(0, 1, deletetButton);
		buttonsGrid.getElement().getStyle().setFloat(Float.RIGHT);

		subMenu = new SubMenuWidget(Orientation.HORIZONTAL, linksMap);
		subMenu.asWidget().getElement().getStyle().setFloat(Float.LEFT);

		final FlowPanel menuContainer = new FlowPanel();
		menuContainer.add(subMenu.asWidget());
		menuContainer.add(buttonsGrid);

		// --
		// Center container.
		// --

		subViewPlaceHolder = Layouts.fit();

		// --
		// Main layout.
		// --

		final LayoutContainer mainContainer = Layouts.vBox();
		mainContainer.add(headerContainer, Layouts.vBoxData(Margin.BOTTOM));
		mainContainer.add(menuContainer);

		add(mainContainer, Layouts.borderLayoutData(LayoutRegion.NORTH, Layouts.BANNER_PANEL_HEIGHT, Margin.BOTTOM));
		add(subViewPlaceHolder);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LayoutContainer getPlaceHolder() {
		return subViewPlaceHolder;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SubMenuWidget getSubMenuWidget() {
		return subMenu;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProjectTitle(final String projectName, final String projectFullName) {

		final StringBuilder builder = new StringBuilder();
		builder.append(I18N.CONSTANTS.projectMainTabTitle()).append(' ').append(projectName);

		if (ClientUtils.isNotBlank(projectFullName)) {
			builder.append(" (").append(ClientUtils.abbreviate(projectFullName, PROJECT_TITLE_MAX_LENGTH)).append(')');
		}

		// Panel header title.
		projectBannerPanel.setHeadingText(builder.toString());

		// Tool tip configuration.
		final ToolTipConfig projectBannerToolTipConfig = new ToolTipConfig(builder.toString());
		projectBannerToolTipConfig.setMaxWidth(500);
		projectBannerPanel.setToolTip(projectBannerToolTipConfig);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProjectLogo(final ProjectModelType projectType) {

		final AbstractImagePrototype projectIcon = FundingIconProvider.getProjectTypeIcon(projectType, IconSize.LARGE);
		projectBannerGrid.setWidget(HEADER_BANNER_LOGO_CELL.left, HEADER_BANNER_LOGO_CELL.right, projectIcon.createImage());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProjectBanner(final Widget bannerWidget) {
		projectBannerGrid.setWidget(HEADER_BANNER_WIDGET_CELL.left, HEADER_BANNER_WIDGET_CELL.right, bannerWidget);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HTMLTable buildBannerTable(final int rows, final int cols) {

		final Grid gridLayout = new Grid(rows, cols);
		gridLayout.addStyleName(ProjectView.STYLE_HEADER_BANNER_FLEX);
		gridLayout.setCellPadding(0);
		gridLayout.setCellSpacing(0);
		gridLayout.setWidth("100%");
		gridLayout.setHeight("100%");

		for (int i = 0; i < gridLayout.getColumnCount() - 1; i++) {
			gridLayout.getColumnFormatter().setWidth(i, "325px");
		}

		return gridLayout;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Component getAmendmentBox() {
		return amendmentsPanel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getLockerAmendmentButton() {
		return lockerAmendmentButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getValidateVersionProjectCoreButton() {
		return validateVersionProjectCoreButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getExportButton() {
		return exportButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getDeletetButton() {
		return deletetButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ContentPanel getProjectBannerPanel() {
		return projectBannerPanel;
	}

	/**
	 * {@inheritDoc}
	 */

	@Override
	public SplitButton getAmendmentsButton() {
		return amendmentsButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListStore<AmendmentDTO> getListAmendmentStore() {
		return listAmendmentStore;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListView<AmendmentDTO> getListAmendments() {
		return listAmendments;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Menu getAmendmentsMenuActions() {
		return amendmentsMenuActions;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addAmendmentAction(final boolean clearFirst, final AmendmentAction action, final ClickHandler handler) {

		if (clearFirst) {
			amendmentActionsContainer.removeAll();
		}

		final Anchor actionAnchor = new Anchor(AmendmentAction.getName(action));
		actionAnchor.setStyleName(STYLE_AMENDMENT_ACTION);
		actionAnchor.addClickHandler(handler);

		amendmentActionsContainer.add(new WidgetComponent(actionAnchor), Layouts.vBoxData());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void buildExportDialog(final ExportActionHandler handler) {

		final Window w = new Window();
		w.setPlain(true);
		w.setModal(true);
		w.setBlinkModal(true);
		w.setLayout(new FitLayout());
		w.setSize(350, 180);
		w.setHeadingHtml(I18N.CONSTANTS.exportData());

		final FormPanel panel = Forms.panel();

		final CheckBox synthesisBox = Forms.checkbox(I18N.CONSTANTS.projectSynthesis(), Boolean.TRUE);
		synthesisBox.setEnabled(false);
		final CheckBox indicatorBox = Forms.checkbox(I18N.CONSTANTS.flexibleElementIndicatorsList());
		final CheckBox logFrameBox = Forms.checkbox(I18N.CONSTANTS.logFrame());

		final CheckBoxGroup options =
				Forms.checkBoxGroup(I18N.CONSTANTS.exportOptions(), com.extjs.gxt.ui.client.Style.Orientation.VERTICAL, synthesisBox, logFrameBox, indicatorBox);

		panel.add(options);

		final Button export = Forms.button(I18N.CONSTANTS.export());
		panel.getButtonBar().add(export);
		export.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent ce) {
				if (handler != null) {
					handler.onExportProject(indicatorBox, logFrameBox);
				}
				w.hide();
			}
		});

		w.add(panel);
		w.show();
	}

	/**
	 * Creates the project banner panel.
	 * 
	 * @return The project banner panel.
	 */
	private Component createProjectBannerPanel() {

		// Main panel.
		projectBannerPanel = Panels.content(I18N.CONSTANTS.loading()); // Temporary title.

		// Main grid.
		projectBannerGrid = new Grid(1, 2);
		projectBannerGrid.addStyleName(STYLE_HEADER_BANNER);
		projectBannerGrid.setCellPadding(0);
		projectBannerGrid.setCellSpacing(0);
		projectBannerGrid.setWidth("100%");
		projectBannerGrid.setHeight("100%");

		// Logo cell style.
		projectBannerGrid.getCellFormatter().setStyleName(HEADER_BANNER_LOGO_CELL.left, HEADER_BANNER_LOGO_CELL.right, STYLE_HEADER_BANNER_LOGO);

		projectBannerPanel.add(projectBannerGrid);

		return projectBannerPanel;
	}

	/**
	 * Creates the amendments panel.
	 * 
	 * @return The amendments panel component.
	 */
	private Component createAmendmentsPanel() {

		amendmentsPanel = Panels.content(I18N.CONSTANTS.projectCoreBoxTitle());
		amendmentsPanel.setIcon(IconImageBundle.ICONS.DNABrownRed());

		lockerAmendmentButton = Forms.button(I18N.CONSTANTS.projectCoreUnlockButton(), IconImageBundle.ICONS.unlock());
		validateVersionProjectCoreButton = Forms.button(I18N.CONSTANTS.projectCoreValidateVersion(), IconImageBundle.ICONS.validate());

		final LayoutContainer container = Layouts.hBox(HBoxLayoutAlign.TOP);

		container.add(lockerAmendmentButton, Layouts.hBoxData(Margin.RIGHT));
		container.add(validateVersionProjectCoreButton, Layouts.hBoxData(Margin.RIGHT));

		final LayoutContainer mainContainer = Layouts.vBox(new LayoutOptions(new Padding(4)));
		mainContainer.add(container);

		amendmentsButton = new SplitButton(I18N.CONSTANTS.projectCoreNoValidated());
		amendmentsMenuActions = createMenuAmendmentActions();
		amendmentsButton.setMenu(amendmentsMenuActions);

		LayoutContainer vlayout = Layouts.vBox(VBoxLayoutAlign.LEFT);
		vlayout.add(amendmentsButton);

		mainContainer.add(vlayout, Layouts.vBoxData(Margin.TOP));

		amendmentsPanel.add(mainContainer, Layouts.fitData());

		return amendmentsPanel;
	}

	private Menu createMenuAmendmentActions() {

		final Menu menu = new Menu();

		menu.add(new MenuItem(I18N.CONSTANTS.amendmentCompare()));
		menu.add(new MenuItem(I18N.CONSTANTS.amendmentRename()));

		menu.add(new SeparatorMenuItem());

		MenuItem item = new MenuItem(I18N.CONSTANTS.amendmentDisplayVersion());
		item.setEnabled(false);
		menu.add(item);

		listAmendmentStore = new ListStore<AmendmentDTO>();
		listAmendments = new ListView<AmendmentDTO>();

		listAmendments.setStore(listAmendmentStore);

		menu.add(listAmendments);

		return menu;

	}

}
