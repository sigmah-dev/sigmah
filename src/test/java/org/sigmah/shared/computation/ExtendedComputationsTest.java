package org.sigmah.shared.computation;

import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.sigmah.shared.dto.element.FlexibleElementDTO;

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
		System.out.println(formula.toString());
		Assert.assertFalse(formula.isBadFormula());
	}
	
	/**
	 * Test of parse method, of class Computations.
	 */
	@Test
	public void testParseSum() {
		List<FlexibleElementDTO> allElements = Collections.emptyList();
		Computation formula = Computations.parse("fundedProjects(Local partner project v2).sum(field56)", allElements);
		System.out.println(formula.toString());
		Assert.assertFalse(formula.isBadFormula());
	}
	
	/**
	 * Test of parse method, of class Computations.
	 */
	@Test
	public void testParseFullExample() {
		List<FlexibleElementDTO> allElements = Collections.emptyList();
		Computation formula = Computations.parse("fundingSources().sum(@contribution) - fundedProjects(Local partner project v2).sum(field56)", allElements);
		System.out.println(formula.toString());
		Assert.assertFalse(formula.isBadFormula());
	}
	
}
