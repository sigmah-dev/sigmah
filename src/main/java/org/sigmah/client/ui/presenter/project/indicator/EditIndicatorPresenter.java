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
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.List;
import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.presenter.base.HasForm;
import org.sigmah.client.ui.view.base.ViewPopupInterface;
import org.sigmah.client.ui.view.pivot.LayoutComposer;
import org.sigmah.client.ui.view.pivot.table.PivotGridPanel;
import org.sigmah.client.ui.view.project.indicator.EditIndicatorView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.DatasourceField;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.util.GWTDates;
import org.sigmah.shared.command.BatchCommand;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.GenerateElement;
import org.sigmah.shared.command.GetIndicators;
import org.sigmah.shared.command.GetProject;
import org.sigmah.shared.command.UpdateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.IndicatorListResult;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.Result;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.IndicatorGroup;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.pivot.content.PivotContent;
import org.sigmah.shared.dto.pivot.model.PivotTableElement;

/**
 * Indicator presenter which manages the {@link EditIndicatorView}.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class EditIndicatorPresenter extends AbstractPagePresenter<EditIndicatorPresenter.View> implements HasForm {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(EditIndicatorView.class)
	public static interface View extends ViewPopupInterface {

		FormPanel getForm();

		// Fields.
		Field<String> getCodeField();
		Field<String> getNameField();
		ComboBox<IndicatorGroup> getIndicatorGroupField();
		RadioGroup getTypeField();
		Radio getTypeQuantitativeRadio();
		Radio getTypeQualitativeTypeRadio();
		Field<Object> getLabelsField();
		RadioGroup getAggregationField();
		Radio getAggregationSumRadio();
		Radio getAggregationAverageTypeRadio();
		Field<String> getUnitsField();
		Field<Number> getObjectiveField();
		Field<String> getVerificationField();
		Field<String> getDescriptionField();
		DatasourceField getDatasourceField();
		
		PivotGridPanel getPivotGridPanel();
		
		Button getSaveButton();
		Button getCancelButton();
		
		void loadIndicator(Integer projectId, IndicatorDTO indicator);
		void setDataEntryVisible(boolean visible);
	}
	
	private Integer projectId;
	private Integer indicatorId;
	private String category;
	
	private AsyncCallback<IndicatorDTO> callback;
	
	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 */
	@Inject
	protected EditIndicatorPresenter(final EditIndicatorPresenter.View view, final Injector injector) {
		super(view, injector);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.INDICATOR_EDIT;
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {
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
		
		view.getTypeField().addListener(Events.Change, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				updateFormLayout();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(PageRequest request) {
		this.projectId = request.getParameterInteger(RequestParameter.ID);
		this.category = request.getParameter(RequestParameter.CATEGORY);
		this.callback = request.getData(RequestParameter.REQUEST);
		final IndicatorDTO indicatorDTO = request.getData(RequestParameter.MODEL);

		if(indicatorDTO != null) {
			// Edit mode.
			// Sets the page title.
			setPageTitle(indicatorDTO.getName());
			
			// Saving the current indicator identifer
			this.indicatorId = indicatorDTO.getId();
			
			// Update category if present
			if(indicatorDTO.getCategory() != null) {
				this.category = indicatorDTO.getCategory();
			}
			
			view.setDataEntryVisible(true);
			loadPivot();
			
		} else {
			// Create mode.
			setPageTitle(I18N.CONSTANTS.newIndicator());
			this.indicatorId = null;
			view.setDataEntryVisible(false);
		}
		
		// Load groups
		loadIndicatorGroups(projectId, indicatorDTO);

		// Display the current indicator
		view.loadIndicator(projectId, indicatorDTO);
		updateFormLayout();
	}
	
	/**
	 * Callback executed on save button action.
	 */
	private void onSaveAction() {
		// --
		// Forms validation.
		// --

		if (!isValid()) {
			// Form(s) validation failed.
			N10N.warn(I18N.CONSTANTS.form_validation_ko());
			return;
		}
		
		// --
		// Save or update the indicator.
		// --
		
		if(indicatorId == null) {
			// Save.
			dispatch.execute(new CreateEntity(getIndicator()), new CommandResultHandler<CreateResult>() {

				@Override
				protected void onCommandSuccess(CreateResult result) {
					// Send an event to refresh the grid.
					fireUpdateEvent((Integer)result.getEntity().getId());

					// Hides view and displays notification.
					hideView();
				}
			}, view.getSaveButton());
			
		} else {
			// Update.
			final IndicatorDTO indicatorDTO = getIndicator();
			
			final BatchCommand updateCommand = new BatchCommand(new UpdateEntity(indicatorDTO, indicatorDTO.getProperties()));
			updateCommand.addAll(view.getPivotGridPanel().composeSaveCommand());
			
			dispatch.execute(updateCommand, new CommandResultHandler<ListResult<Result>>() {

				@Override
				protected void onCommandSuccess(ListResult<Result> result) {
					// Send an event to refresh the grid.
					fireUpdateEvent(indicatorId);

					// Hides view and displays notification.
					hideView();
				}
			});
		}
	}
	
	/**
	 * Validates only the visible fields of the form.
	 * 
	 * @return <code>true</code> if all visible fields are valids, <code>false</code> otherwise.
	 */
	private boolean isValid() {
		boolean valid = true;
		
		for (Field<?> field : view.getForm().getFields()) {
		  if (field.isVisible() && !field.isValid(true)) {
			valid = false;
		  }
		}
		
		return valid;
	}
	
	/**
	 * Fires the update event to notify registered presenter(s).
	 */
	private void fireUpdateEvent(int indicatorId) {
		final IndicatorDTO indicator = getIndicator();
		indicator.setId(indicatorId);
		
		eventBus.fireEvent(new UpdateEvent(UpdateEvent.INDICATOR_UPDATED, indicatorId, indicator.getProperties()));
		
		if(callback != null) {
			callback.onSuccess(indicator);
		}
	}
	
	/**
	 * Load the indicator groups of the given project.
	 * @param projectId Project identifier.
	 */
	private void loadIndicatorGroups(Integer projectId, final IndicatorDTO indicatorDTO) {
		final ListStore<IndicatorGroup> store = view.getIndicatorGroupField().getStore();
		store.removeAll();
		
		dispatch.execute(new GetIndicators(projectId), new CommandResultHandler<IndicatorListResult>() {

			@Override
			protected void onCommandSuccess(IndicatorListResult result) {
				store.add(result.getGroups());
				
				if(indicatorDTO != null && indicatorDTO.getGroupId() != null) {
					final Integer groupId = indicatorDTO.getGroupId();
					for(final IndicatorGroup indicatorGroup : result.getGroups()) {
						if(groupId.equals(indicatorGroup.getId())) {
							view.getIndicatorGroupField().setValue(indicatorGroup);
							return;
						}
					}
				}
			}
		});
	}
	
	private IndicatorDTO getIndicator() {
		final IndicatorDTO indicatorDTO = new IndicatorDTO();
		
		indicatorDTO.setId(indicatorId);
		indicatorDTO.setDatabaseId(projectId);
		indicatorDTO.setCategory(category);
		indicatorDTO.setCode(view.getCodeField().getValue());
		indicatorDTO.setName(view.getNameField().getValue());
		indicatorDTO.setGroupId(view.getIndicatorGroupField().getValue() != null ? view.getIndicatorGroupField().getValue().getId() : null);
		indicatorDTO.setAggregation(getAggregationValue());
		indicatorDTO.setLabels((List<String>) view.getLabelsField().getValue());
		indicatorDTO.setUnits(view.getUnitsField().getValue());
		indicatorDTO.setObjective(view.getObjectiveField().getValue() != null ? view.getObjectiveField().getValue().doubleValue() : null);
		indicatorDTO.setSourceOfVerification(view.getVerificationField().getValue());
		indicatorDTO.setDescription(view.getDescriptionField().getValue());
		indicatorDTO.set(IndicatorDTO.DATA_SOURCE_IDS, view.getDatasourceField().getValue());
		indicatorDTO.setDirectDataEntryEnabled(view.getDatasourceField().getDirectBox().getValue() != null ? view.getDatasourceField().getDirectBox().getValue() : false);
		
		return indicatorDTO;
	}
	
	private int getAggregationValue() {
		if(view.getTypeField().getValue() == view.getTypeQualitativeTypeRadio()) {
			return IndicatorDTO.AGGREGATE_MULTINOMIAL;
			
		} else if(view.getAggregationField().getValue() == view.getAggregationAverageTypeRadio()) {
			return IndicatorDTO.AGGREGATE_AVG;
			
		} else {
			return IndicatorDTO.AGGREGATE_SUM;
		}
	}
	
	private void updateFormLayout() {
		final boolean qualitative = view.getTypeField().getValue() ==  view.getTypeQualitativeTypeRadio();
		view.getLabelsField().setVisible(qualitative);
		view.getUnitsField().setVisible(!qualitative);
		view.getAggregationField().setVisible(!qualitative);
		view.getObjectiveField().setVisible(!qualitative);
	}
	
	private void loadPivot() {
		view.getPivotGridPanel().clear();
		
		dispatch.execute(new GetProject(projectId), new CommandResultHandler<ProjectDTO>() {

			@Override
			protected void onCommandSuccess(ProjectDTO project) {
				LayoutComposer composer = new LayoutComposer(new GWTDates(), project);
				final PivotTableElement pivot = composer.fixIndicator(indicatorId);

				dispatch.execute(new GenerateElement<PivotContent>(pivot), 
						new CommandResultHandler<PivotContent>() {

					@Override
					protected void onCommandSuccess(PivotContent content) {
						pivot.setContent(content);
						view.getPivotGridPanel().setValue(pivot);
					}
				}, view.getPivotGridPanel());
			}
		}, view.getPivotGridPanel());
	}
}
