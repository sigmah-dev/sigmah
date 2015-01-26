package org.sigmah.server.endpoint.export.sigmah;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;

import org.dozer.Mapper;
import org.sigmah.client.page.admin.model.common.element.ElementTypeEnum;
import org.sigmah.server.Translator;
import org.sigmah.server.UIConstantsTranslator;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.GlobalExportDataProvider;
import org.sigmah.server.endpoint.gwtrpc.handler.GetImportationSchemeModelsHandler;
import org.sigmah.server.endpoint.gwtrpc.handler.GetOrgUnitsByModelHandler;
import org.sigmah.server.endpoint.gwtrpc.handler.GetProjectsByModelHandler;
import org.sigmah.server.endpoint.gwtrpc.handler.GetValueHandler;
import org.sigmah.shared.Cookies;
import org.sigmah.shared.command.Command;
import org.sigmah.shared.command.GetImportationSchemeModels;
import org.sigmah.shared.command.GetOrgUnitsByModel;
import org.sigmah.shared.command.GetProjectsByModel;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.ImportationSchemeModelListResult;
import org.sigmah.shared.command.result.OrgUnitListResult;
import org.sigmah.shared.command.result.ProjectListResult;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.command.result.ValueResultUtils;
import org.sigmah.shared.domain.Amendment.State;
import org.sigmah.shared.domain.ElementExtractedValue;
import org.sigmah.shared.domain.ImportDetails;
import org.sigmah.shared.domain.OrgUnit;
import org.sigmah.shared.domain.Project;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.element.CheckboxElement;
import org.sigmah.shared.domain.element.DefaultFlexibleElement;
import org.sigmah.shared.domain.element.DefaultFlexibleElementType;
import org.sigmah.shared.domain.element.FlexibleElement;
import org.sigmah.shared.domain.element.QuestionElement;
import org.sigmah.shared.domain.element.TextAreaElement;
import org.sigmah.shared.domain.element.TripletsListElement;
import org.sigmah.shared.domain.importation.ImportationSchemeFileFormat;
import org.sigmah.shared.domain.importation.ImportationSchemeImportType;
import org.sigmah.shared.dto.ElementExtractedValueStatus;
import org.sigmah.shared.dto.EntityDTO;
import org.sigmah.shared.dto.ImportStatusCode;
import org.sigmah.shared.dto.OrgUnitDTO;
import org.sigmah.shared.dto.OrgUnitDTOLight;
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
import org.sigmah.shared.exception.CommandException;

import com.allen_sauer.gwt.log.client.Log;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Class for importing datas from spreadsheet and CSV documents
 * 
 * @author Guerline Jean-Baptiste (gjbaptiste@ideia.fr)
 * 
 */
public abstract class Importer {

	private Injector injector;

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

	private List<ImportDetails> entitiesToImport;

	private final Mapper mapper;

	private final Locale locale;

	private final Translator translator;

	private EntityManager em;

	private User user;

	protected List<ImportationSchemeModelDTO> schemeModelList;

