package org.sigmah.client.computation;

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

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.ui.widget.Loadable;
import org.sigmah.client.ui.widget.form.StringField;
import org.sigmah.offline.sync.SuccessCallback;
import org.sigmah.shared.computation.Computation;
import org.sigmah.shared.dto.IsModel;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.element.ComputationElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementContainer;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.event.ValueEvent;
import org.sigmah.shared.dto.element.event.ValueHandler;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

/**
 * Manage computation element triggers.
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.1
 */
public class ComputationTriggerManager {

	@Inject
	private ClientValueResolver valueResolver;

	private final Map<ComputationElementDTO, Computation> computations = new HashMap<ComputationElementDTO, Computation>();
	private final Map<FlexibleElementDTO, List<ComputationElementDTO>> dependencies = new HashMap<FlexibleElementDTO, List<ComputationElementDTO>>();
	private final Map<FlexibleElementDTO, Field<String>> components = new HashMap<FlexibleElementDTO, Field<String>>();
	private final Map<Integer, ComputationElementDTO> elementsWithHandlers = new HashMap<Integer, ComputationElementDTO>();

	private FlexibleElementContainer container;

	/**
	 * Prepare the trigger manager.
	 *
	 * @param project
	 *          Project to display.
	 */
	public void prepareForProject(final ProjectDTO project) {

		this.container = project;

		clearMaps();

		final ProjectModelDTO model = project.getProjectModel();

		for (final ProjectDTO.LocalizedElement<ComputationElementDTO> localizedElement : project.getLocalizedElements(ComputationElementDTO.class)) {
			final ComputationElementDTO computationElement = localizedElement.getElement();
			prepareForComputationElement(computationElement, model);
		}
		
		// TODO: Chercher les ComputationField des projets liés et appeler prepareForComputationElement.
	}

	/**
	 * Prepare the trigger manager.
	 *
	 * @param orgUnit
	 *          OrgUnit to display.
	 */
	public void prepareForOrgUnit(final OrgUnitDTO orgUnit) {

		this.container = orgUnit;

		clearMaps();

		final OrgUnitModelDTO model = orgUnit.getOrgUnitModel();

		for (final OrgUnitDTO.LocalizedElement localizedElement : orgUnit.getLocalizedElements(ComputationElementDTO.class)) {
			final ComputationElementDTO computationElement = (ComputationElementDTO) localizedElement.getElement();
			prepareForComputationElement(computationElement, model);
		}
	}

	/**
	 * Remove the content of every maps.
	 */
	private void clearMaps() {
		this.dependencies.clear();
		this.computations.clear();
		this.components.clear();
		this.elementsWithHandlers.clear();
	}

	/**
	 * List the dependencies of the given element.
	 *
	 * @param computationElement
	 *          Computation element to prepare.
	 * @param model
	 *          Model of the current container.
	 */
	private void prepareForComputationElement(final ComputationElementDTO computationElement, final IsModel model) {
		final Computation computation = computationElement.getComputationForModel(model);
		computations.put(computationElement, computation);

		for (final FlexibleElementDTO dependency : computation.getDependencies()) {
			List<ComputationElementDTO> list = dependencies.get(dependency);

			if (list == null) {
				list = new ArrayList<ComputationElementDTO>();
				dependencies.put(dependency, list);
			}

			list.add(computationElement);
		}
	}

	/**
	 * Add a value change handler to the given element if it is a dependency
	 * of a computation.
	 *
	 * @param element
	 *          Element to listen.
	 * @param component
	 *          Component associated to the given element.
	 * @param modifications
	 *          Value change list.
	 */
	public void listenToValueChangesOfElement(final FlexibleElementDTO element, final Component component, final List<ValueEvent> modifications) {

		if (element instanceof ComputationElementDTO) {
			components.put(element, (Field<String>) component);
			elementsWithHandlers.put(element.getId(), (ComputationElementDTO) element);

			initialUpdateIfCurrentValueIsEmpty((ComputationElementDTO) element, (StringField) component);
		}

		final List<ComputationElementDTO> computationElements = dependencies.get(element);

		if (computationElements != null) {
			element.addValueHandler(new ValueHandler() {

				@Override
				public void onValueChange(ValueEvent event) {
					updateComputations(computationElements, modifications);
				}
			});
		}
	}

	/**
	 * Compute the value of the given element if no value has been provided.
	 *
	 * @param computationElement
	 *          Computation element to update.
	 * @param component
	 *          Component associated to the given element.
	 */
	private void initialUpdateIfCurrentValueIsEmpty(final ComputationElementDTO computationElement, final StringField field) {

		if (field.getValue() == null || field.getValue().isEmpty()) {
			updateComputation(computationElement, new ArrayList<ValueEvent>(), false);
		}
	}

	/**
	 * Update the given computation elements.
	 *
	 * @param computationElements
	 *          List of elements to update.
	 * @param modifications
	 *          Value change list.
	 */
	private void updateComputations(final List<ComputationElementDTO> computationElements, final List<ValueEvent> modifications) {

		if (computationElements == null) {
			return;
		}

		for (final ComputationElementDTO computationElement : computationElements) {
			updateComputation(computationElement, modifications, true);
		}
	}

	/**
	 * Update the given computation element.
	 *
	 * @param computationElement
	 *          Element to update.
	 * @param modifications 
	 *          Value change list.
	 */
	private void updateComputation(final ComputationElementDTO computationElement, final List<ValueEvent> modifications,
			final boolean fireEvents) {

		final Computation computation = computations.get(computationElement);

		final Loadable loadable;

		final Field<String> computationView = components.get(computationElement);
		if (computationView != null) {
			loadable = new LoadingMask(computationView);
		} else {
			loadable = null;
		}

		computation.computeValueWithModificationsAndResolver(container, modifications, valueResolver, new SuccessCallback<String>() {

			@Override
			public void onSuccess(String result) {
				updateComputationElementWithValue(computationElement, result, modifications, fireEvents);
			}
		}, loadable);
	}

	/**
	 * Update the given computation element with the given value.
	 *
	 * @param computationElement
	 *          Computation element to update.
	 * @param value
	 *          Result of the computation.
	 * @param modifications 
	 *          Value change list.
	 */
	private void updateComputationElementWithValue(final ComputationElementDTO computationElement, final String value,
			final List<ValueEvent> modifications, final boolean fireEvents) {

		final Field<String> field = components.get(computationElement);
		if (field != null) {
			field.setValue(value);
			if (fireEvents) {
				fireValueEvent(computationElement, value);
			}
		} else {
			// The affected computation is not displayed.
			// Manually adding the value to the modifications.
			modifications.add(new ValueEvent(computationElement, value));

			// Manually firing the dependencies.
			updateComputations(dependencies.get(computationElement), modifications);
		}
	}

	/**
	 * Fire a value event for the given computation element.
	 *
	 * @param computationElement
	 *          Modified computation field.
	 * @param value 
	 *          New value.
	 */
	private void fireValueEvent(final ComputationElementDTO computationElement, final String value) {
		// Firing a value event to register the change and trigger dependencies update.
		final ComputationElementDTO withHandlers = elementsWithHandlers.get(computationElement.getId());
		if (withHandlers != null) {
			withHandlers.fireValueEvent(value);
		} else {
			computationElement.fireValueEvent(value);
		}
	}

}
