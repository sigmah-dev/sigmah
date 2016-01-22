package org.sigmah.server.computation;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityTransaction;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sigmah.server.dao.AbstractDaoTest;
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
import org.sigmah.server.domain.element.TextAreaElement;
import org.sigmah.server.domain.layout.Layout;
import org.sigmah.server.domain.value.Value;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.server.security.Authenticator;
import org.sigmah.shared.Language;
import org.sigmah.shared.computation.Computation;
import org.sigmah.shared.computation.Computations;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.TextAreaElementDTO;
import org.sigmah.shared.dto.element.event.ValueEventWrapper;
import org.sigmah.shared.dto.referential.ProjectModelStatus;
import org.sigmah.shared.dto.referential.ValueEventChangeType;

/**
 * Test of ServerValueResolver.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class ServerValueResolverTest extends AbstractDaoTest {
	
	@Inject
	private ServerValueResolver instance;
	
	@Inject
	private Mapper mapper;
	
	@Inject
	private Authenticator authenticator;
	
	private Entity[] entities = new Entity[0];
	
	private int projectId;
	
	@Before
	public void before() {
		persistEntities();
	}
	
	@After
	public void after() {
		removeEntities();
	}
	
	/**
	 * Test of computeValueWithResolver method, of class Computation.
	 */
	@Test
	public void testComputeValueWithResolver() {
		System.out.println("computeValueWithResolver");
		
		final Collection<FlexibleElementDTO> allElements = getElements();
		
		final Computation computation = Computations.parse("12*(neuf-1) + 3.14/(quarante_2+2) + zero", allElements);
		Assert.assertEquals("Computation has not been parsed correctly.", "12 × (neuf - 1) + 3.14 ÷ (quarante_2 + 2) + zero", computation.toHumanReadableString());
		
		computation.computeValueWithResolver(projectId, instance, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				Assert.fail();
			}

			@Override
			public void onSuccess(String result) {
				Assert.assertNotNull("Result must not be null.", result);
				Assert.assertEquals("Computed result is incorrect.", Double.toString(12.0 * (9 - 1) + 3.14 / (4.2 + 2) + 0), result);
			}
		});
	}
	
	/**
	 * Test of computeValueWithResolver method, of class Computation.
	 */
	@Test
	public void testComputeValueWithWrappersAndResolver() {
		System.out.println("computeValueWithWrappersAndResolver");
		
		final Collection<FlexibleElementDTO> allElements = getElements();
		
		final Computation computation = Computations.parse("12*(neuf-1) + 3.14/(quarante_2+2) + zero", allElements);
		Assert.assertEquals("Computation has not been parsed correctly.", "12 × (neuf - 1) + 3.14 ÷ (quarante_2 + 2) + zero", computation.toHumanReadableString());
		
		final List<ValueEventWrapper> modifications = new ArrayList<ValueEventWrapper>();
		
		for (final FlexibleElementDTO dto : allElements) {
			final ValueEventWrapper wrapper = new ValueEventWrapper();
			wrapper.setChangeType(ValueEventChangeType.EDIT);
			wrapper.setProjectCountryChanged(false);
			wrapper.setSourceElement(dto);
			wrapper.setSingleValue("1");
			modifications.add(wrapper);
		}
		
		computation.computeValueWithWrappersAndResolver(projectId, modifications, null, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				Assert.fail();
			}

			@Override
			public void onSuccess(String result) {
				Assert.assertNotNull("Result must not be null.", result);
				Assert.assertEquals("Computed result is incorrect.", Double.toString(12.0 * (1.0 - 1) + 3.14 / (1.0 + 2) + 1.0), result);
			}
		});
	}
	
	private Collection<FlexibleElementDTO> getElements() {
		final ArrayList<FlexibleElementDTO> elements = new ArrayList<>();
		
		for (final Entity entity : entities) {
			if (entity instanceof TextAreaElement) {
				elements.add(mapper.map(entity, new TextAreaElementDTO()));
			}
		}
		
		return elements;
	}
	
	private void persistEntities() {
		final EntityTransaction transaction = em().getTransaction();
		transaction.begin();
		
		final TextAreaElement element0 = new TextAreaElement();
		element0.setCode("zero");
		element0.setType('N');
		element0.setIsDecimal(Boolean.FALSE);
		em().persist(element0);
		
		final TextAreaElement element9 = new TextAreaElement();
		element9.setCode("neuf");
		element9.setType('N');
		element9.setIsDecimal(Boolean.TRUE);
		em().persist(element9);
		
		final TextAreaElement element42 = new TextAreaElement();
		element42.setCode("quarante_2");
		element42.setType('N');
		element42.setIsDecimal(Boolean.FALSE);
		em().persist(element42);
		
		final Layout detailsLayout = new Layout(3, 1);
		detailsLayout.addConstraint(0, 0, element9, 0);
		detailsLayout.addConstraint(1, 0, element42, 0);
		detailsLayout.addConstraint(2, 0, element0, 0);
		
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
		
		final User user = new User();
		user.setActive(Boolean.TRUE);
		user.setEmail("urd-sigmah+test@ideia.fr");
		user.setName("TestLastName");
		user.setFirstName("TestFirstName");
		user.setHashedPassword(authenticator.hashPassword("sigmah"));
		user.setLocale(Language.FR.getLocale());
		em().persist(user);
		
		final Country country = new Country();
		country.setName("Testry");
		country.setCodeISO("_t");
		country.setBounds(new Bounds());
		em().persist(country);
		
		final Project project = new Project();
		project.setProjectModel(model);
		project.setName("TestProject");
		project.setPhases(new ArrayList<Phase>());
		project.setStartDate(new Date());
		project.setOwner(user);
		project.setLastSchemaUpdate(new Date());
		project.setCountry(country);
		em().persist(project);
		
		this.projectId = project.getId();
		
		final Value value9 = new Value();
		value9.setValue("9");
		value9.setContainerId(project.getId());
		value9.setElement(element9);
		value9.setLastModificationAction('C');
		value9.setLastModificationDate(new Date());
		value9.setLastModificationUser(user);
		em().persist(value9);
		
		final Value value42 = new Value();
		value42.setValue("4.2");
		value42.setContainerId(project.getId());
		value42.setElement(element42);
		value42.setLastModificationAction('C');
		value42.setLastModificationDate(new Date());
		value42.setLastModificationUser(user);
		em().persist(value42);
		
		transaction.commit();
		
		this.entities = new Entity[] {
			value42,
			value9,
			project,
			banner,
			details,
			model,
			bannerLayout,
			detailsLayout,
			element42,
			element9,
			element0,
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
