/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.entry;

import org.sigmah.client.EventBus;
import org.sigmah.client.event.EntityEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.i18n.UIConstants;
import org.sigmah.client.page.common.Shutdownable;
import org.sigmah.shared.dto.ActivityDTO;
import org.sigmah.shared.dto.AttributeDTO;
import org.sigmah.shared.dto.AttributeGroupDTO;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.IndicatorGroup;
import org.sigmah.shared.dto.SiteDTO;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.inject.Inject;

/**
 * 
 * ContentPanel that displays the full details of a {@link SiteDTO} in 
 * natural HTML form.
 * 
 * @author Alex Bertram
 */
public class SiteDetailPanel extends ContentPanel implements Shutdownable {

	private final EventBus eventBus;
	private final UIConstants messages;

	private final Html html;

	private boolean showEmptyRows = false;

	private ActivityDTO currentActivity;
	private SiteDTO currentSite;
	
	private Listener siteListener;
	
	@Inject
	public SiteDetailPanel(EventBus eventBus, UIConstants messages) {
		this.eventBus = eventBus;
		this.messages = messages;
		
        setHeading(I18N.CONSTANTS.details());
        setScrollMode(Style.Scroll.AUTOY);
        setLayout(new FitLayout());

        html = new Html("&nbsp;");
        html.setStyleName("details");
        add(html);

		Listener siteListener = new Listener<EntityEvent<SiteDTO>>() {
			public void handleEvent(EntityEvent<SiteDTO> be) {
				if (be.getType() == EntityEvent.UPDATED) {
					if (currentSite != null && currentSite.getId() == be.getEntity().getId()) {
						updateHtml(be.getEntity());
					}
				}
			}
		};
		eventBus.addListener(EntityEvent.UPDATED, siteListener);
	}

	/**
	 * Displays the details of the given site in the Panel
	 * 
	 * @param activity
	 * @param site
	 */
	public void showSite(ActivityDTO activity, SiteDTO site) {
		this.currentActivity = activity;
		this.currentSite = site;
		updateHtml(site);
	}
	
	
	public void shutdown() {
		eventBus.removeListener(EntityEvent.UPDATED, siteListener);
	}

	private void updateHtml(SiteDTO site) {
		setHeading(site.getLocationName());
		html.setHtml(renderSite(site));		
	}


	public void clear() {
		setHeading(I18N.CONSTANTS.details());
		html.setHtml("&nbsp;");
	}
	
	private String renderSite(SiteDTO site) {

		StringBuilder html = new StringBuilder();

		if (site.getComments() != null) {
			String commentsHtml = site.getComments();
			commentsHtml = commentsHtml.replace("\n", "<br/>");
			html.append("<p class='comments'><span class='groupName'>")
					.append(messages.comments()).append(":</span> ")
					.append(commentsHtml).append("</p>");
		}

		for (AttributeGroupDTO group : currentActivity.getAttributeGroups()) {
			renderAttribute(html, group, site);
		}

		html.append("<table class='indicatorTable' cellspacing='0'>");
		for (IndicatorGroup group : currentActivity.groupIndicators()) {
			renderIndicatorGroup(html, group, site);
		}
		html.append("</table>");

		return html.toString();
	}

	private void renderIndicatorGroup(StringBuilder html, IndicatorGroup group,
			SiteDTO site) {
		StringBuilder groupHtml = new StringBuilder();
		boolean empty = true;

		if (group.getName() != null) {
			groupHtml.append("<tr><td class='indicatorGroupHeading'>")
					.append(group.getName())
					.append("</td><td>&nbsp;</td></tr>");
		}
		for (IndicatorDTO indicator : group.getIndicators()) {

			Double value;
			if (indicator.getAggregation() == IndicatorDTO.AGGREGATE_SITE_COUNT) {
				value = 1.0;
			} else {
				value = site.getIndicatorValue(indicator);
			}

			if (showEmptyRows
					|| (value != null && (indicator.getAggregation() != IndicatorDTO.AGGREGATE_SUM || value != 0))) {

				groupHtml.append("<tr><td class='indicatorHeading");
				if (group.getName() != null) {
					groupHtml.append(" indicatorGroupChild");
				}

				groupHtml.append("'>").append(indicator.getName())
						.append("</td><td class='indicatorValue'>")
						.append(formatValue(indicator, value))
						.append("</td><td class='indicatorUnits'>")
						.append(indicator.getUnits()).append("</td></tr>");
				empty = false;
			}
		}
		if (showEmptyRows || !empty) {
			html.append(groupHtml.toString());
		}
	}

	protected String formatValue(IndicatorDTO indicator, Double value) {
		if (value == null) {
			return "-";
		} else if(indicator.getAggregation() == IndicatorDTO.AGGREGATE_AVG) {
			return IndicatorNumberFormats.RATE.format(value);
		} else {
			return IndicatorNumberFormats.STOCK.format(value);
		}
	}

	protected void renderAttribute(StringBuilder html, AttributeGroupDTO group,
			SiteDTO site) {
		int count = 0;
		for (AttributeDTO attribute : group.getAttributes()) {
			Boolean value = site.getAttributeValue(attribute.getId());
			if (value != null && value) {
				if (count == 0) {
					html.append("<p class='attribute'><span class='groupName'>")
							.append(group.getName())
							.append(": </span><span class='attValues'>");
				} else {
					html.append(", ");
				}
				html.append(attribute.getName());
				count++;
			}
		}
		if (count != 0) {
			html.append("</span></p>");
		}
	}

}
