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

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.event.SiteEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.presenter.base.HasForm;
import org.sigmah.client.ui.view.base.ViewPopupInterface;
import org.sigmah.client.ui.view.project.indicator.EditSiteView;
import org.sigmah.client.ui.widget.Loadable;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.map.Pin;
import org.sigmah.client.ui.widget.map.PinDragEndHandler;
import org.sigmah.shared.command.BatchCommand;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.GetAdminEntities;
import org.sigmah.shared.command.UpdateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.Result;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.AdminEntityDTO;
import org.sigmah.shared.dto.AdminLevelDTO;
import org.sigmah.shared.dto.BoundingBoxDTO;
import org.sigmah.shared.dto.SchemaDTO;
import org.sigmah.shared.dto.SiteDTO;
import org.sigmah.shared.dto.UserDatabaseDTO;
import org.sigmah.shared.dto.country.CountryDTO;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class EditSitePresenter extends AbstractPagePresenter<EditSitePresenter.View> implements HasForm {
	
	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(EditSiteView.class)
	public static interface View extends ViewPopupInterface {

		FormPanel getForm();

		// Fields.
		FieldSet getLocationFieldSet();
		ComboBox<AdminEntityDTO> getTopLevelComboBox();
		Field<Double> getLatitudeField();
		Field<Double> getLongitudeField();
		Pin getPin();
		
		Button getSaveButton();
		Button getCancelButton();
		
		void panTo(double latitude, double longitude);
		
		void setCountry(CountryDTO country, Listener<FieldEvent> selectionListener);
		BoundingBoxDTO getMapBounds();
		void setMapBounds(BoundingBoxDTO bounds);
		void setPinPosition(double latitude, double longitude);
	}
	
	private Integer projectId;
	private SiteDTO site;
	private boolean mainSite;
	
	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 */
	@Inject
	protected EditSitePresenter(final EditSitePresenter.View view, final Injector injector) {
		super(view, injector);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.SITE_EDIT;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public FormPanel[] getForms() {
		return new FormPanel[] { 
			view.getForm()
		};
	}

	@Override
	public void onBind() {
		view.getPin().addPinDragEndHandler(new PinDragEndHandler() {

			@Override
			public void onDragEnd(double latitude, double longitude) {
				onMarkerMoved(latitude, longitude);
			}
		});
		
		view.getSaveButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				onSaveAction();
			}
		});
		
		view.getCancelButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				hideView();
			}
		});
		
		final Listener<FieldEvent> latitudeLongitudeChangeListener = new Listener<FieldEvent>() {
			@Override
            public void handleEvent(FieldEvent be) {
                onCoordinatesChanged(view.getLongitudeField().getValue(), view.getLatitudeField().getValue());
            }
        };
		
		view.getLongitudeField().addListener(Events.Change, latitudeLongitudeChangeListener);
		view.getLatitudeField().addListener(Events.Change, latitudeLongitudeChangeListener);
	}

	@Override
	public void onPageRequest(PageRequest request) {
		// Reading parameters.
		projectId = request.getParameterInteger(RequestParameter.ID);
		mainSite = "main".equals(request.getParameter(RequestParameter.TYPE));
		site = request.getData(RequestParameter.DTO);
		final SchemaDTO schema = request.getData(RequestParameter.MODEL);

		// Retrieving the current database.
		final UserDatabaseDTO database = schema.getDatabaseById(projectId);
		assert database != null;

		// Sets the popup title
		if(mainSite) {
			setPageTitle(I18N.CONSTANTS.projectMainSite());
		} else if(site.getId() == null) {
			setPageTitle(I18N.CONSTANTS.newSite());
		} else {
			setPageTitle(I18N.CONSTANTS.editSite());
		}
		
		// Creates the admin entities fields.
		final CountryDTO country = database.getCountry();
		
		view.setCountry(country, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				final ComboBox<AdminEntityDTO> comboBox = (ComboBox<AdminEntityDTO>) be.getField();

				if(be.getEventTypeInt() == Event.ONKEYUP && (
					be.getEvent().getKeyCode() == KeyCodes.KEY_DELETE ||
					be.getEvent().getKeyCode() == KeyCodes.KEY_BACKSPACE)) {
					comboBox.setValue(null);
				}

				updateAdminEntityChild(comboBox);
				setLatitudeAndLongitude(getAdminEntityBounds(comboBox));
			}
		});
		
		setValues(site);
		
		if(site.getId() == null) {
			updateAdminEntity(view.getTopLevelComboBox());
		} else {
			loadAdminEntities(view.getTopLevelComboBox());
		}
		
		if(site.getX() != null && site.getY() != null) {
			// Move to the site coordinates.
			view.setMapBounds(new BoundingBoxDTO(site.getX(), site.getY()));
			view.setPinPosition(site.getY(), site.getX());
			
		} else if(country != null) {
			setLatitudeAndLongitude(country.getBounds());
		}
	}
	
	private void onCoordinatesChanged(Double latitude, Double longitude) {
		if(latitude != null && longitude != null) {
			view.setPinPosition(latitude, longitude);

            if(!view.getMapBounds().contains(longitude, latitude)) {
                view.panTo(latitude, longitude);
            }
		}
	}
	
	private void setLatitudeAndLongitude(BoundingBoxDTO boundingBoxDTO) {
		if(boundingBoxDTO != null) {
			view.setPinPosition(boundingBoxDTO.getCenterY(), boundingBoxDTO.getCenterX());
			onMarkerMoved(boundingBoxDTO.getCenterY(), boundingBoxDTO.getCenterX());
		} else {
			view.setPinPosition(0, 0);
			onMarkerMoved(0, 0);
		}
		view.setMapBounds(boundingBoxDTO);
	}
	
	private void onMarkerMoved(double latitude, double longitude) {
		view.getLatitudeField().setValue(latitude);
		view.getLongitudeField().setValue(longitude);
	}
	
	private void onSaveAction() {
		// --
		// Forms validation.
		// --

		if (!FormPanel.valid(view.getForm())) {
			// Form(s) validation failed.
			return;
		}
		
		// Save
		if(site.getId() == null) {
			saveSite();
		} else {
			updateSite();
		}
	}
	
	private void saveSite() {
		dispatch.execute(new CreateEntity(SiteDTO.ENTITY_NAME, getValues()), new CommandResultHandler<CreateResult>() {

			@Override
			protected void onCommandSuccess(CreateResult result) {
				N10N.infoNotif(I18N.CONSTANTS.saved(), I18N.CONSTANTS.saved());
				site.setId((Integer) result.getEntity().getId());
				site.setProperties(getValues());
				
				final SiteEvent.Action action = mainSite ? SiteEvent.Action.MAIN_SITE_CREATED : SiteEvent.Action.CREATED;
				eventBus.fireEvent(new SiteEvent(action, this, site));
				
				hideView();
			}
		}, view.getSaveButton(), view.getCancelButton());
	}
	
	private void updateSite() {
		dispatch.execute(new UpdateEntity(site, getValues()), new CommandResultHandler<VoidResult>() {

			@Override
			protected void onCommandSuccess(VoidResult result) {
				N10N.infoNotif(I18N.CONSTANTS.saved(), I18N.CONSTANTS.saved());
				site.setProperties(getValues());
				
				final SiteEvent.Action action = mainSite ? SiteEvent.Action.MAIN_SITE_UPDATED : SiteEvent.Action.UPDATED;
				eventBus.fireEvent(new SiteEvent(action, this, site));
				
				hideView();
			}
		}, view.getSaveButton(), view.getCancelButton());
	}
	
	private Map<String, Object> getValues() {
		final Map<String, Object> values = new HashMap<String, Object>();
		
		values.put(SiteDTO.DATABASE_ID, projectId);
		
		for(final Field<?> field : view.getForm().getFields()) {
			values.put(field.getName(), field.getValue());
		}
		values.put(view.getLatitudeField().getName(), view.getLatitudeField().getValue());
		values.put(view.getLongitudeField().getName(), view.getLongitudeField().getValue());
		
		return values;
	}
	
	private void setValues(SiteDTO siteDTO) {
		for(final Field field : view.getForm().getFields()) {
			field.setOriginalValue(siteDTO.get(field.getName()));
			field.setValue(siteDTO.get(field.getName()));
		}
		view.getLatitudeField().setValue(site.getY());
		view.getLongitudeField().setValue(site.getX());
	}
	
	private void updateAdminEntityChild(ComboBox<AdminEntityDTO> comboBox) {
		final ComboBox<AdminEntityDTO> childComboBox = comboBox.getData("child");
		updateAdminEntity(childComboBox, true, false);
    }
	
	private void updateAdminEntity(final ComboBox<AdminEntityDTO> comboBox) {
		updateAdminEntity(comboBox, false, true);
	}
	private void updateAdminEntity(final ComboBox<AdminEntityDTO> comboBox, boolean resetValue, final boolean forceNoReset) {
		if(comboBox == null) {
			return;
		}
		
		final AdminLevelDTO level = comboBox.getData("level");
		final ComboBox<AdminEntityDTO> parentComboBox = comboBox.getData("parent");
		final ComboBox<AdminEntityDTO> childComboBox = comboBox.getData("child");
		
		final GetAdminEntities getAdminEntities;
		
		if(parentComboBox == null) {
			// Fetch all parent entities
			getAdminEntities = new GetAdminEntities(level.getId());
			
		} else {
			final AdminEntityDTO parentAdminEntity = parentComboBox.getValue();
			if(parentAdminEntity != null) {
				// Fetch only the entities related to the parent entity.
				getAdminEntities = new GetAdminEntities(level.getId(), parentAdminEntity.getId());
				
			} else {
				getAdminEntities = null;
				comboBox.setValue(null);
			}
		}

		if(resetValue) {
			comboBox.setValue(null);
		}
		
		// Remove the current levels.
		comboBox.getStore().removeAll();
		
		if(getAdminEntities != null) {
			// Fetch the levels.
			dispatch.execute(getAdminEntities, new CommandResultHandler<ListResult<AdminEntityDTO>>() {

				@Override
				protected void onCommandSuccess(ListResult<AdminEntityDTO> result) {
					// Update the store and allow selection for this level.
					comboBox.getStore().add(result.getData());
					comboBox.setEnabled(true);
					
					updateAdminEntity(childComboBox, !forceNoReset, forceNoReset);
				}
			}, new LoadingMask(comboBox));
			
		} else {
			// No parent level has been selected, disable this combo.
			comboBox.setEnabled(false);
			updateAdminEntity(childComboBox, !forceNoReset, forceNoReset);
		}
	}
	
	private BoundingBoxDTO getAdminEntityBounds(ComboBox<AdminEntityDTO> comboBox) {
		BoundingBoxDTO bounds = null;
		ComboBox<AdminEntityDTO> adminEntityComboBox = comboBox;
		
		do {
			final AdminEntityDTO adminEntity = adminEntityComboBox.getValue();
			if(adminEntity != null) {
				bounds = adminEntity.getBounds();
			}
			adminEntityComboBox = comboBox.getData("parent");
			
		} while(bounds == null && adminEntityComboBox != null);
		
		return bounds;
	}
	
	private void loadAdminEntities(final ComboBox<AdminEntityDTO> rootComboBox) {
		final BatchCommand batchCommand = new BatchCommand();
		final ArrayList<Loadable> masks = new ArrayList<Loadable>();

		final ArrayList<ComboBox<AdminEntityDTO>> affectedComboBoxes = new ArrayList<ComboBox<AdminEntityDTO>>();
		
		ComboBox<AdminEntityDTO> comboBox = rootComboBox;
		while(comboBox != null) {
			comboBox.getStore().removeAll();
			
			final AdminLevelDTO level = comboBox.getData("level");
			final ComboBox<AdminEntityDTO> parentComboBox = comboBox.getData("parent");
			
			final GetAdminEntities getAdminEntities;
			
			if(parentComboBox == null) {
				// Fetch all parent entities
				getAdminEntities = new GetAdminEntities(level.getId());

			} else {
				final AdminEntityDTO parentAdminEntity = parentComboBox.getValue();
				if(parentAdminEntity != null) {
					// Fetch only the entities related to the parent entity.
					getAdminEntities = new GetAdminEntities(level.getId(), parentAdminEntity.getId());
					
				} else {
					getAdminEntities = null;
					comboBox.setValue(null);
				}
			}
			if(getAdminEntities != null) {
				batchCommand.add(getAdminEntities);
				masks.add(new LoadingMask(comboBox));
				affectedComboBoxes.add(comboBox);
			}
			
			comboBox = comboBox.getData("child");
		}
		
		dispatch.execute(batchCommand, new CommandResultHandler<ListResult<Result>>() {

			@Override
			protected void onCommandSuccess(ListResult<Result> listResult) {
				final List<Result> results = listResult.getList();
				
				for(int index = 0; index < results.size(); index++) {
					final ComboBox<AdminEntityDTO> comboBox = affectedComboBoxes.get(index);
					final ListResult<AdminEntityDTO> entities = (ListResult<AdminEntityDTO>) results.get(index);
					comboBox.getStore().add(entities.getData());
					comboBox.setEnabled(true);
				}
			}
		}, masks);
	}
}
