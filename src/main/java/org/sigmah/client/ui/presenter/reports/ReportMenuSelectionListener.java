package org.sigmah.client.ui.presenter.reports;

import org.sigmah.client.event.EventBus;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.shared.dto.element.FlexibleElementDTO;

import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;

/**
 * Report menu selection listener.<br>
 * This listener implementation is used for both <b>attach file</b> and <b>add report</b> menus.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class ReportMenuSelectionListener extends SelectionListener<MenuEvent> {

	// --
	// Application event bus.
	// --

	private final EventBus eventBus;

	// --
	// Destination's required parameters/data.
	// --

	private final Integer containerId;
	private final String phaseName;
	private final FlexibleElementDTO flexibleElement;
	private final boolean createReportMenu;

	/**
	 * @param eventBus
	 *          The application client event bus.
	 * @param containerId
	 *          The container id (Project or OrgUnit).
	 * @param phaseName
	 *          The phase name.
	 * @param flexibleElement
	 *          The flexible element.
	 * @param createReportMenu
	 *          {@code true} if the listener handles a <em>create report</em> menu, {@code false} if it handler an
	 *          <em>attach file</em> menu.
	 */
	public ReportMenuSelectionListener(EventBus eventBus, Integer containerId, String phaseName, FlexibleElementDTO flexibleElement, boolean createReportMenu) {
		this.eventBus = eventBus;
		this.containerId = containerId;
		this.phaseName = phaseName;
		this.flexibleElement = flexibleElement;
		this.createReportMenu = createReportMenu;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void componentSelected(final MenuEvent ce) {

		final Component sourceItem = (Component) ce.getSource();
		final Page page = createReportMenu ? Page.REPORT_CREATE : Page.ATTACH_FILE;

		eventBus.navigateRequest(page.requestWith(RequestParameter.ID, containerId).addParameter(RequestParameter.NAME, phaseName)
			.addData(RequestParameter.DTO, flexibleElement).addData(RequestParameter.SOURCE, sourceItem));
	}

}
