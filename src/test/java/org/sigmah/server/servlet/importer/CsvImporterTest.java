package org.sigmah.server.servlet.importer;

import com.google.inject.Inject;
import com.google.inject.Injector;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityTransaction;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sigmah.server.dao.AbstractDaoTest;
import org.sigmah.server.dao.UserDAO;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.domain.Bounds;
import org.sigmah.server.domain.Country;
import org.sigmah.server.domain.Phase;
import org.sigmah.server.domain.PhaseModel;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.ProjectBanner;
import org.sigmah.server.domain.ProjectDetails;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.server.domain.ProjectModelVisibility;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.base.Entity;
import org.sigmah.server.domain.element.DefaultFlexibleElement;
import org.sigmah.server.domain.element.TextAreaElement;
import org.sigmah.server.domain.importation.ImportationScheme;
import org.sigmah.server.domain.importation.ImportationSchemeModel;
import org.sigmah.server.domain.importation.Variable;
import org.sigmah.server.domain.importation.VariableFlexibleElement;
import org.sigmah.server.domain.layout.Layout;
import org.sigmah.server.domain.value.Value;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.server.security.Authenticator;
import org.sigmah.shared.Language;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.ElementExtractedValue;
import org.sigmah.shared.dto.ImportDetails;
import org.sigmah.shared.dto.base.EntityDTO;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;
import org.sigmah.shared.dto.referential.DefaultFlexibleElementType;
import org.sigmah.shared.dto.referential.ImportStatusCode;
import org.sigmah.shared.dto.referential.ImportationSchemeFileFormat;
import org.sigmah.shared.dto.referential.ImportationSchemeImportType;
import org.sigmah.shared.dto.referential.LogicalElementType;
import org.sigmah.shared.dto.referential.LogicalElementTypes;
import org.sigmah.shared.dto.referential.ProjectModelStatus;
import org.sigmah.shared.dto.referential.TextAreaType;

