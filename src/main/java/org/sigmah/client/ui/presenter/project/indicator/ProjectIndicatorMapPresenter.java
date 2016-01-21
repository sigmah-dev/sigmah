package org.sigmah.client.ui.presenter.project.indicator;

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


import org.sigmah.client.ui.view.project.indicator.SiteGridPanel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.shared.dto.SiteDTO;

import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.event.SiteEvent;
import org.sigmah.client.event.handler.SiteHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.presenter.project.AbstractProjectPresenter;
import org.sigmah.client.ui.view.project.indicator.ProjectIndicatorMapView;
import org.sigmah.client.ui.widget.map.Pin;
import org.sigmah.client.ui.widget.map.WorldMap;
import org.sigmah.shared.command.BatchCommand;
import org.sigmah.shared.command.GetMainSite;
import org.sigmah.shared.command.GetSchema;
import org.sigmah.shared.command.GetSitePoints;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.Result;
import org.sigmah.shared.command.result.SitePointList;
import org.sigmah.shared.dto.SchemaDTO;
import org.sigmah.shared.dto.SitePointDTO;
import org.sigmah.shared.dto.country.CountryDTO;
import org.sigmah.shared.dto.referential.DimensionType;
import org.sigmah.shared.util.Filter;

/**
 * Project's indicators map and sites presenter which manages the {@link ProjectIndicatorMapView}.
 * 
 * @author sherzod
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class ProjectIndicatorMapPresenter extends AbstractProjectPresenter<ProjectIndicatorMapPresenter.View> {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(ProjectIndicatorMapView.class)
	public static interface View extends AbstractProjectPresenter.View {
		SiteGridPanel getSiteGridPanel();
		WorldMap getWorldMap();
		
		void onMapLoaded();
		void setPins(List<Pin> pins);
		void setStatusMessage(String message, boolean busy);
	}
	
	private Integer projectId;
	private Integer mainSiteId;
	private SchemaDTO schema;

	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 */
	@Inject
	public ProjectIndicatorMapPresenter(final View view, final Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.PROJECT_INDICATORS_MAP;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {
		view.getSiteGridPanel().getNewSiteButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				addSite();
			}
		});
		view.getSiteGridPanel().getEditButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				editSite();
			}
		});
		view.getSiteGridPanel().getManageMainSiteButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				manageMainSite();
			}
		});
		eventBus.addHandler(SiteEvent.getType(), new SiteHandler() {

			@Override
			public void handleEvent(SiteEvent siteEvent) {
				if(siteEvent.getAction() == SiteEvent.Action.MAIN_SITE_CREATED) {
					// Updates the main site identifier.
					mainSiteId = siteEvent.getSiteId();
				}
				
				// Refresh the map.
				final Filter siteFilter = new Filter();
				siteFilter.addRestriction(DimensionType.Database, projectId);
		
				dispatch.execute(new GetSitePoints(siteFilter), new CommandResultHandler<SitePointList>() {

					@Override
					protected void onCommandSuccess(SitePointList result) {
						displaySites(result);
					}
				}, view.getWorldMap());
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(final PageRequest request) {
		projectId = request.getParameterInteger(RequestParameter.ID);
		schema = null;
		
		view.getSiteGridPanel().clear();
		
		final Filter siteFilter = new Filter();
		siteFilter.addRestriction(DimensionType.Database, projectId);
		
		view.setStatusMessage(I18N.CONSTANTS.loading(), true);
		dispatch.execute(new BatchCommand(new GetSchema(), new GetMainSite(projectId), new GetSitePoints(siteFilter)), new CommandResultHandler<ListResult<Result>>() {

			@Override
			protected void onCommandSuccess(ListResult<Result> result) {
				schema = (SchemaDTO) result.getList().get(0);
				view.getSiteGridPanel().setSchema(schema);
				final SiteDTO mainSite = (SiteDTO) result.getList().get(1);
				final SitePointList points = (SitePointList) result.getList().get(2);
				
				mainSiteId = mainSite != null ? mainSite.getId() : null;
				
				// Load the sites in the grid.
				view.getSiteGridPanel().load(siteFilter, mainSiteId);
				
				// Display the sites on the world map.
				displaySites(points);
			}
			
		}, new LoadingMask(view.getSiteGridPanel()), view.getWorldMap());
	}

	@Override
	protected boolean hasValueChanged() {
		return view.getSiteGridPanel().isSiteUpdated();
	}

	private void displaySites(SitePointList points) {
		final ArrayList<Pin> pins = new ArrayList<Pin>();
		for(final SitePointDTO point : points.getPoints()) {
			pins.add(new Pin(point, mainSiteId != null && mainSiteId.equals(point.getSiteId())));
		}
		
		// Updates the map status bar
		view.setPins(pins);
		view.setStatusMessage(I18N.MESSAGES.siteLoadStatus(
            			Integer.toString(points.getPoints().size()), 
            			Integer.toString(points.getWithoutCoordinates())), false);
	}
	
	private SiteDTO createEmptySite() {
		final SiteDTO site = new SiteDTO();
		site.setDatabaseId(projectId);
		
		final CountryDTO country = schema.getDatabaseById(projectId).getCountry();
		if(country != null && country.getBounds() != null) {
			site.setX(country.getBounds().getCenterX());
			site.setY(country.getBounds().getCenterY());
		}
		
		return site;
	}
	
	private void addSite() {
		eventBus.navigateRequest(Page.SITE_EDIT.request()
			.addParameter(RequestParameter.ID, projectId)
			.addData(RequestParameter.DTO, createEmptySite())
			.addData(RequestParameter.MODEL, schema));
	}

	private void editSite() {
		final List<SiteDTO> selection = view.getSiteGridPanel().getSelection();
		if (!selection.isEmpty()) {
			final SiteDTO site = selection.get(0);
			
			eventBus.navigateRequest(Page.SITE_EDIT.request()
				.addParameter(RequestParameter.ID, projectId)
				.addData(RequestParameter.DTO, site)
				.addData(RequestParameter.MODEL, schema));
		}
	}
	
	private void manageMainSite() {
		final SiteDTO site;

		final List<SiteDTO> selection = view.getSiteGridPanel().getSelection();
		if (!selection.isEmpty()) {
			site = selection.get(0);
		} else {
			site = createEmptySite();
		}
		
		eventBus.navigateRequest(Page.SITE_EDIT.request()
			.addParameter(RequestParameter.ID, projectId)
			.addParameter(RequestParameter.TYPE, "main")
			.addData(RequestParameter.DTO, site)
			.addData(RequestParameter.MODEL, schema));
	}
}
