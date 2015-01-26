package org.sigmah.client.ui.view.project;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.project.ProjectDetailsPresenter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.layout.Layouts.LayoutOptions;
import org.sigmah.client.ui.widget.layout.Layouts.LayoutOptions.Scroll;
import org.sigmah.client.ui.widget.panel.Panels;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Layout;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout.VBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Singleton;

/**
 * {@link ProjectDetailsPresenter} view.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class ProjectDetailsView extends AbstractView implements ProjectDetailsPresenter.View {

	private ContentPanel mainPanel;
	private Button saveButton;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		final Layout layout = Layouts.vBoxLayout(VBoxLayoutAlign.STRETCH, new LayoutOptions(new Padding(5), false, Scroll.VERTICAL));

		mainPanel = Panels.content(I18N.CONSTANTS.projectDetails(), layout);
		mainPanel.setBorders(true);

		// Toolbar.
		mainPanel.setTopComponent(buildToolbar());

		add(mainPanel);
	}

	/**
	 * Builds the actions toolbar.
	 * 
	 * @return The actions toolbar.
	 */
	private ToolBar buildToolbar() {

		// Save button.
		saveButton = Forms.button(I18N.CONSTANTS.save(), IconImageBundle.ICONS.save());
		saveButton.setEnabled(false);

		// Actions toolbar.
		final ToolBar toolBar = new ToolBar();
		toolBar.setAlignment(HorizontalAlignment.LEFT);
		toolBar.setBorders(false);

		toolBar.add(saveButton);
		toolBar.add(new FillToolItem());

		return toolBar;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LayoutContainer getMainPanel() {
		return mainPanel;
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
	public void setMainPanelWidget(final Widget widget) {
		mainPanel.add(widget);
		mainPanel.layout();
	}

}
