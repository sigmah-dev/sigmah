package org.sigmah.server.servlet.importer;

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
import org.sigmah.shared.command.GetImportationSchemeModels;
import org.sigmah.shared.command.GetOrgUnitsByModel;
import org.sigmah.shared.command.GetProjectsByModel;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.util.ValueResultUtils;
import org.sigmah.shared.dto.referential.AmendmentState;
import org.sigmah.server.domain.User;
import org.sigmah.shared.dto.referential.ElementExtractedValueStatus;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.element.BudgetElementDTO;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.QuestionChoiceElementDTO;
import org.sigmah.shared.dto.element.QuestionElementDTO;
import org.sigmah.shared.dto.element.TextAreaElementDTO;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;
import org.sigmah.shared.dto.importation.ImportationSchemeModelDTO;
import org.sigmah.shared.dto.importation.VariableBudgetElementDTO;
import org.sigmah.shared.dto.importation.VariableBudgetSubFieldDTO;
import org.sigmah.shared.dto.importation.VariableDTO;
import org.sigmah.shared.dto.importation.VariableFlexibleElementDTO;

import com.google.inject.Injector;
import javax.persistence.TypedQuery;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.domain.Country;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.element.CheckboxElement;
import org.sigmah.server.domain.element.DefaultFlexibleElement;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.element.QuestionElement;
import org.sigmah.server.domain.element.TextAreaElement;
import org.sigmah.server.domain.element.TripletsListElement;
import org.sigmah.server.i18n.I18nServer;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.server.servlet.exporter.utils.ExporterUtil;
import org.sigmah.shared.Language;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dispatch.FunctionalException;
import org.sigmah.shared.dto.ElementExtractedValue;
import org.sigmah.shared.dto.ImportDetails;
import org.sigmah.shared.dto.base.EntityDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.referential.DefaultFlexibleElementType;
import org.sigmah.shared.dto.referential.ImportStatusCode;
import org.sigmah.shared.dto.referential.ImportationSchemeFileFormat;
import org.sigmah.shared.dto.referential.ImportationSchemeImportType;
import org.sigmah.shared.dto.referential.TextAreaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for importing datas from spreadsheet and CSV documents
 * 
 * @author Guerline Jean-Baptiste (gjbaptiste@ideia.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr) v2.0
 */
