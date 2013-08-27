package org.sigmah.server.endpoint.export.sigmah;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import org.sigmah.shared.dto.EntityDTO;
import org.sigmah.shared.dto.ImportUtils.ImportStatusCode;
import org.sigmah.shared.dto.OrgUnitDTO;
import org.sigmah.shared.dto.OrgUnitDTOLight;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.element.BudgetElementDTO;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
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
					Serializable valueForFle = getValueFormatForFlexibleElement(varfle.getFlexibleElementDTO(),
					                cellValue);
					elementExtractedValue.getNewBudgetValues().put(varBsfDTO.getBudgetSubFieldDTO().getId(),
					                valueForFle);
					if (entityDTO != null) {
						elementExtractedValue.setOldBudgetValues(getBudgetElementValue(varfle.getFlexibleElementDTO(),
						                entityDTO));
					}
				}
			} else {
				cellValue = getValueFromVariable(varfle.getVariableDTO().getReference(), lineNumber, sheetName);
				Serializable valueForFle = getValueFormatForFlexibleElement(varfle.getFlexibleElementDTO(), cellValue);
				elementExtractedValue.setNewValue(valueForFle);
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
					Log.debug("Import for org unit model : " + projectModelDTO.getName());
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
	public Serializable getValueFormatForFlexibleElement(FlexibleElementDTO fleDTO, Object value) {
		Serializable formattedValue = null;
		if (value != null) {
			switch (fleDTO.getElementType()) {
			case CHECKBOX:
				if (value instanceof Boolean) {
					formattedValue = (Serializable) value;
				} else if (value instanceof String) {
					String stringValue = (String) value;
					if ("true".equalsIgnoreCase(stringValue) || "false".equalsIgnoreCase(stringValue)) {
						formattedValue = Boolean.valueOf(stringValue);
					}
				}
				break;
			case DEFAULT:
				DefaultFlexibleElementDTO dfDTO = (DefaultFlexibleElementDTO) fleDTO;
				if (!value.toString().isEmpty()) {
					if (DefaultFlexibleElementType.START_DATE.equals(dfDTO.getType())
					                || DefaultFlexibleElementType.END_DATE.equals(dfDTO.getType())) {
						if (value instanceof Number) {
							Long time = Double.valueOf(value.toString()).longValue();
							formattedValue = new Date(time);
						} else if (value instanceof Date) {
							formattedValue = (Date) value;
						}
					} else if (!DefaultFlexibleElementType.BUDGET.equals(dfDTO.getType())) {
						formattedValue = value.toString();
					} else {
						formattedValue = Double.valueOf(value.toString());
					}
				}

				break;
			case MESSAGE:
				formattedValue = String.valueOf(value);
				break;
			case QUESTION:
				// TODO Not implemented yet
				break;
			case TEXT_AREA:
				TextAreaElementDTO textAreaElementDTO = (TextAreaElementDTO) fleDTO;
				switch (textAreaElementDTO.getType()) {
				case 'D': {
					if (value instanceof Date) {
						return (Date) value;
					}
				}
					break;
				case 'N': {
					if (value instanceof Double) {
						return (Double) value;
					}
				}
					break;
				case 'T': {
					value = String.valueOf(value);
				}
				default:
					break;
				}
				break;
			case TRIPLETS:
				// TODO Not implemented yet
				break;
			default:
				break;

			}
		}

		return formattedValue;

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

}
