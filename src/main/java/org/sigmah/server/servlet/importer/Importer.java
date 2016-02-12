package org.sigmah.server.servlet.importer;

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


import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;

import org.sigmah.server.servlet.exporter.data.GlobalExportDataProvider;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.util.ValueResultUtils;
import org.sigmah.shared.dto.referential.AmendmentState;
import org.sigmah.server.domain.User;
import org.sigmah.shared.dto.referential.ElementExtractedValueStatus;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;

import com.google.inject.Injector;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.domain.Country;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.OrgUnitModel;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.server.domain.base.EntityId;
import org.sigmah.server.domain.element.CheckboxElement;
import org.sigmah.server.domain.element.DefaultFlexibleElement;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.element.QuestionChoiceElement;
import org.sigmah.server.domain.element.QuestionElement;
import org.sigmah.server.domain.element.TextAreaElement;
import org.sigmah.server.domain.element.TripletsListElement;
import org.sigmah.server.domain.importation.ImportationSchemeModel;
import org.sigmah.server.domain.importation.Variable;
import org.sigmah.server.domain.importation.VariableBudgetElement;
import org.sigmah.server.domain.importation.VariableBudgetSubField;
import org.sigmah.server.domain.importation.VariableFlexibleElement;
import org.sigmah.server.i18n.I18nServer;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.server.servlet.exporter.utils.ExporterUtil;
import org.sigmah.shared.Language;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dispatch.FunctionalException;
import org.sigmah.shared.dto.ElementExtractedValue;
import org.sigmah.shared.dto.ImportDetails;
import org.sigmah.shared.dto.base.EntityDTO;
import org.sigmah.shared.dto.element.BudgetElementDTO;
import org.sigmah.shared.dto.element.CheckboxElementDTO;
import org.sigmah.shared.dto.element.ComputationElementDTO;
import org.sigmah.shared.dto.element.CoreVersionElementDTO;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.element.FilesListElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.IndicatorsListElementDTO;
import org.sigmah.shared.dto.element.MessageElementDTO;
import org.sigmah.shared.dto.element.QuestionElementDTO;
import org.sigmah.shared.dto.element.ReportElementDTO;
import org.sigmah.shared.dto.element.ReportListElementDTO;
import org.sigmah.shared.dto.element.TextAreaElementDTO;
import org.sigmah.shared.dto.element.TripletsListElementDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.referential.DefaultFlexibleElementType;
import org.sigmah.shared.dto.referential.ImportStatusCode;
import org.sigmah.shared.dto.referential.ImportationSchemeFileFormat;
import org.sigmah.shared.dto.referential.ImportationSchemeImportType;
import org.sigmah.shared.dto.referential.LogicalElementType;
import org.sigmah.shared.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for importing datas from spreadsheet and CSV documents
 * 
 * @author Guerline Jean-Baptiste (gjbaptiste@ideia.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr) v2.0
 */
