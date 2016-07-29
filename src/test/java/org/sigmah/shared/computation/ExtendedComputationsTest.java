package org.sigmah.shared.computation;

import org.junit.Assert;

/**
 * Test of the new functionalities of computations.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class ExtendedComputationsTest {
	
	/**
	 * Test of parse method, of class Computations.
	 */
	public void testParseAverage() {
		Computation formula = Computations.parse("fundingSources().avg(@contribution)", null);
		Assert.assertFalse(formula.isBadFormula());
	}
	
	/**
	 * Test of parse method, of class Computations.
	 */
	public void testParseSum() {
		Computation formula = Computations.parse("fundedProjects(Local partner project v2).sum(field56)", null);
		Assert.assertFalse(formula.isBadFormula());
	}
	
	/**
	 * Test of parse method, of class Computations.
	 */
	public void testParseFullExample() {
		Computation formula = Computations.parse("fundingSources().sum(@contribution) - fundedProjects(Local partner project v2).sum(field56)", null);
		Assert.assertFalse(formula.isBadFormula());
	}
	
}
