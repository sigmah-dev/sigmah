package org.sigmah.shared.computation;

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
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
		System.out.println("parseAddSub");
		
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
		System.out.println("parseMultDiv");
		
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
		System.out.println("parseUnary");
		
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
		System.out.println("parseContext");
		
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
		System.out.println("parseVariable");
		
		final HashMap<Integer, ComputedValue> values = new HashMap<>();
		values.put(9, new DoubleValue(9.0));
		values.put(42, new DoubleValue(42.0));
		
		final Computation result = Computations.parse("12 * $9 + 3.14 * quarante_2", getAllElements());
		
		Assert.assertEquals("Computation was not parsed correctly.", "12 × $9 + 3.14 × $42", result.toString());
		Assert.assertEquals("Computation result is incorrect.", new DoubleValue(12.0 * 9.0 + 3.14 * 42.0), result.computeValue(values));
	}
	
	/**
	 * Test of parse method, of class Computations.
	 */
	@Test
	public void testParseFunction() {
		System.out.println("parseFunction");
		
		final HashMap<Integer, ComputedValue> values = new HashMap<>();
		values.put(9, new DoubleValue(9.0));
		values.put(42, new DoubleValue(42.0));
		
		final Computation result = Computations.parse("min(neuf, quarante_2) / max(neuf, quarante_2)", getAllElements());
		Assert.assertEquals("Computation was not parsed correctly.", "BAD_FORMULA", result.toString());
	}
	
	/**
	 * Test of parse method, of class Computations.
	 */
	@Test
	public void testDivisionError() {
		System.out.println("divisionError");
		
		final HashMap<Integer, ComputedValue> values = new HashMap<>();
		values.put(9, new DoubleValue(0.0));
		
		final Computation result = Computations.parse("(12 / $9 + 3.14) * 2", getAllElements());
		
		Assert.assertEquals("Computation was not parsed correctly.", "(12 ÷ $9 + 3.14) × 2", result.toString());
		Assert.assertEquals("Computation result is incorrect.", ComputationError.DIVISON_BY_ZERO, result.computeValue(values));
	}
	
	/**
	 * Test of parse method, of class Computations.
	 */
	@Test
	public void testReferenceError() {
		System.out.println("referenceError");
		
		final HashMap<Integer, ComputedValue> values = new HashMap<>();
		values.put(9, new DoubleValue(9.0));
		
		final Computation result = Computations.parse("12 * $10 + 3.14 * quarante_3", getAllElements());
		
		Assert.assertEquals("Computation was not parsed correctly.", "12 × $10 + 3.14 × quarante_3", result.toString());
		Assert.assertEquals("Computation result is incorrect.", ComputationError.BAD_REFERENCE, result.computeValue(values));
	}
	
	/**
	 * Test of parse method, of class Computations.
	 */
	@Test
	public void testBadValue() {
		System.out.println("badValue");
		
		final HashMap<Integer, ComputedValue> values = new HashMap<>();
		values.put(9, ComputedValues.from("9,1"));
		values.put(42, ComputedValues.from("quarante deux"));
		
		final Computation result = Computations.parse("12 * $9 + 3.14 * quarante_2", getAllElements());
		
		Assert.assertEquals("Computation was not parsed correctly.", "12 × $9 + 3.14 × $42", result.toString());
		Assert.assertEquals("Computation result is incorrect.", ComputationError.BAD_VALUE, result.computeValue(values));
	}
    
    /**
	 * Test of parse method, of class Computations.
	 */
	@Test
	public void testBadFormula() {
		System.out.println("badFormula");
		
		final HashMap<Integer, ComputedValue> values = new HashMap<>();
		values.put(9, new DoubleValue(9.0));
		values.put(42, new DoubleValue(42.0));
		
		final Computation badFunction = Computations.parse("neuf(neuf, quarante_2) / quarante_2(neuf, quarante_2)", getAllElements());
		Assert.assertEquals("Computation was not parsed correctly.", "BAD_FORMULA", badFunction.toString());
	}
	
	/**
	 * Test of toHumanReadableString method, of class Computation.
	 */
	@Test
	public void testHumanReadableFormat() {
		System.out.println("humanReadableFormat");
		
		final Computation result = Computations.parse("12 * $9 + 3.14 * quarante_2", getAllElements());
		Assert.assertEquals("Computation was not parsed correctly.", "12 × neuf + 3.14 × quarante_2", result.toHumanReadableString());
	}
	
	/**
	 * Test of computeValueWithResolver method, of class Computation.
	 */
	@Test
	public void testComputeWithValueResolver() {
		System.out.println("computeValueWithResolver");
		
		final Computation result = Computations.parse("12 * $9 + 3.14 * quarante_2", getAllElements());
		Assert.assertEquals("Computation was not parsed correctly.", "12 × $9 + 3.14 × $42", result.toString());
		
		final double[] values = new double[2];
		
		result.computeValueWithResolver(1, new ValueResolver() {

			@Override
			public void resolve(Collection<FlexibleElementDTO> elements, int containerId, AsyncCallback<Map<Integer, ComputedValue>> onResult) {
				Assert.assertEquals("Elements size is incorrect.", 2, elements.size());
				
				final HashMap<Integer, ComputedValue> map = new HashMap<>();
				
				for (final FlexibleElementDTO element : elements) {
					final double value = Math.random() * 50;
					map.put(element.getId(), new DoubleValue(value));
					
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
				Assert.assertEquals("Computation result is incorrect.", Double.toString(12 * values[0] + 3.14 * values[1]), result);
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
	
}