public abstract class Importer implements Iterator<ImportDetails> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Importer.class);

	private UserDispatch.UserExecutionContext executionContext;
	private Injector injector;
	protected ImportationSchemeDTO scheme;
	
	private Iterator<ImportationSchemeModel> schemeModels;
	private ImportationSchemeModel schemeModel;
	
	private Mapper mapper;
	private Language language;
	private I18nServer translator;
	
	/**
	 * Sets the stream that will be parsed by this importer.
	 * 
	 * @param inputStream 
	 *          Stream to parse.
	 * @throws java.io.IOException
	 *          If the file format is invalid or if an error occurs while reading from the stream.
	 */
	public abstract void setInputStream(InputStream inputStream) throws IOException;
	
	/**
	 * Get the String value of the corresponding variable reference from the
	 * imported file.
	 * 
	 * @param reference
	 *          Location where to find the variable value (column number, cell reference, etc.)
	 * @param lineNumber
	 *          Line number.
	 * @param sheetName
	 *          Name of the sheet.
	 * @return The value at the requested location.
	 * @throws org.sigmah.shared.dispatch.FunctionalException
	 */
	public abstract Object getValueFromVariable(String reference, Integer lineNumber, String sheetName) throws FunctionalException;
	
	/**
	 * Prepare the importer. Must be called before trying to parse an input 
	 * stream.
	 * <p>
	 * The attributes <code>schemeModelList</code>, 
	 * <code>entitiesToImport</code>, <code>language</code>, <code>mapper</code>
	 * and <code>translator</code> will be initialized after this method.
	 * <p>
	 * The attributes <code>injector</code>, <code>executionContext</code> and 
	 * <code>executionContext</code> must be initialized before calling this 
	 * method.
	 * 
	 * @throws CommandException
	 *          If an error occurs while retrieving the scheme models.
	 * @throws IllegalStateException 
	 *          If one of <code>injector</code>, <code>executionContext</code>, 
	 *          <code>executionContext</code> is <code>null</code>.
	 */
	public void initialize() throws CommandException, IllegalStateException {
		
		if (injector == null || executionContext == null || scheme == null) {
			throw new IllegalStateException("injector, executionContext and scheme must be set before calling initialize.");
		}
		
		this.language = executionContext.getLanguage();
		this.mapper = injector.getInstance(Mapper.class);
		this.translator = injector.getInstance(I18nServer.class);
		this.schemeModels = em().createQuery("SELECT ism FROM ImportationSchemeModel AS ism WHERE ism.importationScheme.id = :schemeId", ImportationSchemeModel.class)
				.setParameter("schemeId", scheme.getId())
				.getResultList()
				.iterator();
	}
	
	/**
	 * Retrieves the detail of everything that can be imported.
	 * 
	 * @return the entities to import.
	 * @throws CommandException
	 *          If an error occurs during the analysis.
	 */
	public List<ImportDetails> getCorrespondances() throws CommandException {
		
		final ArrayList<ImportDetails> correspondances = new ArrayList<>();
		
		while (hasNext()) {
			final ImportDetails importDetails = next();
			if (importDetails != null) {
				correspondances.add(importDetails);
			}
		}
		
		return correspondances;
	}

	/**
	 * Sets the instance of the Guice injecto.
	 * 
	 * @param injector
	 *          Guice injector.
	 */
	public void setInjector(Injector injector) {
		this.injector = injector;
	}

	/**
	 * Retrieves the current execution context.
	 * 
	 * @return the current execution context.
	 */
	UserDispatch.UserExecutionContext getExecutionContext() {
		return executionContext;
	}
	
	/**
	 * Sets the current execution context.
	 * 
	 * @param executionContext 
	 *          Execution context.
	 */
	public void setExecutionContext(UserDispatch.UserExecutionContext executionContext) {
		this.executionContext = executionContext;
	}

	/**
	 * Sets the importation scheme to use.
	 * 
	 * @param scheme 
	 *          Importation scheme.
	 */
	public void setScheme(ImportationSchemeDTO scheme) {
		this.scheme = scheme;
	}

	/**
	 * Retrieves the shared instance of translator.
	 * 
	 * @return the shared instance of translator.
	 */
	I18nServer getTranslator() {
		return translator;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException("Not supported.");
	}
	
	protected boolean hasNextSchemeModel() {
		return schemeModels.hasNext();
	}
	
	protected ImportationSchemeModel nextSchemeModel() {
		this.schemeModel = schemeModels.next();
		return schemeModel;
	}
	
	protected ImportationSchemeModel getSchemeModel() {
		return schemeModel;
	}
	
	/**
	 * Get the map mapping a variable value and a flexible element value.
	 * 
	 * @param variableFlexibleElementsDTO
	 * @param lineNumber
	 *            number of the line
	 * @param sheetName
	 *            name of the sheet
	 * @return Map linking a flexible element to the value extracted from the
	 *         document
	 * @throws Throwable
	 */
	private List<ElementExtractedValue> getCorrespondancesVariableFlexibleElement(
	                List<VariableFlexibleElement> variableFlexibleElements, EntityId<Integer> entity,
	                Integer lineNumber, String sheetName) {
		final List<ElementExtractedValue> correspondances = new ArrayList<ElementExtractedValue>();
		
		for (VariableFlexibleElement variableFlexibleElement : variableFlexibleElements) {
			// FEATURE #789: Removed the identification key from the importation results.
			if (entity != null && variableFlexibleElement.getIsKey() != null && variableFlexibleElement.getIsKey()) {
				continue;
			}
			try {
				ElementExtractedValue elementExtractedValue = new ElementExtractedValue();
				Object cellValue = null;
				if (variableFlexibleElement instanceof VariableBudgetElement) {
					// Handling budget elements.
					final VariableBudgetElement variableBudgetElement = (VariableBudgetElement) variableFlexibleElement;

					for (VariableBudgetSubField variableBudgetSubField : variableBudgetElement.getVariableBudgetSubFields()) {
						cellValue = getValueFromVariable(variableBudgetSubField.getVariable().getReference(), lineNumber, sheetName);
						Object[] valueStatus = getValueFormatForFlexibleElement(variableFlexibleElement.getFlexibleElement(),
										cellValue);
						if(valueStatus[0] != null) {
							elementExtractedValue.getNewBudgetValues().put(variableBudgetSubField.getBudgetSubField().getId(),
											(Serializable) valueStatus[0]);
						}
						if(valueStatus[1] != null) {
							elementExtractedValue.setStatus((ElementExtractedValueStatus) valueStatus[1]);
						}
						if (entity != null) {
							elementExtractedValue.setOldBudgetValues(getBudgetElementValue(variableFlexibleElement.getFlexibleElement(),
											entity));
						}
					}
				} else {
					// Handling others elements.
					cellValue = getValueFromVariable(variableFlexibleElement.getVariable().getReference(), lineNumber, sheetName);
					Object[] valueStatus = getValueFormatForFlexibleElement(variableFlexibleElement.getFlexibleElement(),
									cellValue);
					if(valueStatus[0] != null) {
						elementExtractedValue.setNewValue((Serializable) valueStatus[0]);
					}
					if(valueStatus[1] != null) {
						elementExtractedValue.setStatus((ElementExtractedValueStatus) valueStatus[1]);
					}
				}
				
				final FlexibleElementDTO elementDTO = toDTO(variableFlexibleElement.getFlexibleElement());
				elementExtractedValue.setElement(elementDTO);
				if (entity != null) {
					elementExtractedValue.setOldValue(getFlexibleElementValue(variableFlexibleElement.getFlexibleElement(), entity));
				}
				correspondances.add(elementExtractedValue);
			} catch (FunctionalException e) {
				LOGGER.trace("An exception occured while retrieveing the value of the variable '" + variableFlexibleElement.getVariable().getName() + "' for the container '" + entity + "'", e);
			}
		}
		return correspondances;
	}
	
	/**
	 * Gets the alphabetic position of the provided character (-1)
	 * 
	 * @param letter
	 * @return
	 */
	public Integer getNumericValuefromCharacter(Character letter) {

		if (letter >= 'A' && letter <= 'Z')
			return ((int) letter - 'A');
		if (letter >= 'a' && letter <= 'z')
			return ((int) letter - 'a');
		return null;
	}

	/**
	 * Get the value of the flexible Element.
	 * 
	 * @param flexibleElement
	 * @param entityDTO
	 * @param forkey
	 * @return
	 * @throws CommandException
	 */
	private Serializable getFlexibleElementValue(FlexibleElement flexibleElement, EntityId<Integer> entity) {
		
		final GetValue command = new GetValue(entity.getId(), flexibleElement.getId(), "element." + flexibleElement.getClass().getSimpleName(), null);
		final ValueResult valueResult;
		
		try {
			valueResult = executionContext.execute(command);
		} catch (CommandException e) {
			LOGGER.error("An error occured when trying to find the value of flexible element " + flexibleElement + " for the container " + entity, e);
			return null;
		}

		GlobalExportDataProvider gdp = new GlobalExportDataProvider(injector);

		final FlexibleElement element;
		final Serializable valueObject;
		
		final LogicalElementType type = flexibleElement.type();
		
		switch (type.toElementTypeEnum()) {
		case CHECKBOX:
			element = mapper.map(flexibleElement, new CheckboxElement());
			valueObject = getCheckboxValue(valueResult, element);
			break;
		case DEFAULT:
			element = mapper.map(flexibleElement, new DefaultFlexibleElement());
			if (!DefaultFlexibleElementType.BUDGET.equals(((DefaultFlexibleElement) element).type())) {
				valueObject = (Serializable) gdp.getDefElementPair(valueResult, element, entity, entity.getClass(), em(),
				                translator, language).getValue();
			} else {
				valueObject = null;
			}
			break;
		case QUESTION:
			element = mapper.map(flexibleElement, new QuestionElement());
			valueObject = (Serializable) gdp.getChoicePair(element, valueResult).getValue();
			break;
		case TEXT_AREA:
			element = mapper.map(flexibleElement, new TextAreaElement());
			valueObject = (Serializable) gdp.getTextAreaElementPair(valueResult, element).getValue();
			break;
		case TRIPLETS:
			element = mapper.map(flexibleElement, new TripletsListElement());
			valueObject = (Serializable) gdp.getTripletPair(element, valueResult).getValue();
			break;
		default:
			valueObject = null;
			break;
		}
		return valueObject;
	}

	private Map<Integer, String> getBudgetElementValue(FlexibleElement flexibleElement, EntityId<Integer> entity) {
		
		try {
			final String value = em().createQuery("SELECT v.value FROM Value AS v WHERE v.containerId = :containerId AND v.element = :element", String.class)
					.setParameter("containerId", entity.getId())
					.setParameter("element", flexibleElement)
					.getSingleResult();

			return ValueResultUtils.splitMapElements(value);
		} catch (NoResultException e) {
			LOGGER.trace("No value for the element " + flexibleElement + " of entity " + entity.getId(), e);
			return java.util.Collections.<Integer, String>emptyMap();
		}
	}

	/**
	 * Build the resultMap for the importationSchemeModel provided
	 * 
	 * @param schemeModelDTO
	 * @param lineNumber
	 *            number of line
	 * @param sheetName
	 *            name of the sheet
	 * @throws CommandException
	 */
	public ImportDetails getCorrespondancePerSheetOrLine(Integer lineNumber,
	                String sheetName) {
		
		final VariableFlexibleElement keyVariableElement = getKeyOfCurrentSchemeModel();
		if (keyVariableElement == null) {
			return null;
		}

		final ImportDetails importEntity = new ImportDetails();

		final Variable variable = keyVariableElement.getVariable();
		final FlexibleElement flexibleElement = keyVariableElement.getFlexibleElement();

		final String keyValue;
		try {
			final Object cellValue = getValueFromVariable(variable.getReference(), lineNumber, sheetName);
			if (cellValue == null) {
				return null;
			}
			keyValue = cellValue.toString();
		} catch (FunctionalException e) {
			LOGGER.error("An error occured while retrieving the value of the key.", e);
			return null;
		}
		
		LOGGER.debug("Key identification is " + keyValue);
		final String label = ExporterUtil.getFlexibleElementLabel(flexibleElement, translator, language);

		final Map<EntityDTO<Integer>, List<ElementExtractedValue>> entityCorrespondances = new HashMap<>();

		// Checks if the model is an orgUnit or a project model
		if (schemeModel.getOrgUnitModel() != null) {

			final OrgUnitModel orgUnitModel = schemeModel.getOrgUnitModel();

			LOGGER.debug("Import for org unit model : " + orgUnitModel.getName());

			importEntity.setModelName(orgUnitModel.getName());
			importEntity.setModelStatus(orgUnitModel.getStatus());

			// Get all the orgUnits from an orgUnit model
			final List<OrgUnit> orgUnits = em().createQuery("SELECT ou FROM OrgUnit as ou WHERE ou.orgUnitModel = :orgUnitModel", OrgUnit.class)
					.setParameter("orgUnitModel", orgUnitModel)
					.getResultList();
			
			// For each project get the value of the corresponding
			// identification key
			for (OrgUnit orgUnit : orgUnits) {
				final String valueString = (String) getFlexibleElementValue(flexibleElement, orgUnit);
				
				if (valueString != null && valueString.equals(keyValue)) {
				
					final List<ElementExtractedValue> correspondances = getCorrespondancesVariableFlexibleElement(
									schemeModel.getVariableFlexibleElements(), orgUnit, lineNumber,
									sheetName);

					final OrgUnitDTO orgUnitDTO = mapper.map(orgUnit, new OrgUnitDTO());
					entityCorrespondances.put(orgUnitDTO, correspondances);
				}
			}

			// Initializes the importEntity according to the number of
			// orgUnits found
			importEntity.setKeyIdentification(label + " : " + keyValue);
			if (entityCorrespondances.isEmpty()) {
				importEntity.setEntityStatus(ImportStatusCode.ORGUNIT_NOT_FOUND_CODE);
				List<ElementExtractedValue> correspondances = getCorrespondancesVariableFlexibleElement(
								schemeModel.getVariableFlexibleElements(), null, lineNumber, sheetName);
				OrgUnitDTO o = new OrgUnitDTO();
				o.setId(0);
				entityCorrespondances.put(o, correspondances);
			} else if (entityCorrespondances.size() == 1) {
				importEntity.setEntityStatus(ImportStatusCode.ORGUNIT_FOUND_CODE);
			} else {
				importEntity.setEntityStatus(ImportStatusCode.SEVERAL_ORGUNITS_FOUND_CODE);
			}
			importEntity.setEntitiesToImport(entityCorrespondances);

		} else if (schemeModel.getProjectModel() != null) {
			final Map<EntityDTO<Integer>, List<ElementExtractedValue>> lockedEntityCorrespondances = new HashMap<EntityDTO<Integer>, List<ElementExtractedValue>>();

			final ProjectModel projectModel = schemeModel.getProjectModel();

			LOGGER.debug("Import for project model : " + projectModel.getName());

			importEntity.setModelName(projectModel.getName());
			importEntity.setModelStatus(projectModel.getStatus());

			// Get all the projects of a project model
			final List<Project> projects = em().createQuery("SELECT p FROM Project as p WHERE p.projectModel = :projectModel", Project.class)
					.setParameter("projectModel", projectModel)
					.getResultList();
			
			// For each project get the value of the corresponding
			// identification key
			for (Project project : projects) {
				final String valueString = (String) getFlexibleElementValue(flexibleElement, project);
				
				if (valueString != null && valueString.equals(keyValue)) {
				
					final List<ElementExtractedValue> correspondances = getCorrespondancesVariableFlexibleElement(
							schemeModel.getVariableFlexibleElements(), project, lineNumber,
							sheetName);
				
					final ProjectDTO projectDTO = mapper.map(project, new ProjectDTO());

					if (project.getAmendmentState() != null && project.getAmendmentState() == AmendmentState.LOCKED) {
						if (lockedEntityCorrespondances.isEmpty()) {
							lockedEntityCorrespondances.put(projectDTO, correspondances);
						}
					} else {
						entityCorrespondances.put(projectDTO, correspondances);
					}
				}
			}

			// Initializes the importEntity according to the number of
			// projects found
			importEntity.setKeyIdentification(label + " : " + keyValue);
			if (entityCorrespondances.isEmpty()) {
				if (!lockedEntityCorrespondances.isEmpty()) {
					importEntity.setEntityStatus(ImportStatusCode.PROJECT_LOCKED_CODE);
					importEntity.setEntitiesToImport(lockedEntityCorrespondances);

				} else {
					importEntity.setEntityStatus(ImportStatusCode.PROJECT_NOT_FOUND_CODE);
					List<ElementExtractedValue> correspondances = getCorrespondancesVariableFlexibleElement(
									schemeModel.getVariableFlexibleElements(), null, lineNumber, sheetName);
					ProjectDTO p = new ProjectDTO();
					p.setId(0);
					entityCorrespondances.put(p, correspondances);
					importEntity.setEntitiesToImport(entityCorrespondances);
				}

			} else if (entityCorrespondances.size() == 1) {
				importEntity.setEntityStatus(ImportStatusCode.PROJECT_FOUND_CODE);
				importEntity.setEntitiesToImport(entityCorrespondances);

			} else {
				importEntity.setEntityStatus(ImportStatusCode.SEVERAL_PROJECTS_FOUND_CODE);
				importEntity.setEntitiesToImport(entityCorrespondances);
			}
		}

		return importEntity;
	}

	/**
	 * Gets the right format of the value for the flexible element provided
	 * 
	 * @param flexibleElement
	 * @param value
	 * @return
	 */
	public Object[] getValueFormatForFlexibleElement(FlexibleElement flexibleElement, Object value) {
		Object[] valueStatus = new Object[2];
		Serializable formattedValue = null;
		ElementExtractedValueStatus statusCode = null;
		String stringValue = String.valueOf(value);
		if (value != null) {
			final LogicalElementType type = flexibleElement.type();
			
			switch (type.toElementTypeEnum()) {
			case CHECKBOX:
				if (value instanceof Boolean) {
					formattedValue = (Serializable) value;
					statusCode = ElementExtractedValueStatus.VALID_VALUE;
				} else if (value instanceof String) {
					final String noValue = translator.t(language, "no");
					final String yesValue = translator.t(language, "yes");
					
					if ("true".equalsIgnoreCase(stringValue) || "false".equalsIgnoreCase(stringValue)) {
						formattedValue = Boolean.valueOf(stringValue);
						statusCode = ElementExtractedValueStatus.VALID_VALUE;
					} else if (noValue.equalsIgnoreCase(stringValue)) {
						formattedValue = false;
						statusCode = ElementExtractedValueStatus.VALID_VALUE;
					} else if (yesValue.equalsIgnoreCase(stringValue)) {
						formattedValue = true;
						statusCode = ElementExtractedValueStatus.VALID_VALUE;
					}
				}
				break;
				
			case DEFAULT:
				if (!stringValue.isEmpty()) {
					switch (type.toDefaultFlexibleElementType()) {
						case START_DATE:
						case END_DATE:
						if (value instanceof Number) {
								final Long time = Double.valueOf(stringValue).longValue();
							formattedValue = new Date(time);
							statusCode = ElementExtractedValueStatus.VALID_VALUE;
						} else if (value instanceof Date) {
							formattedValue = (Date) value;
							statusCode = ElementExtractedValueStatus.VALID_VALUE;
						}else if (value instanceof String) {
								final SimpleDateFormat defaultFormat = new SimpleDateFormat("dd/MM/yy");
							try {
								formattedValue = defaultFormat.parse(stringValue);
								statusCode = ElementExtractedValueStatus.VALID_VALUE;
							} catch (ParseException e) {
								statusCode = ElementExtractedValueStatus.INVALID_DATE_VALUE;
							}
						}
							break;
							
						case BUDGET:
						if(value instanceof String) {
							try{
								formattedValue = Double.valueOf(stringValue);
								statusCode = ElementExtractedValueStatus.VALID_VALUE;
							} catch(NumberFormatException nfe) {
								statusCode = ElementExtractedValueStatus.INVALID_NUMBER_VALUE;
							}
						} else if (value instanceof Number) {
							formattedValue = ((Number)value).doubleValue();
							statusCode = ElementExtractedValueStatus.VALID_VALUE;
						} else {
							statusCode = ElementExtractedValueStatus.INVALID_NUMBER_VALUE;
						}
							break;
							
						case ORG_UNIT:
							if (value instanceof String) {
								try {
									final int orgUnitId = Integer.parseInt(stringValue);
									value = orgUnitId;
								} catch( NumberFormatException e) {
									// Ignored.
								}
								
								// Searching by code and by name.
								final TypedQuery<OrgUnit> query = em().createQuery("SELECT o FROM OrgUnit o WHERE LOWER(o.name) = :value OR LOWER(o.fullName) = :value", OrgUnit.class);
								query.setParameter("value", stringValue.toLowerCase());
								
								final List<OrgUnit> results = query.getResultList();

								if (!results.isEmpty()) {
									// Selecting the first result.
									formattedValue = results.get(0).getId();
									statusCode = ElementExtractedValueStatus.VALID_VALUE;
								}
							}
							if (value instanceof Integer) {
								// Searching by ID.
								final int orgUnitId = (Integer) value;
								final OrgUnit orgUnit = em().getReference(OrgUnit.class, orgUnitId);
								if (orgUnit != null) {
									statusCode = ElementExtractedValueStatus.VALID_VALUE;
									formattedValue = orgUnitId;
								}
							}
							if (formattedValue == null) {
								statusCode = ElementExtractedValueStatus.INVALID_NUMBER_VALUE;
							}
							break;
							
						case COUNTRY:
							// TODO: Needs to prevent import of countries for projects.
							if (value instanceof String) {
								try {
									final int countryId = Integer.parseInt(stringValue);
									value = countryId;
								} catch( NumberFormatException e) {
									// Ignored.
								}
								
								// Searching by code ISO and by name.
								final TypedQuery<Country> query = em().createQuery("SELECT c FROM Country c WHERE LOWER(c.codeISO) = :value OR LOWER(c.name) = :value", Country.class);
								query.setParameter("value", stringValue.toLowerCase());
								
								final List<Country> results = query.getResultList();

								if (!results.isEmpty()) {
									// Selecting the first result.
									formattedValue = results.get(0).getId();
									statusCode = ElementExtractedValueStatus.VALID_VALUE;
								}
							}
							if (value instanceof Integer) {
								// Searching by ID.
								final int countryId = (Integer) value;
								final Country country = em().getReference(Country.class, countryId);
								if (country != null) {
									statusCode = ElementExtractedValueStatus.VALID_VALUE;
									formattedValue = countryId;
								}
							}
							if (formattedValue == null) {
								statusCode = ElementExtractedValueStatus.INVALID_NUMBER_VALUE;
							}
							break;
							
						case MANAGER:
						case OWNER:
							if (value instanceof String) {
								try {
									final int userId = Integer.parseInt(stringValue);
									value = userId;
								} catch( NumberFormatException e) {
									// Ignored.
								}
								
								// Searching by e-mail address and by last name.
								final TypedQuery<User> query = em().createQuery("SELECT o FROM User o WHERE LOWER(o.email) = :value OR LOWER(o.name) = :value", User.class);
								query.setParameter("value", stringValue.toLowerCase());
								
								final List<User> results = query.getResultList();

								if (!results.isEmpty()) {
									// Selecting the first result.
									formattedValue = results.get(0).getId();
									statusCode = ElementExtractedValueStatus.VALID_VALUE;
								}
							}
							if (value instanceof Integer) {
								// Searching by ID.
								final int userId = (Integer) value;
								final User user = em().getReference(User.class, userId);
								if (user != null) {
									statusCode = ElementExtractedValueStatus.VALID_VALUE;
									formattedValue = userId;
								}
							}
							if (formattedValue == null) {
								statusCode = ElementExtractedValueStatus.INVALID_NUMBER_VALUE;
							}
							break;
							
						default:
						formattedValue = stringValue;
						statusCode = ElementExtractedValueStatus.VALID_VALUE;
							break;
					}
				}
				break;
				
			case QUESTION:
				// Accepted formats:
				// Multiple : label(-label)+
				// Single : label
				QuestionElement questionElement = (QuestionElement) flexibleElement;
				if (questionElement.getMultiple() != null && questionElement.getMultiple()) {
					String[] extractedQuestionValues = stringValue.split("-");
					List<QuestionChoiceElement> choices = new ArrayList<QuestionChoiceElement>();
					for (QuestionChoiceElement choice : questionElement.getChoices()) {
						final String choiceLabel;
						if (choice.getCategoryElement() != null) {
							choiceLabel = choice.getCategoryElement().getLabel();
						} else {
							choiceLabel = choice.getLabel();
						}
						for (String questionValue : extractedQuestionValues) {
							if (choiceLabel.trim().equals(questionValue.trim())) {
								choices.add(choice);
							}
						}
					}
					if (!choices.isEmpty()) {
						formattedValue = Collections.join(choices, new Collections.Mapper<QuestionChoiceElement, String>() {
							@Override
							public String forEntry(QuestionChoiceElement entry) {
								return entry.getId().toString();
							}
						}, ValueResultUtils.DEFAULT_VALUE_SEPARATOR);
						statusCode = ElementExtractedValueStatus.VALID_VALUE;
					} else {
						statusCode = ElementExtractedValueStatus.INVALID_QUESTION_VALUE;
					}
					
				} else {
					for (QuestionChoiceElement choice : questionElement.getChoices()) {
						final String choiceLabel;
						if (choice.getCategoryElement() != null) {
							choiceLabel = choice.getCategoryElement().getLabel();
						} else {
							choiceLabel = choice.getLabel();
						}
						
						if (choiceLabel.equals(stringValue)) {
							formattedValue = choice.getId();
							statusCode = ElementExtractedValueStatus.VALID_VALUE;
							break;
						}

					}
				}
				break;
			case TEXT_AREA:
				// Accepted formats:
				// Type DATE -> dd/MM/yyyy
				// Type NUMBER -> 0
				// Type NUMBER + decimal -> 0.00
				// Type PARAGRAPH -> *
				// Type TEXT -> *
				TextAreaElement textAreaElement = (TextAreaElement) flexibleElement;
				switch (type.toTextAreaType()) {
				case DATE: {
					if (value instanceof Date) {
						formattedValue = (Date) value;
						statusCode = ElementExtractedValueStatus.VALID_VALUE;
					} else if (value instanceof String) {
						SimpleDateFormat defaultFormat = new SimpleDateFormat("dd/MM/yyyy");
						try {
							formattedValue = defaultFormat.parse(stringValue);
							statusCode = ElementExtractedValueStatus.VALID_VALUE;
						} catch (ParseException e) {
							statusCode = ElementExtractedValueStatus.INVALID_DATE_VALUE;
						}
					}
					
					if( ElementExtractedValueStatus.VALID_VALUE.equals(statusCode)){
						Date dateValue  = (Date) formattedValue;
						Date minValue = textAreaElement.getMinValue() != null ?  new Date(textAreaElement.getMinValue()) : null;
						Date maxValue = textAreaElement.getMaxValue() != null ?  new Date(textAreaElement.getMaxValue()) : null;
						boolean isValueCorrect = !((minValue != null && dateValue.before(minValue)) || (maxValue != null && dateValue.after(minValue)));
						if(!isValueCorrect) {
							statusCode = ElementExtractedValueStatus.FORBIDDEN_VALUE;
						}
					}
				}
					break;
				case NUMBER: {
					if (textAreaElement.getIsDecimal()) {
						if(value instanceof String) {
							try{
								formattedValue = Double.valueOf(stringValue);
								statusCode = ElementExtractedValueStatus.VALID_VALUE;
							} catch(NumberFormatException nfe) {
								statusCode = ElementExtractedValueStatus.INVALID_NUMBER_VALUE;
							}
						} else if (value instanceof Number) {
							formattedValue = ((Number)value).doubleValue();
							statusCode = ElementExtractedValueStatus.VALID_VALUE;
						} else {
							statusCode = ElementExtractedValueStatus.INVALID_NUMBER_VALUE;
						}
						
						if( ElementExtractedValueStatus.VALID_VALUE.equals(statusCode)){
							Double numberValue  = (Double) formattedValue;
							Long minValue = textAreaElement.getMinValue() != null ?  textAreaElement.getMinValue() : null;
							Long maxValue = textAreaElement.getMaxValue() != null ?  textAreaElement.getMaxValue() : null;
							boolean isValueCorrect = !((minValue != null && numberValue < minValue) || (maxValue != null && numberValue > maxValue));
							if(!isValueCorrect) {
								statusCode = ElementExtractedValueStatus.FORBIDDEN_VALUE;
							}
						}
					} else {
						if(value instanceof String) {
							try{
								formattedValue = Long.valueOf(stringValue);
								statusCode = ElementExtractedValueStatus.VALID_VALUE;
							} catch(NumberFormatException nfe) {
								statusCode = ElementExtractedValueStatus.INVALID_NUMBER_VALUE;
							}
						} else if (value instanceof Number) {
							formattedValue = ((Number)value).longValue();
							statusCode = ElementExtractedValueStatus.VALID_VALUE;
						} else {
							statusCode = ElementExtractedValueStatus.INVALID_NUMBER_VALUE;
						}
						
						if( ElementExtractedValueStatus.VALID_VALUE.equals(statusCode)){
							Long numberValue  = (Long) formattedValue;
							Long minValue = textAreaElement.getMinValue() != null ?  textAreaElement.getMinValue() : null;
							Long maxValue = textAreaElement.getMaxValue() != null ?  textAreaElement.getMaxValue() : null;
							boolean isValueCorrect = !((minValue != null && numberValue < minValue) || (maxValue != null && numberValue > maxValue));
							if(!isValueCorrect) {
								statusCode = ElementExtractedValueStatus.FORBIDDEN_VALUE;
							}
						}
					}
				}
					break;
				case PARAGRAPH:
				case TEXT: {
					formattedValue = String.valueOf(value);
					statusCode = ElementExtractedValueStatus.VALID_VALUE;
				}
				default:
					break;
				}
				break;
			case TRIPLETS:
				// Accepted formats:
				// Code-Name-Date
				// IGNORED-Code-Name:Date
				String[] extractedTripletValues = stringValue.split("-");
				if(extractedTripletValues.length == 3){
					String[] namePeriod = extractedTripletValues[2].split(":");
					if(namePeriod.length == 2) {
						String[] arrayTripletValues = new String[3];
						arrayTripletValues[0] = extractedTripletValues[1];
						arrayTripletValues[1] = namePeriod[0];
						arrayTripletValues[2] = namePeriod[1];
						formattedValue = arrayTripletValues;
						statusCode = ElementExtractedValueStatus.VALID_VALUE;
					} else {
						formattedValue = extractedTripletValues;
						statusCode = ElementExtractedValueStatus.VALID_VALUE;
					}
					
				} else {
					statusCode = ElementExtractedValueStatus.INVALID_TRIPLET_VALUE;
				}
				break;
				
			default:
				LOGGER.warn("Unsupported flexible element type: {}",  type);
				break;
			}
		}
		valueStatus[0] = formattedValue;
		valueStatus[1] = statusCode;
		return valueStatus;

	}

	protected void logWarnFormatImportTypeIncoherence() {
		LOGGER.warn("Incoherence in ImporationScheme fileFormat ("
				+ ImportationSchemeFileFormat.getStringValue(scheme.getFileFormat()) 
				+ " and its importType "
				+ ImportationSchemeImportType.getStringValue(scheme.getImportType()));
	}

	/**
	 * Return the boolean version of the checkbox element value
	 * 
	 * @param valueResult
	 * @param element
	 * @return
	 */
	public Boolean getCheckboxValue(final ValueResult valueResult, final FlexibleElement element) {
		Boolean value = false;
		if (valueResult != null && valueResult.getValueObject() != null) {
			if (((String) valueResult.getValueObject()).equalsIgnoreCase("true"))
				value = true;

		}
		return value;
	}

	protected int getColumnFromReference(String reference) {
		int column = 0;
		
		// First, try and see if the user used a letter to reference the column.
		Pattern MY_PATTERN = Pattern.compile("[a-zA-Z]+\\.?");
		Matcher m = MY_PATTERN.matcher(reference);
		if (m.find()) {
			String letters = m.group(0);
			column = 0;
			for (int i = 0; i < letters.length(); i++) {
				column = column * 26 + (getNumericValuefromCharacter(letters.charAt(i)) + 1);
			}
			if(column > 0) {
				column -= 1;
			}
			
		} else {
			// Then see if the user used a number to reference the column.
			try {
				column = Integer.parseInt(reference);
			} catch(NumberFormatException e) {
				// Ignored.
			}
		}

		return column;
	}

	protected int getRowFromReference(String reference) {
		int row = 0;
		Pattern MY_PATTERN = Pattern.compile("[0-9]+\\.?");
		Matcher m = MY_PATTERN.matcher(reference);
		if (m.find()) {
			String numbers = m.group(0);
			row = Integer.valueOf(numbers) - 1;
		}
		return row;
	}
	
	protected EntityManager em() {
		return injector.getProvider(EntityManager.class).get();
	}
	
	// --
	// Privates methods.
	// --
	
	private VariableFlexibleElement getKeyOfCurrentSchemeModel() {
		
		try {
			return em().createQuery("SELECT vfe FROM VariableFlexibleElement AS vfe WHERE vfe.isKey = true AND vfe.importationSchemeModel = :schemeModel", VariableFlexibleElement.class)
					.setParameter("schemeModel", schemeModel)
					.getSingleResult();
		} catch (NoResultException e) {
			LOGGER.trace("No key was found in the scheme model " + schemeModel, e);
			return null;
		}
	}
	
	private FlexibleElementDTO toDTO(FlexibleElement element) {
		
		final FlexibleElementDTO dto;
		
		final LogicalElementType type = element.type();
		
		switch (type.toElementTypeEnum()) {
		case CHECKBOX:
			dto = new CheckboxElementDTO();
			break;
		case COMPUTATION:
			dto = new ComputationElementDTO();
			break;
		case CORE_VERSION:
			dto = new CoreVersionElementDTO();
			break;
		case DEFAULT:
			if (type == DefaultFlexibleElementType.BUDGET) {
				dto = new BudgetElementDTO();
			} else {
				dto = new DefaultFlexibleElementDTO();
			}
			break;
		case FILES_LIST:
			dto = new FilesListElementDTO();
			break;
		case INDICATORS:
			dto = new IndicatorsListElementDTO();
			break;
		case MESSAGE:
			dto = new MessageElementDTO();
			break;
		case QUESTION:
			dto = new QuestionElementDTO();
			break;
		case REPORT:
			dto = new ReportElementDTO();
			break;
		case REPORT_LIST:
			dto = new ReportListElementDTO();
			break;
		case TEXT_AREA:
			dto = new TextAreaElementDTO();
			break;
		case TRIPLETS:
			dto = new TripletsListElementDTO();
			break;
		default:
			throw new UnsupportedOperationException("Flexible element of type '" + type + "' is not supported.");
		}
		
		return mapper.map(element, dto);
	}
	
}