/**
 * Test class for <code>CsvImporter</code>.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class CsvImporterTest extends AbstractDaoTest {
	
	private static final String EMAIL_ADDRESS = "urd-sigmah+test@ideia.fr";
	
	@Inject
	private Injector injector;
	
	@Inject
	private Mapper mapper;
	
	@Inject
	private Authenticator authenticator;
	
	@Inject
	private UserDAO userDAO;
	
	@Inject
	private UserDispatch dispatch;
	
	private Entity[] entities = new Entity[0];
	
	private int projectId;
	private int schemeId;
	
	@Before
	public void before() {
		persistEntities();
	}
	
	@After
	public void after() {
		removeEntities();
	}
	
	@Test
	public void testGetCorrespondances() throws CommandException, IOException {
		final CsvImporter importer = new CsvImporter();
		importer.setInjector(injector);
		importer.setScheme(getImportationScheme());
		importer.setExecutionContext(getExecutionContext());
		importer.initialize();
		
		importer.setInputStream(getClass().getResourceAsStream("import.csv"));
		
		final List<ImportDetails> correspondances = importer.getCorrespondances();
		Assert.assertNotNull(correspondances);
		Assert.assertEquals(2, correspondances.size());
		
		final ImportDetails details = correspondances.get(0);
		Assert.assertEquals(ImportStatusCode.PROJECT_FOUND_CODE, details.getEntityStatus());
		
		final Map.Entry<EntityDTO<Integer>, List<ElementExtractedValue>> singleEntity = details.getEntitiesToImport().entrySet().iterator().next();
		final EntityDTO<Integer> entity = singleEntity.getKey();
		Assert.assertEquals(projectId, (int) entity.getId());
		
		Assert.assertEquals(2, singleEntity.getValue().size());
		for (final ElementExtractedValue value : singleEntity.getValue()) {
			final LogicalElementType type = LogicalElementTypes.of(value.getElement());
			
			if (type == DefaultFlexibleElementType.TITLE) {
				Assert.assertEquals("Mon projet d’import", value.getNewValue());
				Assert.assertEquals("TestProject", value.getOldValue());
			} else if (type == TextAreaType.TEXT) {
				Assert.assertEquals("Ce projet sérieux et plein d'avenir devrait sauver beaucoup de personnes", value.getNewValue());
				Assert.assertEquals("Pas d'introduction", value.getOldValue());
			} else {
				Assert.fail();
			}
		}
		
		Assert.assertEquals(ImportStatusCode.PROJECT_NOT_FOUND_CODE, correspondances.get(1).getEntityStatus());
		final Map.Entry<EntityDTO<Integer>, List<ElementExtractedValue>> noEntity = correspondances.get(1).getEntitiesToImport().entrySet().iterator().next();
		Assert.assertEquals(3, noEntity.getValue().size());
		for (final ElementExtractedValue value : noEntity.getValue()) {
			final LogicalElementType type = LogicalElementTypes.of(value.getElement());
			
			if (type == DefaultFlexibleElementType.CODE) {
				Assert.assertEquals("I8", value.getNewValue());
				Assert.assertNull(value.getOldValue());
			} else if (type == DefaultFlexibleElementType.TITLE) {
				Assert.assertEquals("Mon projet qui n'existe pas", value.getNewValue());
				Assert.assertNull(value.getOldValue());
			} else if (type == TextAreaType.TEXT) {
				Assert.assertEquals("Rien", value.getNewValue());
				Assert.assertNull(value.getOldValue());
			} else {
				Assert.fail();
			}
		}
	}
	
	private ImportationSchemeDTO getImportationScheme() {
		return mapper.map(em().find(ImportationScheme.class, schemeId), new ImportationSchemeDTO());
	}
	
	private UserDispatch.UserExecutionContext getExecutionContext() {
		final User user = userDAO.findUserByEmail(EMAIL_ADDRESS);
		return dispatch.createContext(user, null, null);
	}
	
	private void persistEntities() {
		final EntityTransaction transaction = em().getTransaction();
		transaction.begin();
		
		// Project Model
		final DefaultFlexibleElement codeElement = new DefaultFlexibleElement();
		codeElement.setType(DefaultFlexibleElementType.CODE);
		em().persist(codeElement);
		
		final DefaultFlexibleElement titleElement = new DefaultFlexibleElement();
		titleElement.setType(DefaultFlexibleElementType.TITLE);
		em().persist(titleElement);
		
		final TextAreaElement introductionElement = new TextAreaElement();
		introductionElement.setType(TextAreaType.TEXT.getCode());
		em().persist(introductionElement);
		
		final Layout detailsLayout = new Layout(3, 1);
		detailsLayout.addConstraint(0, 0, codeElement, 0);
		detailsLayout.addConstraint(1, 0, titleElement, 0);
		detailsLayout.addConstraint(2, 0, introductionElement, 0);
		
		final Layout bannerLayout = new Layout(0, 0);
		
		final ProjectModel model = new ProjectModel();
		model.setName("TestModel");
		model.setStatus(ProjectModelStatus.READY);
		model.setPhaseModels(new ArrayList<PhaseModel>());
		model.setVisibilities(new ArrayList<ProjectModelVisibility>());
		
		final ProjectDetails details = new ProjectDetails();
		details.setLayout(detailsLayout);
		details.setProjectModel(model);
		
		em().persist(details);
		
		final ProjectBanner banner = new ProjectBanner();
		banner.setProjectModel(model);
		banner.setLayout(bannerLayout);
		
		em().persist(banner);
		
		model.setProjectDetails(details);
		model.setProjectBanner(banner);
		
		em().persist(model);
		
		// User
		final User user = new User();
		user.setActive(Boolean.TRUE);
		user.setEmail(EMAIL_ADDRESS);
		user.setName("TestLastName");
		user.setFirstName("TestFirstName");
		user.setHashedPassword(authenticator.hashPassword("sigmah"));
		user.setLocale(Language.FR.getLocale());
		em().persist(user);
		
		// Importation Scheme
		final ArrayList<Variable> variables = new ArrayList<>();
		
		final ImportationScheme scheme = new ImportationScheme();
		scheme.setName("Test scheme");
		scheme.setFileFormat(ImportationSchemeFileFormat.CSV);
		scheme.setFirstRow(1);
		scheme.setImportType(ImportationSchemeImportType.ROW);
		scheme.setVariables(variables);
		
		final Variable codeVariable = new Variable();
		codeVariable.setImportationScheme(scheme);
		codeVariable.setName("Code");
		codeVariable.setReference("0");
		
		final Variable titleVariable = new Variable();
		titleVariable.setImportationScheme(scheme);
		titleVariable.setName("Title");
		titleVariable.setReference("1");
		
		final Variable introductionVariable = new Variable();
		introductionVariable.setImportationScheme(scheme);
		introductionVariable.setName("Introduction");
		introductionVariable.setReference("5");
		
		variables.add(codeVariable);
		variables.add(titleVariable);
		variables.add(introductionVariable);
		
		em().persist(scheme);
		
		schemeId = scheme.getId();
		
		// Importation Scheme Model
		final ArrayList<VariableFlexibleElement> variableFlexibleElements = new ArrayList<>();
		
		final ImportationSchemeModel importationSchemeModel = new ImportationSchemeModel();
		importationSchemeModel.setImportationScheme(scheme);
		importationSchemeModel.setProjectModel(model);
		importationSchemeModel.setVariableFlexibleElements(variableFlexibleElements);
		
		final VariableFlexibleElement codeVariableFlexibleElement = new VariableFlexibleElement();
		codeVariableFlexibleElement.setImportationSchemeModel(importationSchemeModel);
		codeVariableFlexibleElement.setIsKey(Boolean.TRUE);
		codeVariableFlexibleElement.setFlexibleElement(codeElement);
		codeVariableFlexibleElement.setVariable(codeVariable);
		
		final VariableFlexibleElement titleVariableFlexibleElement = new VariableFlexibleElement();
		titleVariableFlexibleElement.setImportationSchemeModel(importationSchemeModel);
		titleVariableFlexibleElement.setFlexibleElement(titleElement);
		titleVariableFlexibleElement.setVariable(titleVariable);
		
		final VariableFlexibleElement introductionVariableFlexibleElement = new VariableFlexibleElement();
		introductionVariableFlexibleElement.setImportationSchemeModel(importationSchemeModel);
		introductionVariableFlexibleElement.setFlexibleElement(introductionElement);
		introductionVariableFlexibleElement.setVariable(introductionVariable);
		
		variableFlexibleElements.add(codeVariableFlexibleElement);
		variableFlexibleElements.add(titleVariableFlexibleElement);
		variableFlexibleElements.add(introductionVariableFlexibleElement);
		
		em().persist(importationSchemeModel);
		
		
		// Project, country and values
		final Country country = new Country();
		country.setName("Testry");
		country.setCodeISO("_t");
		country.setBounds(new Bounds());
		em().persist(country);
		
		final Project project = new Project();
		project.setProjectModel(model);
		project.setName("I1");
		project.setFullName("TestProject");
		project.setPhases(new ArrayList<Phase>());
		project.setStartDate(new Date());
		project.setOwner(user);
		project.setLastSchemaUpdate(new Date());
		project.setCountry(country);
		em().persist(project);
		
		this.projectId = project.getId();
		
		final Value introductionValue = new Value();
		introductionValue.setValue("Pas d'introduction");
		introductionValue.setContainerId(project.getId());
		introductionValue.setElement(introductionElement);
		introductionValue.setLastModificationAction('C');
		introductionValue.setLastModificationDate(new Date());
		introductionValue.setLastModificationUser(user);
		em().persist(introductionValue);
		
		transaction.commit();
		
		this.entities = new Entity[] {
			introductionValue,
			project,
			banner,
			details,
			codeVariableFlexibleElement,
			titleVariableFlexibleElement,
			introductionVariableFlexibleElement,
			importationSchemeModel,
			codeVariable,
			titleVariable,
			introductionVariable,
			scheme,
			model,
			bannerLayout,
			detailsLayout,
			introductionElement,
			titleElement,
			codeElement,
			user,
			country
		};
	}
	
	private void removeEntities() {
		final EntityTransaction transaction = em().getTransaction();
		transaction.begin();
		
		for (final Entity entity : entities) {
			em().remove(entity);
		}
		this.entities = new Entity[0];
		transaction.commit();
	}
	
}
