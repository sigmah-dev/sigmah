package org.sigmah.client.ui.presenter.zone;

import java.util.HashMap;
import java.util.Map;

import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.event.handler.UpdateHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.presenter.base.AbstractZonePresenter;
import org.sigmah.client.ui.presenter.zone.MenuBannerPresenter.MenuTabId;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.view.zone.SearchView;
import org.sigmah.client.ui.widget.tab.Tab;
import org.sigmah.client.ui.widget.tab.TabBar;
import org.sigmah.client.ui.widget.tab.TabId;
import org.sigmah.client.ui.widget.tab.TabBar.TabBarListener;
import org.sigmah.client.ui.zone.Zone;
import org.sigmah.client.ui.zone.ZoneRequest;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.conf.PropertyName;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

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

/**
* Organization banner presenter displaying organization's name and logo.
* 
* @author 
*/

@Singleton
public class SearchPresenter extends AbstractZonePresenter<SearchPresenter.View> {
	
	@Inject
	public SearchPresenter(View view, Injector injector) {
		super(view, injector);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Project tab id.
	 */
	private final static String SEARCH_RESULTS_TAB_ID="searchResultsTabId";

	/**
	 * View interface.
	 */
	@ImplementedBy(SearchView.class)
	public static interface View extends ViewInterface {

		HasHTML getNameLabel();
		Panel getSearchBarPanel();
		ListBox getSearchOptions();
		TextBox getSearchText();
		Button getSearchButton();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Zone getZone() {
		// TODO Auto-generated method stub
		return Zone.SEARCH_BANNER;
	}
	
	/**
	 * Utility method to add a tab.
	 * 
	 * @param request
	 *          The page request.
	 */
	private void addTab(final PageRequest request) {

		// Login case?
		if (Page.LOGIN.equals(request.getPage()) || Page.RESET_PASSWORD.equals(request.getPage())) {
			return;
		}

		boolean closeable = true;
		String styleName = "default";

		// Homepage case ?
		if (Page.SEARCH_RESULTS.equals(request.getPage())) {
			closeable = false;
			styleName = "home";
		}

		// Builds the tab.
		final Tab tab = new Tab(new MenuTabId(request), closeable, styleName);

		if(request.getPage()==Page.SEARCH_RESULTS){
			tab.getElement().setId(DASHBOARD_HOME_TAB_ID);
		}		
		// Adds the tab.
		view.getTabBar().addTab(tab);
		requests.put(tab.getId(), new PageRequest(request)); // Important: create a new instance.

		// Sets the first title.
		final String pageTitle = Page.getTitle(request.getPage());
		final String tabTitle = ClientUtils.isNotBlank(pageTitle) && !PropertyName.isErrorKey(pageTitle) ? pageTitle : I18N.CONSTANTS.loading();
		view.getTabBar().updateTitle(tab.getId(), tabTitle);

	}
	
	/**
	 * Builds the unique tab id from the page request.
	 * 
	 * @author Tom Miette (tmiette@ideia.fr)
	 */
	private static class MenuTabId implements TabId {

		private final String token;
		private final Map<RequestParameter, String> params;

		private MenuTabId(final PageRequest request) {

			this.token = request.getPage().getParentKey() != null ? request.getPage().getParentKey() : request.getPage().getToken();
			this.params = request.getParameters(true);

		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(Object obj) {

			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}

			final MenuTabId other = (MenuTabId) obj;

			// Compares tokens.
			boolean equals = token.equals(other.token);

			// Compares parameters maps sizes.
			if (equals) {
				equals &= params.size() == other.params.size();
			}

			// Compares parameters keys and values.
			if (equals) {
				for (final Map.Entry<RequestParameter, String> entry : params.entrySet()) {

					// Key exists ?
					if (!other.params.keySet().contains(entry.getKey())) {
						equals = false;
						break;
					}

					// Values equals ?
					if (!entry.getValue().equals(other.params.get(entry.getKey()))) {
						equals = false;
						break;
					}

				}
			}

			return equals;

		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((params == null) ? 0 : params.hashCode());
			result = prime * result + ((token == null) ? 0 : token.hashCode());
			return result;
		}

	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {
		
		view.getSearchButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// Add the search results tab.
				addTab(new PageRequest(Page.SEARCH_RESULTS));
			}

		});
		
		

		// Project delete event handler.
		registerHandler(eventBus.addHandler(UpdateEvent.getType(), new UpdateHandler() {

			@Override
			public void onUpdate(final UpdateEvent event) {

				if (event.concern(UpdateEvent.PROJECT_DELETE)) {
					final PageRequest request = event.getParam(0);
					view.getTabBar().removeTab(new MenuTabId(request));
				}
			}
		}));

		// Contact delete event handler.
		registerHandler(eventBus.addHandler(UpdateEvent.getType(), new UpdateHandler() {

			@Override
			public void onUpdate(final UpdateEvent event) {

				if (event.concern(UpdateEvent.CONTACT_DELETE)) {
					final PageRequest request = event.getParam(0);
					view.getTabBar().removeTab(new MenuTabId(request));
				}
			}
		}));

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onZoneRequest(ZoneRequest zoneRequest) {
		// TODO Auto-generated method stub
		
	}

}
