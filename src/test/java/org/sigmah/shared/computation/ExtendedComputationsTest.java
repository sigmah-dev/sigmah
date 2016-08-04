package org.sigmah.shared.computation;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.sigmah.shared.computation.dependency.CollectionDependency;
import org.sigmah.shared.computation.dependency.ContributionDependency;
import org.sigmah.shared.computation.dependency.Dependency;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.TextAreaElementDTO;

/**
 * Test of the new functionalities of computations.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class ExtendedComputationsTest {
	
	/**
	 * Test of parse method, of class Computations.
	 */
	@Test
	public void testParseAverage() {
		List<FlexibleElementDTO> allElements = Collections.emptyList();
		Computation formula = Computations.parse("fundingSources().avg(@contribution)", allElements);
		Assert.assertFalse(formula.isBadFormula());
		Assert.assertEquals("fundingSources().avg(@contribution)", formula.toString());
		
		final Set<Dependency> dependencies = formula.getDependencies();
		Assert.assertEquals(1, dependencies.size());
		
		for (final Dependency dependency : formula.getDependencies()) {
			Assert.assertTrue(dependency.isResolved());
			Assert.assertTrue(dependency instanceof ContributionDependency);
		}
	}
	
	/**
	 * Test of parse method, of class Computations.
	 */
	@Test
	public void testParseSum() {
		List<FlexibleElementDTO> allElements = Collections.emptyList();
		Computation formula = Computations.parse("fundedProjects(Local partner project v2).sum(field56)", allElements);
		Assert.assertFalse(formula.isBadFormula());
		Assert.assertEquals("fundedProjects(Local partner project v2).sum(field56)", formula.toString());
		
		final Set<Dependency> dependencies = formula.getDependencies();
		Assert.assertEquals(1, dependencies.size());
		
		for (final Dependency dependency : formula.getDependencies()) {
			Assert.assertTrue(dependency instanceof CollectionDependency);
			Assert.assertFalse(dependency.isResolved());
			
			final CollectionDependency collectionDependency = (CollectionDependency) dependency;
			
			final TextAreaElementDTO element56 = new TextAreaElementDTO();
			element56.setId(56);
			element56.setCode("new_name_56");
			element56.setType('N');
			element56.setIsDecimal(Boolean.TRUE);
			
			collectionDependency.setFlexibleElement(element56);
			Assert.assertTrue(dependency.isResolved());
		}
		
		Assert.assertEquals("fundedProjects(Local partner project v2).sum($56%new_name_56%NUMBER)", formula.toString());
		Assert.assertEquals("fundedProjects(Local partner project v2).sum(new_name_56)", formula.toHumanReadableString());
	}
	
	/**
	 * Test of parse method, of class Computations.
	 */
	@Test
	public void testParseFullExample() {
		List<FlexibleElementDTO> allElements = Collections.emptyList();
		Computation formula = Computations.parse("fundingSources().sum(@contribution) - fundedProjects(Local partner project v2).sum(field56)", allElements);
		Assert.assertFalse(formula.isBadFormula());
		Assert.assertEquals("fundingSources().sum(@contribution) - fundedProjects(Local partner project v2).sum(field56)", formula.toString());
	}
	
	/**
	 * Test of parse method, of class Computations.
	 */
	@Test
	public void testParseContributions() {
		List<FlexibleElementDTO> allElements = Collections.emptyList();
		Computation formula = Computations.parse("fundingSources(Local partner project v2).avg(@contribution)", allElements);
		Assert.assertFalse(formula.isBadFormula());
		Assert.assertEquals("fundingSources(Local partner project v2).avg(@contribution)", formula.toString());
		
		final Set<Dependency> dependencies = formula.getDependencies();
		Assert.assertEquals(1, dependencies.size());
		
		for (final Dependency dependency : formula.getDependencies()) {
			Assert.assertFalse(dependency.isResolved());
			Assert.assertTrue(dependency instanceof ContributionDependency);
			
			final ContributionDependency contributionDependency = (ContributionDependency) dependency;
			contributionDependency.setProjectModelId(42);
			
			Assert.assertTrue(dependency.isResolved());
		}
		
		Assert.assertEquals("fundingSources($42%Local partner project v2).avg(@contribution)", formula.toString());
		Assert.assertEquals("fundingSources(Local partner project v2).avg(@contribution)", formula.toHumanReadableString());
	}
	
	/**
	 * Test of parse method, of class Computations.
	 */
	@Test
	public void testParseCollectionFromId() {
		List<FlexibleElementDTO> allElements = Collections.emptyList();
		Computation formula = Computations.parse("fundedProjects(Local partner project v2).sum($56%field56%NUMBER)", allElements);
		Assert.assertFalse(formula.isBadFormula());
		Assert.assertEquals("fundedProjects(Local partner project v2).sum($56%field56%NUMBER)", formula.toString());
		
		final Set<Dependency> dependencies = formula.getDependencies();
		Assert.assertEquals(1, dependencies.size());
		
		for (final Dependency dependency : formula.getDependencies()) {
			Assert.assertTrue(dependency instanceof CollectionDependency);
			Assert.assertTrue(dependency.isResolved());
		}
		
		Assert.assertEquals("fundedProjects(Local partner project v2).sum($56%field56%NUMBER)", formula.toString());
		Assert.assertEquals("fundedProjects(Local partner project v2).sum(field56)", formula.toHumanReadableString());
	}
	
	/**
	 * Test of parse method, of class Computations.
	 */
	@Test
	public void testParseContributionFromId() {
		List<FlexibleElementDTO> allElements = Collections.emptyList();
		Computation formula = Computations.parse("fundedProjects($42%Local partner project v2).sum(@contribution)", allElements);
		Assert.assertFalse(formula.isBadFormula());
		Assert.assertEquals("fundedProjects($42%Local partner project v2).sum(@contribution)", formula.toString());
		
		final Set<Dependency> dependencies = formula.getDependencies();
		Assert.assertEquals(1, dependencies.size());
		
		for (final Dependency dependency : formula.getDependencies()) {
			Assert.assertTrue(dependency instanceof ContributionDependency);
			Assert.assertTrue(dependency.isResolved());
		}
		
		Assert.assertEquals("fundedProjects($42%Local partner project v2).sum(@contribution)", formula.toString());
		Assert.assertEquals("fundedProjects(Local partner project v2).sum(@contribution)", formula.toHumanReadableString());
	}
	
}