	@Inject
	public Importer(Injector injector, Map<String, Object> properties, User user) throws Throwable {
		this.injector = injector;
		this.mapper = injector.getInstance(Mapper.class);
		// set up user's locale

		String localeString = Cookies.DEFAULT_LOCALE;
		this.locale = new Locale(localeString);
		this.translator = new UIConstantsTranslator(new Locale(""));
		this.em = injector.getInstance(EntityManager.class);
		this.properties = properties;
		this.user = user;

		entitiesToImport = new ArrayList<ImportDetails>();

		final GetImportationSchemeModels cmd = new GetImportationSchemeModels();
		scheme = (ImportationSchemeDTO) properties.get("scheme");
		cmd.setImportationSchemeId(Long.valueOf(scheme.getId()));

		final GetImportationSchemeModelsHandler handler = new GetImportationSchemeModelsHandler(em, mapper);

		final ImportationSchemeModelListResult result = (ImportationSchemeModelListResult) handler.execute(cmd, null);
		@SuppressWarnings("rawtypes")
		List<Command> commands = new ArrayList<Command>();
		commands.add(cmd);

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
	 * @param schemeModelDTO
	 * @throws Throwable
	 */
	protected abstract void getCorrespondances(List<ImportationSchemeModelDTO> schemeModelList) throws Throwable;

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
	                Integer lineNumber, String sheetName) throws Throwable {
		List<ElementExtractedValue> correspondances = new ArrayList<ElementExtractedValue>();
		for (VariableFlexibleElementDTO varfle : variableFlexibleElementsDTO) {
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
	 * @param varDTO
	 * @param lineNumber
	 * @param sheetName
	 * @return
	 * @throws ServletException
	 */

	public abstract Object getValueFromVariable(String reference, Integer lineNumber, String sheetName)
	                throws Exception;

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
	 * @throws Throwable
	 */
	public Serializable getFlexibleElementValue(FlexibleElementDTO fleDTO, EntityDTO entityDTO, Boolean forkey)
	                throws Throwable {
		Class<?> entityClass;
		if (entityDTO instanceof OrgUnitDTOLight) {
			entityClass = OrgUnit.class;
		} else {
			entityClass = Project.class;
		}
		final GetValue command = new GetValue(entityDTO.getId(), fleDTO.getId(), fleDTO.getEntityName(), null);

		final CommandHandler<GetValue> handler = new GetValueHandler(em, injector.getInstance(Mapper.class));
		final ValueResult valueResult = (ValueResult) handler.execute(command, null);

		GlobalExportDataProvider gdp = new GlobalExportDataProvider(injector);

		FlexibleElement element = null;

		Object entity = mapper.map(entityDTO, entityClass);

		Serializable valueObject = null;
		switch (fleDTO.getElementType()) {
		case CHECKBOX:
			element = mapper.map(fleDTO, CheckboxElement.class);
			valueObject = getCheckboxValue(valueResult, element);
			break;
		case DEFAULT:
			element = mapper.map(fleDTO, DefaultFlexibleElement.class);
			if (!DefaultFlexibleElementType.BUDGET.equals(((DefaultFlexibleElement) element).getType())) {
				valueObject = (Serializable) gdp.getDefElementPair(valueResult, element, entity, entityClass, em,
				                locale, translator).getValue();
			}
			break;
		case QUESTION:
			element = mapper.map(fleDTO, QuestionElement.class);
			valueObject = (Serializable) gdp.getChoicePair(element, valueResult).getValue();
			break;
		case TEXT_AREA:
			element = mapper.map(fleDTO, TextAreaElement.class);
			valueObject = (Serializable) gdp.getTextAreaElementPair(valueResult, element).getValue();
			break;
		case TRIPLETS:
			element = mapper.map(fleDTO, TripletsListElement.class);
			valueObject = (Serializable) gdp.getTripletPair(element, valueResult).getValue();
			break;
		default:
			break;
		}
		return valueObject;
	}

	private Map<Integer, String> getBudgetElementValue(FlexibleElementDTO flexibleElementDTO, EntityDTO entityDTO)
	                throws CommandException {
		final GetValue command = new GetValue(entityDTO.getId(), flexibleElementDTO.getId(),
		                flexibleElementDTO.getEntityName(), null);

		final CommandHandler<GetValue> handler = new GetValueHandler(em, injector.getInstance(Mapper.class));
		final ValueResult valueResult = (ValueResult) handler.execute(command, null);
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
	 * @throws Throwable
	 */
	public void getCorrespondancePerSheetOrLine(ImportationSchemeModelDTO schemeModelDTO, Integer lineNumber,
	                String sheetName) throws Throwable {
		if (schemeModelDTO.getIdKey() != null) {

			ImportDetails importEntity = new ImportDetails();

			final VariableDTO varDTO = schemeModelDTO.getIdKey().getVariableDTO();
			final FlexibleElementDTO fleDTO = schemeModelDTO.getIdKey().getFlexibleElementDTO();

			final GetOrgUnitsByModelHandler orgUnitHandler = new GetOrgUnitsByModelHandler(em, mapper, injector);
			final GetProjectsByModelHandler projectHandler = new GetProjectsByModelHandler(em, mapper);

			final Object cellValue = getValueFromVariable(varDTO.getReference(), lineNumber, sheetName);

			if (cellValue == null) {
				return;
			}
			if (Log.isDebugEnabled()) {
				Log.debug("Key identification is " + cellValue.toString());
			}
			String fleName = getFlexibleElementLabel(fleDTO);

			Map<EntityDTO, List<ElementExtractedValue>> mapEntityCorrespondances = new HashMap<EntityDTO, List<ElementExtractedValue>>();

			// Checks if the model is an orgUnit or a project model
			if (schemeModelDTO.getOrgUnitModelDTO() != null) {

				OrgUnitModelDTO orgUnitModelDTO = schemeModelDTO.getOrgUnitModelDTO();

				if (Log.isDebugEnabled()) {
					Log.debug("Import for org unit model : " + orgUnitModelDTO.getName());
				}

				importEntity.setModelName(orgUnitModelDTO.getName());
				importEntity.setModelStatus(orgUnitModelDTO.getStatus());

				// Get all the orgUnits from an orgUnit model
				final GetOrgUnitsByModel cmdGOU = new GetOrgUnitsByModel(orgUnitModelDTO.getId());

				OrgUnitListResult resultGP = (OrgUnitListResult) orgUnitHandler.execute(cmdGOU, user);
				List<OrgUnitDTOLight> list = resultGP.getOrgUnitDTOLightList();

				// For each project get the value of the corresponding
				// identification key
				for (OrgUnitDTOLight orgUnitDTO : list) {

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
				if (mapEntityCorrespondances.size() == 0) {
					importEntity.setEntityStatus(ImportStatusCode.ORGUNIT_NOT_FOUND_CODE);
					List<ElementExtractedValue> correspondances = getCorrespondancesVariableFlexibleElement(
					                schemeModelDTO.getVariableFlexibleElementsDTO(), null, lineNumber, sheetName);
					OrgUnitDTOLight o = new OrgUnitDTOLight();
					o.setId(0);
					mapEntityCorrespondances.put(o, correspondances);
				} else if (mapEntityCorrespondances.size() == 1) {
					importEntity.setEntityStatus(ImportStatusCode.ORGUNIT_FOUND_CODE);
				} else {
					importEntity.setEntityStatus(ImportStatusCode.SEVERAL_ORGUNITS_FOUND_CODE);
				}
				importEntity.setEntitiesToImport(mapEntityCorrespondances);

			} else if (schemeModelDTO.getProjectModelDTO() != null) {
				Map<EntityDTO, List<ElementExtractedValue>> mapLockedEntityCorrespondances = new HashMap<EntityDTO, List<ElementExtractedValue>>();

				ProjectModelDTO projectModelDTO = schemeModelDTO.getProjectModelDTO();

				if (Log.isDebugEnabled()) {
					Log.debug("Import for project model : " + projectModelDTO.getName());
				}

				importEntity.setModelName(projectModelDTO.getName());
				importEntity.setModelStatus(projectModelDTO.getStatus());

				// Get all the projects of a project model
				GetProjectsByModel cmdGP = new GetProjectsByModel(Long.valueOf(projectModelDTO.getId()));
				cmdGP.setAsProjectDTOs(true);
				ProjectListResult resultGP = (ProjectListResult) projectHandler.execute(cmdGP, user);
				List<ProjectDTO> list = resultGP.getListProjectsDTO();

				// For each project get the value of the corresponding
				// identification key
				for (ProjectDTO projectDTO : list) {
					String valueString = (String) getFlexibleElementValue(fleDTO, projectDTO, true);

					if (valueString.equals(cellValue)) {

						List<ElementExtractedValue> correspondances = getCorrespondancesVariableFlexibleElement(
						                schemeModelDTO.getVariableFlexibleElementsDTO(), projectDTO, lineNumber,
						                sheetName);
						if (projectDTO.getAmendmentState() != null && projectDTO.getAmendmentState() == State.LOCKED) {
							if (mapLockedEntityCorrespondances.size() == 0) {
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
				if (mapEntityCorrespondances.size() == 0) {
					if (mapLockedEntityCorrespondances.size() != 0) {
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
					String noValue = translator.translate("no", locale);
					String yesValue = translator.translate("yes", locale);
					if ("true".equalsIgnoreCase(stringValue) || "false".equalsIgnoreCase(stringValue)) {
						formattedValue = Boolean.valueOf(stringValue);
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
					if (DefaultFlexibleElementType.START_DATE.equals(dfDTO.getType())
					                || DefaultFlexibleElementType.END_DATE.equals(dfDTO.getType())) {
						if (value instanceof Number) {
							Long time = Double.valueOf(stringValue).longValue();
							formattedValue = new Date(time);
							statusCode = ElementExtractedValueStatus.VALID_VALUE;
						} else if (value instanceof Date) {
							formattedValue = (Date) value;
							statusCode = ElementExtractedValueStatus.VALID_VALUE;
						}else if (value instanceof String) {
							SimpleDateFormat defaultFormat = new SimpleDateFormat("dd/MM/yy");
							try {
								formattedValue = defaultFormat.parse(stringValue);
								statusCode = ElementExtractedValueStatus.VALID_VALUE;
							} catch (ParseException e) {
								statusCode = ElementExtractedValueStatus.INVALID_DATE_VALUE;
							}
						}
					} else if (DefaultFlexibleElementType.BUDGET.equals(dfDTO.getType())) {
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
					} else {
						formattedValue = stringValue;
						statusCode = ElementExtractedValueStatus.VALID_VALUE;
					}
				}

				break;
			case QUESTION:
				QuestionElementDTO questionElement = (QuestionElementDTO) fleDTO;
				if (questionElement.getIsMultiple()) {
					String[] extractedQuestionValues = stringValue.split("-");
					List<QuestionChoiceElementDTO> choices = new ArrayList<QuestionChoiceElementDTO>();
					for (QuestionChoiceElementDTO choice : questionElement.getChoicesDTO()) {
						String choiceLabel = "";
						if (choice.getCategoryElementDTO() != null) {
							choiceLabel = choice.getCategoryElementDTO().getLabel();
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
					for (QuestionChoiceElementDTO choice : questionElement.getChoicesDTO()) {
						String choiceLabel = "";
						if (choice.getCategoryElementDTO() != null) {
							choiceLabel = choice.getCategoryElementDTO().getLabel();
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
				TextAreaElementDTO textAreaElementDTO = (TextAreaElementDTO) fleDTO;
				switch (textAreaElementDTO.getType()) {
				case 'D': {
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
				case 'N': {
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
				case 'T': {
					value = String.valueOf(value);
					statusCode = ElementExtractedValueStatus.VALID_VALUE;
				}
				default:
					break;
				}
				break;
			case TRIPLETS:
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
				break;

			}
		}
		valueStatus[0] = formattedValue;
		valueStatus[1] = statusCode;
		return valueStatus;

	}

	public void logWarnFormatImportTypeIncoherence() {
		Log.warn("Incoherence in  ImporationScheme fileFormat ("
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

	/**
	 * Gets the label of the flexible element
	 * 
	 * @param fleDTO
	 * @return
	 */
	public String getFlexibleElementLabel(FlexibleElementDTO fleDTO) {
		String fleName = null;
		if (ElementTypeEnum.DEFAULT.equals(fleDTO.getElementType())) {
			DefaultFlexibleElementDTO defaultElementDTO = (DefaultFlexibleElementDTO) fleDTO;
			switch (defaultElementDTO.getType()) {
			case BUDGET:
				fleName = translator.translate("projectBudget", locale);
				break;
			case CODE:
				fleName = translator.translate("projectName", locale);
				break;
			case COUNTRY:
				fleName = translator.translate("projectCountry", locale);
				break;
			case END_DATE:
				fleName = translator.translate("projectEndDate", locale);
				break;
			case MANAGER:
				fleName = translator.translate("projectManager", locale);
				break;
			case ORG_UNIT:
				fleName = translator.translate("orgUnit", locale);
				break;
			case OWNER:
				fleName = translator.translate("projectOwner", locale);
				break;
			case START_DATE:
				fleName = translator.translate("projectStartDate", locale);
				break;
			case TITLE:
				fleName = translator.translate("projectFullName", locale);
				break;
			default:
				break;

			}
		} else {
			fleName = fleDTO.getLabel();
		}
		return fleName;
	}

	

	protected int getColumnFromReference(String reference) {
		int column = 0;
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
}
