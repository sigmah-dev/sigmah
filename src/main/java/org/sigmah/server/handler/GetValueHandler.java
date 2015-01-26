package org.sigmah.server.handler;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Amendment;
import org.sigmah.server.domain.HistoryToken;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.report.ReportReference;
import org.sigmah.shared.dto.value.BudgetPartsListValueDTO;
import org.sigmah.shared.dto.value.FileDTO;
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
 */
public class GetValueHandler extends AbstractCommandHandler<GetValue, ValueResult> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(GetValueHandler.class);

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

			Amendment amendment = amedmentQuery.getSingleResult();

			final List<HistoryToken> tokens = amendment.getValues();

			if (tokens != null) {
				for (final HistoryToken token : tokens) {
					if (token.getElementId().equals(cmd.getElementId())) {
						historyValue = token.getValue();
					}
				}
			}
		}

		// --------------------------------------------------------------------
		// STEP 1 : gets the string value (regardless of the element).
		// --------------------------------------------------------------------

		// Creates the query to get the value for the flexible element (as
		// string) in the Value table.
		final TypedQuery<String> valueQuery =
				em().createQuery("SELECT v.value FROM Value v WHERE v.containerId = :projectId AND v.element.id = :elementId", String.class);
		valueQuery.setParameter("projectId", cmd.getProjectId());
		valueQuery.setParameter("elementId", cmd.getElementId());

		// Executes the query and tests if a value exists for this flexible
		// element.
		boolean isValueExisting = true;
		String valueAsString = null;
		try {
			valueAsString = valueQuery.getSingleResult();
			if (StringUtils.isBlank(valueAsString)) {
				isValueExisting = false;
			}
		} catch (NoResultException | ClassCastException e) {
			isValueExisting = false;
		}

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
		Class<? extends ListableValue> dtoClazz = null;
		boolean isList = false;

		// Creates the sub-select query to get the true value.
		if (elementClassName.equals("element.TripletsListElement")) {

			LOG.debug("Case TripletsListElementDTO.");

			dtoClazz = TripletValueDTO.class;
			isList = true;

			query = em().createQuery("SELECT tv FROM TripletValue tv WHERE tv.id IN (:idsList)");
			query.setParameter("idsList", ValueResultUtils.splitValuesAsInteger(valueAsString));

		} else if (elementClassName.equals("element.IndicatorsListElement")) {

			LOG.debug("Case IndicatorsListElementDTO.");

			dtoClazz = IndicatorsListValueDTO.class;
			isList = true;

			query = em().createQuery("SELECT ilv FROM IndicatorsListValue ilv WHERE ilv.id.idList = :value");
			query.setParameter("value", Integer.valueOf(valueAsString));

		} else if (elementClassName.equals("element.BudgetDistributionElement")) {

			LOG.debug("Case BudgetDistributionElementDTO.");

			dtoClazz = BudgetPartsListValueDTO.class;
			isList = true;

			query = em().createQuery("SELECT bplv FROM BudgetPartsListValue bplv WHERE bplv.id = :value");
			query.setParameter("value", Integer.valueOf(valueAsString));

		} else if (elementClassName.equals("element.FilesListElement")) {

			LOG.debug("Case FilesListElementDTO.");

			dtoClazz = FileDTO.class;
			isList = true;

			query = em().createQuery("SELECT f FROM File f WHERE f.id IN (:idsList)");
			query.setParameter("idsList", ValueResultUtils.splitValuesAsInteger(valueAsString));

		} else if (elementClassName.equals("element.ReportListElement")) {

			LOG.debug("Case ReportListElementDTO.");

			dtoClazz = ReportReference.class;
			isList = true;

			query = em().createQuery("SELECT r FROM ProjectReport r WHERE r.id IN (:idList)");
			query.setParameter("idList", ValueResultUtils.splitValuesAsInteger(valueAsString));

		} else if (!(elementClassName.equals("element.MessageElement"))) {

			LOG.debug("Case others (but MessageElementDTO).");

			dtoClazz = ListableValue.class;
			isList = false;

		}

		// --------------------------------------------------------------------
		// STEP 3 : fill the command result with the values.
		// --------------------------------------------------------------------

		// No value for this kind of elements.
		if (dtoClazz == null) {
			return valueResult;
		}

		// Multiple results case
		if (isList) {

			LOG.debug("Multiple values for the element #{}.", cmd.getElementId());

			@SuppressWarnings("unchecked")
			final List<Object> objectsList = query.getResultList();

			final List<ListableValue> serializablesList = new ArrayList<>();
			for (Object o : objectsList) {
				serializablesList.add(mapper().map(o, dtoClazz));
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
}