public abstract class Importer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Importer.class);

	private final Injector injector;
	
	/**
	 * The import parameters.
	 */
	protected Map<String, Object> properties;

	protected ImportationSchemeDTO scheme;

	/**
	 * The result map sent back to the client: EntityDTO :
	 * {@link ProjectModelDTO} or {@link OrgUnitModelDTO} String : the key
	 * identification EntityDTO : {@link ProjectDTO} or {@link OrgUnitDTO}
	 * FlexibleElementDTO : The element updated during the importation Object :
	 * the element new value
	 */

	private final List<ImportDetails> entitiesToImport;

	private final Mapper mapper;

	private final Language language;

	private final I18nServer translator;

	private final EntityManager em;


	protected List<ImportationSchemeModelDTO> schemeModelList;
	
	private UserDispatch.UserExecutionContext executionContext;

	public Importer(Injector injector, Map<String, Object> properties, UserDispatch.UserExecutionContext executionContext) throws CommandException {
		this.injector = injector;
		this.mapper = injector.getInstance(Mapper.class);
		this.em = injector.getProvider(EntityManager.class).get();
		this.translator = injector.getInstance(I18nServer.class);
		this.properties = properties;
		this.executionContext = executionContext;
		
		final User user = executionContext.getUser();
		this.language = Language.fromString(user.getLocale());

		entitiesToImport = new ArrayList<ImportDetails>();

		final GetImportationSchemeModels getImportationSchemeModels = new GetImportationSchemeModels();
		scheme = (ImportationSchemeDTO) properties.get("scheme");
		getImportationSchemeModels.setImportationSchemeId(scheme.getId());

		final ListResult<ImportationSchemeModelDTO> result = executionContext.execute(getImportationSchemeModels);

		schemeModelList = result.getList();
	}

	/**
	 * @return the entitiesToImport
	 */
	public List<ImportDetails> getEntitiesToImport() {
		return entitiesToImport;
	}

	/**
	 * Add the correspondances map (flexibleElement , new value) to the result
	 * map
	 * 
	 * @param schemeModelList
	 * @throws CommandException
	 */
	protected abstract void getCorrespondances(List<ImportationSchemeModelDTO> schemeModelList) throws CommandException;

	/**
	 * Get the map mapping a variable value and a flexible element value
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
	                List<VariableFlexibleElementDTO> variableFlexibleElementsDTO, EntityDTO entityDTO,
	                Integer lineNumber, String sheetName) throws CommandException {
		final List<ElementExtractedValue> correspondances = new ArrayList<ElementExtractedValue>();
		
		for (VariableFlexibleElementDTO varfle : variableFlexibleElementsDTO) {
			// FEATURE #789: Removed the identification key from the importation results.
			if (varfle.getIsKey() != null && varfle.getIsKey()) {
				continue;
			}
			
			ElementExtractedValue elementExtractedValue = new ElementExtractedValue();
			Object cellValue = null;
			if (varfle.getFlexibleElementDTO() instanceof BudgetElementDTO) {
				VariableBudgetElementDTO varBudgetElement = (VariableBudgetElementDTO) varfle;
				for (VariableBudgetSubFieldDTO varBsfDTO : varBudgetElement.getVariableBudgetSubFieldsDTO()) {
					cellValue = getValueFromVariable(varBsfDTO.getVariableDTO().getReference(), lineNumber, sheetName);
					Object[] valueStatus = getValueFormatForFlexibleElement(varfle.getFlexibleElementDTO(),
					                cellValue);
					if(valueStatus[0] != null) {
						elementExtractedValue.getNewBudgetValues().put(varBsfDTO.getBudgetSubFieldDTO().getId(),
										(Serializable) valueStatus[0]);
					}
					if(valueStatus[1] != null) {
						elementExtractedValue.setStatus((ElementExtractedValueStatus) valueStatus[1]);
					}
					if (entityDTO != null) {
						elementExtractedValue.setOldBudgetValues(getBudgetElementValue(varfle.getFlexibleElementDTO(),
						                entityDTO));
					}
				}
				
			} else {
				cellValue = getValueFromVariable(varfle.getVariableDTO().getReference(), lineNumber, sheetName);
				Object[] valueStatus = getValueFormatForFlexibleElement(varfle.getFlexibleElementDTO(),
				                cellValue);
				if(valueStatus[0] != null) {
					elementExtractedValue.setNewValue((Serializable) valueStatus[0]);
				}
				if(valueStatus[1] != null) {
					elementExtractedValue.setStatus((ElementExtractedValueStatus) valueStatus[1]);
				}
			}
			elementExtractedValue.setElement(varfle.getFlexibleElementDTO());
			if (entityDTO != null) {
				elementExtractedValue.setOldValue(getFlexibleElementValue(varfle.getFlexibleElementDTO(), entityDTO,
				                false));
			}
			correspondances.add(elementExtractedValue);
		}
		return correspondances;
	}

	/**
	 * Get the String value of the corresponding variable DTO reference from the
	 * imported file
	 * 
	 * @param reference
	 * @param lineNumber
	 * @param sheetName
	 * @return
	 */

	public abstract Object getValueFromVariable(String reference, Integer lineNumber, String sheetName) throws FunctionalException;

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
	 * Get the value of the flexible Element
	 * 
	 * @param fleDTO
	 * @param projectDTO
	 * @param class1
	 * @return
	 * @throws CommandException
	 */
	public Serializable getFlexibleElementValue(FlexibleElementDTO fleDTO, EntityDTO<Integer> entityDTO, Boolean forkey)
	                throws CommandException {
		Class<?> entityClass;
		if (entityDTO instanceof OrgUnitDTO) {
			entityClass = OrgUnit.class;
		} else {
			entityClass = Project.class;
		}
		final GetValue command = new GetValue(entityDTO.getId(), fleDTO.getId(), fleDTO.getEntityName(), null);
		final ValueResult valueResult = executionContext.execute(command);

		GlobalExportDataProvider gdp = new GlobalExportDataProvider(injector);

		Object entity = mapper.map(entityDTO, entityClass);

		final FlexibleElement element;
		final Serializable valueObject;
		
		switch (fleDTO.getElementType()) {
		case CHECKBOX:
			element = mapper.map(fleDTO, new CheckboxElement());
			valueObject = getCheckboxValue(valueResult, element);
			break;
		case DEFAULT:
			element = mapper.map(fleDTO, new DefaultFlexibleElement());
			if (!DefaultFlexibleElementType.BUDGET.equals(((DefaultFlexibleElement) element).getType())) {
				valueObject = (Serializable) gdp.getDefElementPair(valueResult, element, entity, entityClass, em,
				                translator, language).getValue();
			} else {
				valueObject = null;
			}
			break;
		case QUESTION:
			element = mapper.map(fleDTO, new QuestionElement());
			valueObject = (Serializable) gdp.getChoicePair(element, valueResult).getValue();
			break;
		case TEXT_AREA:
			element = mapper.map(fleDTO, new TextAreaElement());
			valueObject = (Serializable) gdp.getTextAreaElementPair(valueResult, element).getValue();
			break;
		case TRIPLETS:
			element = mapper.map(fleDTO, new TripletsListElement());
			valueObject = (Serializable) gdp.getTripletPair(element, valueResult).getValue();
			break;
		default:
			valueObject = null;
			break;
		}
		return valueObject;
	}

	private Map<Integer, String> getBudgetElementValue(FlexibleElementDTO flexibleElementDTO, EntityDTO<Integer> entityDTO)
	                throws CommandException {
		final GetValue command = new GetValue(entityDTO.getId(), flexibleElementDTO.getId(),
		                flexibleElementDTO.getEntityName(), null);

		final ValueResult valueResult = executionContext.execute(command);
		final Map<Integer, String> values = ValueResultUtils.splitMapElements(valueResult.getValueObject());
		return values;
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
	public void getCorrespondancePerSheetOrLine(ImportationSchemeModelDTO schemeModelDTO, Integer lineNumber,
	                String sheetName) throws CommandException {
		if (schemeModelDTO.getIdKey() != null) {

			ImportDetails importEntity = new ImportDetails();

			final VariableDTO varDTO = schemeModelDTO.getIdKey().getVariableDTO();
			final FlexibleElementDTO fleDTO = schemeModelDTO.getIdKey().getFlexibleElementDTO();

			final Object cellValue = getValueFromVariable(varDTO.getReference(), lineNumber, sheetName);

			if (cellValue == null) {
				return;
			}
			LOGGER.debug("Key identification is " + cellValue.toString());
			String fleName = ExporterUtil.getFlexibleElementLabel(fleDTO, translator, language);

			Map<EntityDTO<?>, List<ElementExtractedValue>> mapEntityCorrespondances = new HashMap<EntityDTO<?>, List<ElementExtractedValue>>();

			// Checks if the model is an orgUnit or a project model
			if (schemeModelDTO.getOrgUnitModelDTO() != null) {

				OrgUnitModelDTO orgUnitModelDTO = schemeModelDTO.getOrgUnitModelDTO();

				LOGGER.debug("Import for org unit model : " + orgUnitModelDTO.getName());

				importEntity.setModelName(orgUnitModelDTO.getName());
				importEntity.setModelStatus(orgUnitModelDTO.getStatus());

				// Get all the orgUnits from an orgUnit model
				final GetOrgUnitsByModel cmdGOU = new GetOrgUnitsByModel(orgUnitModelDTO.getId(), null);

				ListResult<OrgUnitDTO> resultGP = executionContext.execute(cmdGOU);
				List<OrgUnitDTO> list = resultGP.getList();

				// For each project get the value of the corresponding
				// identification key
				for (OrgUnitDTO orgUnitDTO : list) {

					String valueString = (String) getFlexibleElementValue(fleDTO, orgUnitDTO, true);

					if (valueString.equals(cellValue)) {

						List<ElementExtractedValue> correspondances = getCorrespondancesVariableFlexibleElement(
						                schemeModelDTO.getVariableFlexibleElementsDTO(), orgUnitDTO, lineNumber,
						                sheetName);

						mapEntityCorrespondances.put(orgUnitDTO, correspondances);

					}
				}

				// Initializes the importEntity according to the number of
				// orgUnits found
				importEntity.setKeyIdentification(fleName + " : " + cellValue);
				if (mapEntityCorrespondances.isEmpty()) {
					importEntity.setEntityStatus(ImportStatusCode.ORGUNIT_NOT_FOUND_CODE);
					List<ElementExtractedValue> correspondances = getCorrespondancesVariableFlexibleElement(
					                schemeModelDTO.getVariableFlexibleElementsDTO(), null, lineNumber, sheetName);
					OrgUnitDTO o = new OrgUnitDTO();
					o.setId(0);
					mapEntityCorrespondances.put(o, correspondances);
				} else if (mapEntityCorrespondances.size() == 1) {
					importEntity.setEntityStatus(ImportStatusCode.ORGUNIT_FOUND_CODE);
				} else {
					importEntity.setEntityStatus(ImportStatusCode.SEVERAL_ORGUNITS_FOUND_CODE);
				}
				importEntity.setEntitiesToImport(mapEntityCorrespondances);

			} else if (schemeModelDTO.getProjectModelDTO() != null) {
				Map<EntityDTO<?>, List<ElementExtractedValue>> mapLockedEntityCorrespondances = new HashMap<EntityDTO<?>, List<ElementExtractedValue>>();

				ProjectModelDTO projectModelDTO = schemeModelDTO.getProjectModelDTO();

				LOGGER.debug("Import for project model : " + projectModelDTO.getName());

				importEntity.setModelName(projectModelDTO.getName());
				importEntity.setModelStatus(projectModelDTO.getStatus());

				// Get all the projects of a project model
				GetProjectsByModel cmdGP = new GetProjectsByModel(projectModelDTO.getId(), null);
				ListResult<ProjectDTO> resultGP = executionContext.execute(cmdGP);
				List<ProjectDTO> list = resultGP.getList();

				// For each project get the value of the corresponding
				// identification key
				for (ProjectDTO projectDTO : list) {
					String valueString = (String) getFlexibleElementValue(fleDTO, projectDTO, true);

					if (valueString != null && valueString.equals(cellValue)) {

						List<ElementExtractedValue> correspondances = getCorrespondancesVariableFlexibleElement(
						                schemeModelDTO.getVariableFlexibleElementsDTO(), projectDTO, lineNumber,
						                sheetName);
						if (projectDTO.getAmendmentState() != null && projectDTO.getAmendmentState() == AmendmentState.LOCKED) {
							if (mapLockedEntityCorrespondances.isEmpty()) {
								mapLockedEntityCorrespondances.put(projectDTO, correspondances);
							}
						} else {
							mapEntityCorrespondances.put(projectDTO, correspondances);

						}
					}

				}

				// Initializes the importEntity according to the number of
				// projects found
				importEntity.setKeyIdentification(fleName + " : " + cellValue);
				if (mapEntityCorrespondances.isEmpty()) {
					if (!mapLockedEntityCorrespondances.isEmpty()) {
						importEntity.setEntityStatus(ImportStatusCode.PROJECT_LOCKED_CODE);
						importEntity.setEntitiesToImport(mapLockedEntityCorrespondances);
						
					} else {
						importEntity.setEntityStatus(ImportStatusCode.PROJECT_NOT_FOUND_CODE);
						List<ElementExtractedValue> correspondances = getCorrespondancesVariableFlexibleElement(
						                schemeModelDTO.getVariableFlexibleElementsDTO(), null, lineNumber, sheetName);
						ProjectDTO p = new ProjectDTO();
						p.setId(0);
						mapEntityCorrespondances.put(p, correspondances);
						importEntity.setEntitiesToImport(mapEntityCorrespondances);
					}
					
				} else if (mapEntityCorrespondances.size() == 1) {
					importEntity.setEntityStatus(ImportStatusCode.PROJECT_FOUND_CODE);
					importEntity.setEntitiesToImport(mapEntityCorrespondances);
					
				} else {
					importEntity.setEntityStatus(ImportStatusCode.SEVERAL_PROJECTS_FOUND_CODE);
					importEntity.setEntitiesToImport(mapEntityCorrespondances);
				}
			}

			entitiesToImport.add(importEntity);
		}
	}

	/**
	 * Gets the right format of the value for the flexible element provided
	 * 
	 * @param fleDTO
	 * @param value
	 * @return
	 */
	public Object[] getValueFormatForFlexibleElement(FlexibleElementDTO fleDTO, Object value) {
		Object[] valueStatus = new Object[2];
		Serializable formattedValue = null;
		ElementExtractedValueStatus statusCode = null;
		String stringValue = String.valueOf(value);
		if (value != null) {
			switch (fleDTO.getElementType()) {
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
				DefaultFlexibleElementDTO dfDTO = (DefaultFlexibleElementDTO) fleDTO;
				if (!stringValue.isEmpty()) {
					switch (dfDTO.getType()) {
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
				QuestionElementDTO questionElement = (QuestionElementDTO) fleDTO;
				if (questionElement.getMultiple() != null && questionElement.getMultiple()) {
					String[] extractedQuestionValues = stringValue.split("-");
					List<QuestionChoiceElementDTO> choices = new ArrayList<QuestionChoiceElementDTO>();
					for (QuestionChoiceElementDTO choice : questionElement.getChoices()) {
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
					if(!choices.isEmpty()){
						formattedValue = ValueResultUtils.mergeValues(choices);
						statusCode = ElementExtractedValueStatus.VALID_VALUE;
					} else {
						statusCode = ElementExtractedValueStatus.INVALID_QUESTION_VALUE;
					}
					
				} else {
					for (QuestionChoiceElementDTO choice : questionElement.getChoices()) {
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
				TextAreaElementDTO textAreaElementDTO = (TextAreaElementDTO) fleDTO;
				switch (TextAreaType.fromCode(textAreaElementDTO.getType())) {
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
						Date minValue = textAreaElementDTO.getMinValue() != null ?  new Date(textAreaElementDTO.getMinValue()) : null;
						Date maxValue =textAreaElementDTO.getMaxValue() != null ?  new Date(textAreaElementDTO.getMaxValue()) : null;
						boolean isValueCorrect = !((minValue != null && dateValue.before(minValue)) || (maxValue != null && dateValue.after(minValue)));
						if(!isValueCorrect) {
							statusCode = ElementExtractedValueStatus.FORBIDDEN_VALUE;
						}
					}
				}
					break;
				case NUMBER: {
					if (textAreaElementDTO.getIsDecimal()) {
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
							Long minValue = textAreaElementDTO.getMinValue() != null ?  textAreaElementDTO.getMinValue() : null;
							Long maxValue =textAreaElementDTO.getMaxValue() != null ?  textAreaElementDTO.getMaxValue() : null;
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
							Long minValue = textAreaElementDTO.getMinValue() != null ?  textAreaElementDTO.getMinValue() : null;
							Long maxValue =textAreaElementDTO.getMaxValue() != null ?  textAreaElementDTO.getMaxValue() : null;
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
				LOGGER.warn("Unsupported flexible element type: {}",  fleDTO.getElementType());
				break;

			}
		}
		valueStatus[0] = formattedValue;
		valueStatus[1] = statusCode;
		return valueStatus;

	}

	protected void logWarnFormatImportTypeIncoherence() {
		LOGGER.warn("Incoherence in ImporationScheme fileFormat ("
		                + ImportationSchemeFileFormat.getStringValue(scheme.getFileFormat()) + " and its importType "
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
	
	private EntityManager em() {
		return injector.getProvider(EntityManager.class).get();
	}
	
}
