package org.sigmah.server.handler;

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


import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Amendment;
import org.sigmah.server.domain.HistoryToken;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.element.DefaultFlexibleElement;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.file.FileStorageProvider;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.computation.value.ComputedValue;
import org.sigmah.shared.computation.value.ComputedValues;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.element.BudgetDistributionElementDTO;
import org.sigmah.shared.dto.element.BudgetRatioElementDTO;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.element.FilesListElementDTO;
import org.sigmah.shared.dto.element.IndicatorsListElementDTO;
import org.sigmah.shared.dto.element.MessageElementDTO;
import org.sigmah.shared.dto.element.ReportListElementDTO;
import org.sigmah.shared.dto.element.TripletsListElementDTO;
import org.sigmah.shared.dto.report.ReportReference;
import org.sigmah.shared.dto.value.BudgetPartsListValueDTO;
import org.sigmah.shared.dto.value.FileDTO;
import org.sigmah.shared.dto.value.FileVersionDTO;
import org.sigmah.shared.dto.value.IndicatorsListValueDTO;
import org.sigmah.shared.dto.value.ListableValue;
import org.sigmah.shared.dto.value.TripletValueDTO;
import org.sigmah.shared.util.ValueResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler getting the value of a {@link FlexibleElement}.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class GetValueHandler extends AbstractCommandHandler<GetValue, ValueResult> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(GetValueHandler.class);
	
	/**
	 * Allow access to the files.
	 */
	@Inject
	private FileStorageProvider fileStorageProvider;

	/**
	 * Gets a flexible element value from the database.
	 * 
	 * @param cmd
	 *          {@link GetValue} command containing the flexible element class, its id, and the project id
	 * @return a {@link ValueResult} object containing the value of the flexible element or containing {@code null} if
	 *         there is no value defined for this element.
	 * @throws org.sigmah.shared.dispatch.CommandException
	 */
	@Override
	public ValueResult execute(final GetValue cmd, final UserExecutionContext context) throws CommandException {

		LOG.debug("Getting value object from the database for command: '{}'.", cmd);

		// Command result.
		final ValueResult valueResult = new ValueResult();

		// Amendment
		String historyValue = null;
		if (cmd.getAmendmentId() != null) {

			final TypedQuery<Amendment> amedmentQuery = em().createQuery("SELECT a FROM Amendment a WHERE a.id = :amendmentId", Amendment.class);
			amedmentQuery.setParameter("amendmentId", cmd.getAmendmentId());

			final Amendment amendment = amedmentQuery.getSingleResult();

			final List<HistoryToken> tokens = amendment.getValues();

			if (tokens != null) {
				for (final HistoryToken token : tokens) {
					if (token.getElementId().equals(cmd.getElementId()) && token.getLayoutGroupIterationId() == cmd.getIterationId()) {
						historyValue = token.getValue();
					}
				}
			}
		}

		// --------------------------------------------------------------------
		// STEP 1 : gets the string value (regardless of the element).
		// --------------------------------------------------------------------

		final String valueFromDatabase;
		if (DefaultFlexibleElementDTO.ENTITY_NAME.equals(cmd.getElementEntityName())) {
			valueFromDatabase = findCurrentValueOfDefaultElement(cmd.getProjectId(), cmd.getElementId());
		} else if (BudgetRatioElementDTO.ENTITY_NAME.equals(cmd.getElementEntityName())) {
			valueFromDatabase = findCurrentValueOfBudgetRatioElement(cmd.getProjectId(), cmd.getElementId());
		} else {
			valueFromDatabase = findCurrentValue(cmd.getProjectId(), cmd.getElementId(), cmd.getIterationId());
		}
		
		String valueAsString = valueFromDatabase;
		boolean isValueExisting = valueFromDatabase != null;

		// Overriding the value by the old one if we have to display an amendment
		if (historyValue != null) {
			valueAsString = historyValue;
			isValueExisting = true;

			valueResult.setAmendment(true);
		}

		// No value exists for the flexible element.
		if (!isValueExisting) {
			LOG.debug("No value for this flexible element #{}.", cmd.getElementId());
			return valueResult;
		}

		// --------------------------------------------------------------------
		// STEP 2 : gets the true values (depending of the element).
		// Can be a list of id with requires a sub-select query.
		// --------------------------------------------------------------------

		Query query = null;
		String elementClassName = cmd.getElementEntityName();
		ListableValue dto = null;
		Boolean isList = null;

		// Creates the sub-select query to get the true value.
		if (elementClassName.equals(TripletsListElementDTO.ENTITY_NAME)) {

			LOG.debug("Case TripletsListElementDTO.");

			dto = new TripletValueDTO();
			isList = true;

			query = em().createQuery("SELECT tv FROM TripletValue tv WHERE tv.id IN (:idsList)");
			query.setParameter("idsList", ValueResultUtils.splitValuesAsInteger(valueAsString));

		} else if (elementClassName.equals(IndicatorsListElementDTO.ENTITY_NAME)) {

			LOG.debug("Case IndicatorsListElementDTO.");

			dto = new IndicatorsListValueDTO();
			isList = true;

			query = em().createQuery("SELECT ilv FROM IndicatorsListValue ilv WHERE ilv.id.idList = :value");
			query.setParameter("value", Integer.valueOf(valueAsString));

		} else if (elementClassName.equals(BudgetDistributionElementDTO.ENTITY_NAME)) {

			LOG.debug("Case BudgetDistributionElementDTO.");

			dto = new BudgetPartsListValueDTO();
			isList = true;

			query = em().createQuery("SELECT bplv FROM BudgetPartsListValue bplv WHERE bplv.id = :value");
			query.setParameter("value", Integer.valueOf(valueAsString));

		} else if (elementClassName.equals(FilesListElementDTO.ENTITY_NAME)) {

			LOG.debug("Case FilesListElementDTO.");

			dto = new FileDTO();
			isList = true;

			query = em().createQuery("SELECT f FROM File f WHERE f.id IN (:idsList)");
			query.setParameter("idsList", ValueResultUtils.splitValuesAsInteger(valueAsString));

		} else if (elementClassName.equals(ReportListElementDTO.ENTITY_NAME)) {

			LOG.debug("Case ReportListElementDTO.");

			dto = new ReportReference();
			isList = true;

			query = em().createQuery("SELECT r FROM ProjectReport r WHERE r.id IN (:idList)");
			query.setParameter("idList", ValueResultUtils.splitValuesAsInteger(valueAsString));

		} else if (!(elementClassName.equals(MessageElementDTO.ENTITY_NAME))) {

			LOG.debug("Case others (but MessageElementDTO).");

			dto = null;
			isList = false;

		}

		// --------------------------------------------------------------------
		// STEP 3 : fill the command result with the values.
		// --------------------------------------------------------------------

		// No value for this kind of elements.
		if (isList == null) {
			return valueResult;
		}

		// Multiple results case
		if (isList) {

			LOG.debug("Multiple values for the element #{}.", cmd.getElementId());

			@SuppressWarnings("unchecked")
			final List<Object> objectsList = query.getResultList();

			final List<ListableValue> serializablesList = new ArrayList<>();
			for (Object o : objectsList) {
				serializablesList.add(mapper().map(o, dto.getClass()));
			}
			
			if(elementClassName.equals(FilesListElementDTO.ENTITY_NAME)) {
				for(final ListableValue value : serializablesList) {
					if(value instanceof FileDTO) {
						final FileDTO file = (FileDTO)value;
						for(final FileVersionDTO version : file.getVersions()) {
							version.setAvailable(fileStorageProvider.exists(version.getPath()));
						}
					}
				}
			}

			valueResult.setValuesObject(serializablesList);
		}
		// Single result case
		else {

			LOG.debug("Single value for the element #{}.", cmd.getElementId());

			// A single value is always interpreted as a string.
			valueResult.setValueObject(valueAsString);
		}

		LOG.debug("Returned value = {}.", valueResult);

		return valueResult;
	}
	
	private String findCurrentValue(int projectId, int elementId, Integer iterationId) {
		// Creates the query to get the value for the flexible element (as
		// string) in the Value table.
		final TypedQuery<String> valueQuery;

		if(iterationId == null) {
			valueQuery = em().createQuery("SELECT v.value FROM Value v WHERE v.containerId = :projectId AND v.element.id = :elementId", String.class);
		valueQuery.setParameter("projectId", projectId);
		valueQuery.setParameter("elementId", elementId);
		} else {
			valueQuery = em().createQuery("SELECT v.value FROM Value v WHERE v.element.id = :elementId AND v.layoutGroupIteration.id = :iterationId", String.class);
			valueQuery.setParameter("elementId", elementId);
			valueQuery.setParameter("iterationId", iterationId);
		}

		String valueAsString;
		
		// Executes the query and tests if a value exists for this flexible
		// element.
		try {
			valueAsString = valueQuery.getSingleResult();
			if (StringUtils.isBlank(valueAsString)) {
				valueAsString = null;
			}
		} catch (NoResultException | ClassCastException e) {
			valueAsString = null;
		}
		
		return valueAsString;
	}
	
	private String findCurrentValueOfDefaultElement(int projectId, int elementId) {
		final Project container = em().find(Project.class, projectId);
		final DefaultFlexibleElement element = em().find(DefaultFlexibleElement.class, elementId);
		
		return element.getValue(container);
	}
	
	/**
	 * Find in database the current value of a budget ratio element.
	 * 
	 * @param containerId
	 * @param elementId
	 * @return 
	 */
	private String findCurrentValueOfBudgetRatioElement(int containerId, int elementId) {
		
		// Selecting multiple columns results in an Object[] return type.
		// http://docs.oracle.com/cd/E17904_01/apirefs.1111/e13946/ejb3_langref.html#ejb3_langref_resulttype
		final TypedQuery<Object[]> valueQuery = em().createQuery("SELECT v1.value, v2.value "
				+ "FROM BudgetRatioElement bre, Value v1, Value v2 "
				+ "WHERE bre.id = :elementId "
				+ "AND v1.containerId = :containerId AND v1.element = bre.spentBudget "
				+ "AND v2.containerId = :containerId AND v2.element = bre.plannedBudget", Object[].class);
		
		valueQuery.setParameter("containerId", containerId);
		valueQuery.setParameter("elementId", elementId);
		
		// Executes the query and tests if a value exists for this flexible
		// element.
		try {
			final Object[] values = valueQuery.getSingleResult();
			if (values != null && values.length == 2) {
				final ComputedValue spent = ComputedValues.from((String) values[0]);
				final ComputedValue planned = ComputedValues.from((String) values[1]);
				
				final Double valueAsDouble = planned.divide(spent).get();
				if (valueAsDouble != null) {
					return valueAsDouble.toString();
				}
			}
		} catch (NoResultException e) {
			// Ignored.
		}
		
		return null;
	}
	
}
