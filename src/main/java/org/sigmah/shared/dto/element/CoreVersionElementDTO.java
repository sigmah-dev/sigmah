package org.sigmah.shared.dto.element;

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

import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import java.util.Date;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.event.handler.UpdateHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.dto.AmendmentDTO;
import org.sigmah.shared.dto.PhaseDTO;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.element.event.RequiredValueEvent;
import org.sigmah.shared.dto.element.event.ValueEvent;
import org.sigmah.shared.dto.layout.LayoutConstraintDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class CoreVersionElementDTO extends FlexibleElementDTO {
	
	public static final String ENTITY_NAME = "element.CoreVersionElement";

	@Override
	protected Component getComponent(ValueResult valueResult, boolean enabled) {
		// Create the component.
		final ComboBox<AmendmentDTO> comboBox = createComboBox();
		comboBox.setEnabled(enabled);
		
		// Identify the current value.
		final Integer value;
		if(valueResult.getValueObject() != null && !valueResult.getValueObject().isEmpty()) {
			value = Integer.parseInt(valueResult.getValueObject());
		} else {
			value = null;
		}
		
		// Populate its store.
		if(currentContainerDTO instanceof ProjectDTO) {
			populateStore(comboBox, value);
			
			// Refresh the store if a core version has been updated.
			eventBus.addHandler(UpdateEvent.getType(), new UpdateHandler() {

				@Override
				public void onUpdate(UpdateEvent event) {
					if(event.concern(UpdateEvent.CORE_VERSION_UPDATED)) {
						populateStore(comboBox, null);
					}
				}
			});
		}
		
		return comboBox;
	}
	
	private void populateStore(ComboBox<AmendmentDTO> comboBox, Integer selectedAmendmentId) {
		final ListStore<AmendmentDTO> store = comboBox.getStore();
		store.removeAll();
		
		final ProjectDTO project = (ProjectDTO)currentContainerDTO;
			
		final PhaseDTO phase = getParentPhase(project);
		if(phase != null) {
			final Date startDate = phase.getStartDate();
			final boolean isFirstPhase = phase.getPhaseModel().getRoot() != null && phase.getPhaseModel().getRoot();
			
			for(final AmendmentDTO amendment : project.getAmendments()) {
				// Adds only the amendment created after the parent phase has been activated.
				if(isFirstPhase || (startDate != null && startDate.before(amendment.getDate()))) {
					store.add(amendment);

					// Preselecting the current value.
					if(selectedAmendmentId != null && selectedAmendmentId.equals(amendment.getId())) {
						comboBox.setValue(amendment);
					}
				}
			}
			
			if(store.getCount() == 0) {
				comboBox.setEmptyText(I18N.CONSTANTS.projectCoreNoValidated());
			}
		}
	}
	
	private ComboBox<AmendmentDTO> createComboBox() {
		final ComboBox<AmendmentDTO> comboBox = new ComboBox<AmendmentDTO>();
		comboBox.setStore(new ListStore<AmendmentDTO>());
		comboBox.setTriggerAction(ComboBox.TriggerAction.ALL);
		comboBox.setFieldLabel(getLabel());
		comboBox.setDisplayField(AmendmentDTO.NAME);
		comboBox.setValueField(AmendmentDTO.ID);
		comboBox.setAllowBlank(true);
		
		comboBox.addSelectionChangedListener(new SelectionChangedListener<AmendmentDTO>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<AmendmentDTO> se) {
				final String value;
				
				final AmendmentDTO selectedCoreVersion = se.getSelectedItem();
				if(selectedCoreVersion != null && selectedCoreVersion.getId() != null) {
					value = selectedCoreVersion.getId().toString();
				} else {
					value = "";
				}
				
				handlerManager.fireEvent(new ValueEvent(CoreVersionElementDTO.this, value));
				
				if (getValidates()) {
					handlerManager.fireEvent(new RequiredValueEvent(isCorrectRequiredValue(value)));
				}
			}
		});
		
		return comboBox;
	}
	
	private PhaseDTO getParentPhase(ProjectDTO project) {
		for(final PhaseDTO phase : project.getPhases()) {
			for(final LayoutGroupDTO group : phase.getPhaseModel().getLayout().getGroups()) {
				for(final LayoutConstraintDTO constraint : group.getConstraints()) {
					final FlexibleElementDTO element = constraint.getFlexibleElementDTO();
					if(element != null && element.getId() != null && element.getId().equals(getId())) {
						return phase;
					}
				}
			}
		}
		return null;
	}
	
	@Override
	public boolean isCorrectRequiredValue(ValueResult result) {
		return isCorrectRequiredValue(result.getValueObject());
	}
	
	private boolean isCorrectRequiredValue(String value) {
		return value != null && value.matches("[0-9]+");
	}

	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}
	
}
