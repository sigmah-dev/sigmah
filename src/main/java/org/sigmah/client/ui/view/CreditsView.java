package org.sigmah.client.ui.view;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.CreditsPresenter;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.client.util.ClientUtils;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.Singleton;

/**
 * Credits frame view.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
@Singleton
public class CreditsView extends AbstractPopupView<PopupWidget> implements CreditsPresenter.View {

	// CSS.
	private static final String ID_CREDITS = "credits";
	private static final String CSS_PROPS = "properties";
	private static final String CSS_VERSION = "version";
	private static final String CSS_VERSION_LOGO = "version-logo";
	private static final String CSS_VERSION_NAME = "version-name";
	private static final String CSS_VERSION_NUMBER = "version-number";
	private static final String CSS_VERSION_REF = "version-ref";
	private static final String CSS_TEAM = "team";
	private static final String CSS_SCROLL = "team-scroll";
	private static final String CSS_TEAM_LABEL = "team-label";
	private static final String CSS_TIMELINE = "timeline";
	private static final String CSS_TIMELINE_LABEL = "timeline-label";
	private static final String CSS_TIMELINE_IMAGE = "timeline-image";

	private Label versionNameLabel;
	private Label versionNumberLabel;
	private Label versionRefLabel;

	private Panel managersPanel;
	private Panel partnersPanel;
	private Panel developersPanel;
	private Panel contributorsPanel;

	/**
	 * Builds the view.
	 */
	public CreditsView() {
		super(new PopupWidget(true), 735);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		// Main panel.
		final VerticalPanel mainPanel = new VerticalPanel();
		mainPanel.getElement().setId(ID_CREDITS);

		mainPanel.add(buildPropertiesPanel());
		mainPanel.add(buildTimelinePanel());

		initPopup(mainPanel);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HasText getVersionNameLabel() {
		return versionNameLabel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HasText getVersionNumberLabel() {
		return versionNumberLabel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HasText getVersionRefLabel() {
		return versionRefLabel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearManagers() {
		managersPanel.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addManager(final String name, final String url) {

		final StringBuilder sb = new StringBuilder();
		if (ClientUtils.isNotBlank(name)) {
			sb.append(name);
			sb.append(" ");
		}
		if (ClientUtils.isNotBlank(url)) {
			sb.append("<a href=\"");
			sb.append(url);
			sb.append("\" target=\"_blank\">");
			sb.append(url);
			sb.append("</a>");
		}

		final HTML html = new HTML(ClientUtils.trimToEmpty(sb.toString()));
		managersPanel.add(html);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearPartners() {
		partnersPanel.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPartner(final String name, final String url, final String role) {

		final StringBuilder sb = new StringBuilder();
		if (ClientUtils.isNotBlank(name)) {
			sb.append(name);
			sb.append(" ");
		}
		if (ClientUtils.isNotBlank(role)) {
			sb.append("(");
			sb.append(role);
			sb.append(") ");
		}
		if (ClientUtils.isNotBlank(url)) {
			sb.append("<a href=\"");
			sb.append(url);
			sb.append("\" target=\"_blank\">");
			sb.append(url);
			sb.append("</a>");
		}

		final HTML html = new HTML(ClientUtils.trimToEmpty(sb.toString()));
		partnersPanel.add(html);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearDevelopers() {
		developersPanel.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addDeveloper(final String name, final String email) {

		final StringBuilder sb = new StringBuilder();
		if (ClientUtils.isNotBlank(name)) {
			sb.append(name);
			sb.append(" ");
		}
		if (ClientUtils.isNotBlank(email)) {
			sb.append("&lt;<a href=\"mailto:");
			sb.append(email);
			sb.append("\" target=\"_blank\">");
			sb.append(email);
			sb.append("</a>&gt;");
		}

		final HTML html = new HTML(ClientUtils.trimToEmpty(sb.toString()));
		developersPanel.add(html);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearContributors() {
		contributorsPanel.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addContributor(final String name, final String email) {

		final StringBuilder sb = new StringBuilder();
		if (ClientUtils.isNotBlank(name)) {
			sb.append(name);
			sb.append(" ");
		}
		if (ClientUtils.isNotBlank(email)) {
			sb.append("&lt;<a href=\"mailto:");
			sb.append(email);
			sb.append("\" target=\"_blank\">");
			sb.append(email);
			sb.append("</a>&gt;");
		}

		final HTML html = new HTML(ClientUtils.trimToEmpty(sb.toString()));
		contributorsPanel.add(html);

	}

	// -------------------------------------
	// -- UI building methods.
	// -------------------------------------

	private Panel buildPropertiesPanel() {

		// Team panel.
		final ScrollPanel scrollTeamPanel = new ScrollPanel();
		scrollTeamPanel.addStyleName(CSS_SCROLL);
		scrollTeamPanel.add(buildTeamPanel());

		// Main panel.
		final HorizontalPanel panel = new HorizontalPanel();
		panel.addStyleName(CSS_PROPS);

		panel.add(buildVersionPanel());
		panel.add(scrollTeamPanel);

		return panel;

	}

	private Panel buildVersionPanel() {

		// Logo.
		final Label logo = new Label();
		logo.addStyleName(CSS_VERSION_LOGO);

		// Name.
		versionNameLabel = new Label();
		versionNameLabel.addStyleName(CSS_VERSION_NAME);

		// Number.
		versionNumberLabel = new Label();
		versionNumberLabel.addStyleName(CSS_VERSION_NUMBER);

		// Ref.
		versionRefLabel = new Label();
		versionRefLabel.addStyleName(CSS_VERSION_REF);

		// Main panel.
		final VerticalPanel panel = new VerticalPanel();
		panel.addStyleName(CSS_VERSION);

		panel.add(logo);
		panel.add(versionNameLabel);
		panel.add(versionNumberLabel);
		panel.add(versionRefLabel);

		return panel;

	}

	private Panel buildTeamPanel() {

		// Managers.
		final Label managersLabel = new Label(ClientUtils.colon(I18N.CONSTANTS.sigmah_managers()));
		managersLabel.addStyleName(CSS_TEAM_LABEL);
		managersPanel = new VerticalPanel();

		// Partners.
		final Label partnersLabel = new Label(ClientUtils.colon(I18N.CONSTANTS.sigmah_partners()));
		partnersLabel.addStyleName(CSS_TEAM_LABEL);
		partnersPanel = new VerticalPanel();

		// Developers.
		final Label developersLabel = new Label(ClientUtils.colon(I18N.CONSTANTS.sigmah_developers()));
		developersLabel.addStyleName(CSS_TEAM_LABEL);
		developersPanel = new VerticalPanel();

		// Contributors.
		final Label contributorsLabel = new Label(ClientUtils.colon(I18N.CONSTANTS.sigmah_contributors()));
		contributorsLabel.addStyleName(CSS_TEAM_LABEL);
		contributorsPanel = new VerticalPanel();

		// Main panel.
		final VerticalPanel panel = new VerticalPanel();
		panel.addStyleName(CSS_TEAM);

		panel.add(managersLabel);
		panel.add(managersPanel);
		panel.add(partnersLabel);
		panel.add(partnersPanel);
		panel.add(developersLabel);
		panel.add(developersPanel);
		panel.add(contributorsLabel);
		panel.add(contributorsPanel);

		return panel;

	}

	private Panel buildTimelinePanel() {

		// Top label.
		final Label label = new Label(I18N.CONSTANTS.sigmah_credits());
		label.addStyleName(CSS_TIMELINE_LABEL);

		// Timeline image.
		final Label image = new Label();
		image.addStyleName(CSS_TIMELINE_IMAGE);

		// Main panel.
		final VerticalPanel panel = new VerticalPanel();
		panel.addStyleName(CSS_TIMELINE);

		panel.add(label);
		panel.add(image);

		return panel;

	}

}
