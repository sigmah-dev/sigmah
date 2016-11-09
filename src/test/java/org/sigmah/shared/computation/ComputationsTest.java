package org.sigmah.shared.computation;

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

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.sigmah.shared.computation.dependency.Dependency;
import org.sigmah.shared.computation.dependency.SingleDependency;
import org.sigmah.shared.computation.value.ComputationError;
import org.sigmah.shared.computation.value.ComputedValue;
import org.sigmah.shared.computation.value.ComputedValues;
import org.sigmah.shared.computation.value.DoubleValue;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.TextAreaElementDTO;

/**
 * Test of Computations.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class ComputationsTest {

	/**
	 * Test of parse method, of class Computations.
	 */
	@Test
	public void testParseAddSub() {
		List<FlexibleElementDTO> allElements = Collections.emptyList();
		final Computation result = Computations.parse("12 + 3.14 - 4", allElements);
		
		Assert.assertEquals("Computation was not parsed correctly.", "12 + 3.14 - 4", result.toString());
		Assert.assertEquals("Computation result is incorrect.", new DoubleValue(12.0 + 3.14 - 4.0), result.computeValue(null));
	}
	
	/**
	 * Test of parse method, of class Computations.
	 */
	@Test
	public void testParseMultDiv() {
		List<FlexibleElementDTO> allElements = Collections.emptyList();
		final Computation result = Computations.parse("12 + 3.14 * 4 + 8 / 2", allElements);
		
		Assert.assertEquals("Computation was not parsed correctly.", "12 + 3.14 × 4 + 8 ÷ 2", result.toString());
		Assert.assertEquals("Computation result is incorrect.", new DoubleValue(12.0 + 3.14 * 4.0 + 8.0 / 2.0), result.computeValue(null));
	}
	
	/**
	 * Test of parse method, of class Computations.
	 */
	@Test
	public void testParseUnary() {
		List<FlexibleElementDTO> allElements = Collections.emptyList();
		final Computation result = Computations.parse("12 * -3.14 / 4", allElements);
		
		Assert.assertEquals("Computation was not parsed correctly.", "12 × (-3.14 ÷ 4)", result.toString());
		Assert.assertEquals("Computation result is incorrect.", new DoubleValue(12.0 * -3.14 / 4.0), result.computeValue(null));
	}
	
	/**
	 * Test of parse method, of class Computations.
	 */
	@Test
	public void testParseContext() {
		List<FlexibleElementDTO> allElements = Collections.emptyList();
		final Computation result = Computations.parse("(12 + 3.14) * 4", allElements);
		
		Assert.assertEquals("Computation was not parsed correctly.", "(12 + 3.14) × 4", result.toString());
		Assert.assertEquals("Computation result is incorrect.", new DoubleValue((12.0 + 3.14) * 4.0), result.computeValue(null));
	}
	
	/**
	 * Test of parse method, of class Computations.
	 */
	@Test
	public void testParseVariable() {
		final HashMap<Dependency, ComputedValue> values = new HashMap<>();
		values.put(new SingleDependency(9), new DoubleValue(9.0));
		values.put(new SingleDependency(42), new DoubleValue(42.0));
		
		final Computation result = Computations.parse("12 * $9 + 3.14 * quarante_2", getAllElements());
		
		Assert.assertEquals("Computation was not parsed correctly.", "12 × $9 + 3.14 × $42", result.toString());
		Assert.assertEquals("Computation result is incorrect.", new DoubleValue(12.0 * 9.0 + 3.14 * 42.0), result.computeValue(values));
	}
	
	/**
	 * Test of parse method, of class Computations.
	 */
	@Test
	public void testParseFunction() {
		final Computation result = Computations.parse("min(neuf, quarante_2) / max(neuf, quarante_2)", getAllElements());
		Assert.assertEquals("Computation was not parsed correctly.", "BAD_FORMULA", result.toString());
	}
	
	/**
	 * Test of parse method, of class Computations.
	 */
	@Test
	public void testDivisionError() {
		final HashMap<Dependency, ComputedValue> values = new HashMap<>();
		values.put(new SingleDependency(9), new DoubleValue(0.0));
		
		final Computation result = Computations.parse("(12 / $9 + 3.14) * 2", getAllElements());
		
		Assert.assertEquals("Computation was not parsed correctly.", "(12 ÷ $9 + 3.14) × 2", result.toString());
		Assert.assertEquals("Computation result is incorrect.", ComputationError.DIVISON_BY_ZERO, result.computeValue(values));
	}
	
	/**
	 * Test of parse method, of class Computations.
	 */
	@Test
	public void testReferenceError() {
		final HashMap<Dependency, ComputedValue> values = new HashMap<>();
		values.put(new SingleDependency(9), new DoubleValue(9.0));
		
		final Computation result = Computations.parse("12 * $10 + 3.14 * quarante_3", getAllElements());
		
		Assert.assertEquals("Computation was not parsed correctly.", "12 × $10 + 3.14 × quarante_3", result.toString());
		Assert.assertEquals("Computation result is incorrect.", ComputationError.BAD_REFERENCE, result.computeValue(values));
	}
	
	/**
	 * Test of parse method, of class Computations.
	 */
	@Test
	public void testBadValue() {
		final HashMap<Dependency, ComputedValue> values = new HashMap<>();
		values.put(new SingleDependency(9), ComputedValues.from("9,1"));
		values.put(new SingleDependency(42), ComputedValues.from("quarante deux"));
		
		final Computation result = Computations.parse("12 * $9 + 3.14 * quarante_2", getAllElements());
		
		Assert.assertEquals("Computation was not parsed correctly.", "12 × $9 + 3.14 × $42", result.toString());
		Assert.assertEquals("Computation result is incorrect.", ComputationError.BAD_VALUE, result.computeValue(values));
	}
    
    /**
	 * Test of parse method, of class Computations.
	 */
	@Test
	public void testBadFormula() {
		final HashMap<Integer, ComputedValue> values = new HashMap<>();
		values.put(9, new DoubleValue(9.0));
		values.put(42, new DoubleValue(42.0));
		
		Computation badFunction = Computations.parse("neuf(neuf, quarante_2) / quarante_2(neuf, quarante_2)", getAllElements());
		Assert.assertEquals("Computation was not parsed correctly.", "BAD_FORMULA", badFunction.toString());
        Assert.assertTrue(badFunction.isBadFormula());
        
        badFunction = Computations.parse("9 /", getAllElements());
        Assert.assertEquals("Computation was not parsed correctly.", "BAD_FORMULA", badFunction.toString());
        Assert.assertTrue(badFunction.isBadFormula());
        
        badFunction = Computations.parse("9 + + 2", getAllElements());
        Assert.assertEquals("Computation was not parsed correctly.", "BAD_FORMULA", badFunction.toString());
        Assert.assertTrue(badFunction.isBadFormula());
        
        badFunction = Computations.parse("9 9 + 2", getAllElements());
        Assert.assertEquals("Computation was not parsed correctly.", "BAD_FORMULA", badFunction.toString());
        Assert.assertTrue(badFunction.isBadFormula());
        
        badFunction = Computations.parse("9 +- 2", getAllElements());
        Assert.assertEquals("Computation was not parsed correctly.", "BAD_FORMULA", badFunction.toString());
        Assert.assertTrue(badFunction.isBadFormula());
        
	}
	
	/**
	 * Test of toHumanReadableString method, of class Computation.
	 */
	@Test
	public void testHumanReadableFormat() {
		final Computation result = Computations.parse("12 * $9 + 3.14 * quarante_2", getAllElements());
		Assert.assertEquals("Computation was not parsed correctly.", "12 × neuf + 3.14 × quarante_2", result.toHumanReadableString());
	}
	
	/**
	 * Test of computeValueWithResolver method, of class Computation.
	 */
	@Test
	public void testComputeWithValueResolver() {
		final Computation result = Computations.parse("12 * $9 + 3.14 * quarante_2", getAllElements());
		Assert.assertEquals("Computation was not parsed correctly.", "12 × $9 + 3.14 × $42", result.toString());
		
		final double[] values = new double[2];
		
		result.computeValueWithResolver(1, new ValueResolver() {

			@Override
			public void resolve(Collection<Dependency> dependencies, int containerId, AsyncCallback<Map<Dependency, ComputedValue>> onResult) {
				Assert.assertEquals("Elements size is incorrect.", 2, dependencies.size());
				
				final HashMap<Dependency, ComputedValue> map = new HashMap<>();
				
				for (final Dependency dependency : dependencies) {
					final SingleDependency singleDependency = (SingleDependency) dependency;
					final FlexibleElementDTO element = singleDependency.getFlexibleElement();
					
					final double value = Math.random() * 50;
					map.put(dependency, new DoubleValue(value));
					
					if (element.getId() == 9) {
						values[0] = value;
					} else if(element.getId() == 42) {
						values[1] = value;
					}
				}
				
				onResult.onSuccess(map);
			}
		}, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				Assert.fail();
			}

			@Override
			public void onSuccess(String result) {
				Assert.assertEquals("Computation result is incorrect.", new DoubleValue(12 * values[0] + 3.14 * values[1]).toString(), result);
			}
		});
	}
	
	/**
	 * Creates the list of elements used in the tests.
	 * 
	 * @return List of flexible elements for the tests.
	 */
	private List<FlexibleElementDTO> getAllElements() {
		List<FlexibleElementDTO> allElements = new ArrayList<FlexibleElementDTO>();
		
		final TextAreaElementDTO element9 = new TextAreaElementDTO();
		element9.setId(9);
		element9.setCode("neuf");
		element9.setType('N');
		element9.setIsDecimal(Boolean.TRUE);
		allElements.add(element9);
		
		final TextAreaElementDTO element11 = new TextAreaElementDTO();
		element11.setId(11);
		element11.setCode("onze");
		element11.setType('N');
		element11.setIsDecimal(Boolean.TRUE);
		allElements.add(element11);
		
		final TextAreaElementDTO element24 = new TextAreaElementDTO();
		element24.setId(24);
		element24.setCode("vingt_4");
		element24.setType('N');
		element24.setIsDecimal(Boolean.FALSE);
		allElements.add(element24);
		
		final TextAreaElementDTO element42 = new TextAreaElementDTO();
		element42.setId(42);
		element42.setCode("quarante_2");
		element42.setType('N');
		element42.setIsDecimal(Boolean.FALSE);
		allElements.add(element42);
		
		final TextAreaElementDTO element60 = new TextAreaElementDTO();
		element60.setId(60);
		element60.setCode("soixante");
		element60.setType('N');
		element60.setIsDecimal(Boolean.TRUE);
		allElements.add(element60);
		
		return allElements;
	}

	/**
	 * Test of formatRuleForEdition method, of class Computations.
	 */
	@Test
	public void testFormatRuleForEdition() {
		Assert.assertNull(Computations.formatRuleForEdition(null, null));
		Assert.assertNull(Computations.formatRuleForEdition("	", null));
		Assert.assertEquals("onze + vingt_4", Computations.formatRuleForEdition("$11 + $24", getAllElements()));
	}

	/**
	 * Test of formatRuleForServer method, of class Computations.
	 */
	@Test
	public void testFormatRuleForServer() {
		Assert.assertNull(Computations.formatRuleForServer(null, null));
		Assert.assertNull(Computations.formatRuleForServer("  ", null));
		Assert.assertEquals("$11 + $24", Computations.formatRuleForServer("onze + vingt_4", getAllElements()));
	}
	
	/**
	 * Test of formatRuleForServer method, of class Computations.
	 */
	@Test
	public void testFormulaWithFrenchCharacters() {
		final TextAreaElementDTO element42 = new TextAreaElementDTO();
		element42.setId(42);
		element42.setCode("utilisé");
		element42.setType('N');
		element42.setIsDecimal(Boolean.TRUE);
		
		final TextAreaElementDTO element60 = new TextAreaElementDTO();
		element60.setId(60);
		element60.setCode("reçu");
		element60.setType('N');
		element60.setIsDecimal(Boolean.TRUE);
		
		final String rule = Computations.formatRuleForServer("(utilisé / reçu) * 100", Arrays.<FlexibleElementDTO>asList(element42, element60));
		Assert.assertEquals("($42 ÷ $60) × 100", rule);
	}
	
	/**
	 * Test of formatRuleForServer method, of class Computations.
	 */
	@Test
	public void testFormulaWithJapaneseCharacters() {
		final TextAreaElementDTO element42 = new TextAreaElementDTO();
		element42.setId(42);
		element42.setCode("使った予算");
		element42.setType('N');
		element42.setIsDecimal(Boolean.TRUE);
		
		final TextAreaElementDTO element60 = new TextAreaElementDTO();
		element60.setId(60);
		element60.setCode("頂いた予算");
		element60.setType('N');
		element60.setIsDecimal(Boolean.TRUE);
		
		final String rule = Computations.formatRuleForServer("(使った予算 / 頂いた予算) * 100", Arrays.<FlexibleElementDTO>asList(element42, element60));
		Assert.assertEquals("($42 ÷ $60) × 100", rule);
	}
	
}
