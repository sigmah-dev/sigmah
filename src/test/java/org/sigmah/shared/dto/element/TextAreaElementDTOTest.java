package org.sigmah.shared.dto.element;

import java.util.Calendar;
import org.junit.Test;
import static org.junit.Assert.*;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.dto.referential.TextAreaType;

/**
 * Test class for <code>TextAreaElementDTO</code>.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class TextAreaElementDTOTest {
	
	/**
	 * Test of getEntityName method, of class TextAreaElementDTO.
	 */
	@Test
	public void testGetEntityName() {
		TextAreaElementDTO instance = new TextAreaElementDTO();
		assertEquals(TextAreaElementDTO.ENTITY_NAME, instance.getEntityName());
	}

	/**
	 * Test of isCorrectRequiredValue method, of class TextAreaElementDTO.
	 */
	@Test
	public void testIsCorrectRequiredValueNull() {
		TextAreaElementDTO instance = new TextAreaElementDTO();
		assertFalse(instance.isCorrectRequiredValue(null));
	}
	
	/**
	 * Test of isCorrectRequiredValue method, of class TextAreaElementDTO.
	 */
	@Test
	public void testIsCorrectRequiredValueEmpty() {
		TextAreaElementDTO instance = new TextAreaElementDTO();
		final ValueResult valueResult = new ValueResult();
		assertFalse(instance.isCorrectRequiredValue(valueResult));
	}
	
	/**
	 * Test of isCorrectRequiredValue method, of class TextAreaElementDTO.
	 */
	@Test
	public void testIsCorrectRequiredValue_Number() {
		TextAreaElementDTO instance = new TextAreaElementDTO();
		instance.setType(TextAreaType.NUMBER.getCode());
		
		final ValueResult valueResult = new ValueResult();
		valueResult.setValueObject("42");
		
		assertTrue(instance.isCorrectRequiredValue(valueResult));
		
		instance.setMinValue(50L);
		assertFalse(instance.isCorrectRequiredValue(valueResult));
		
		instance.setMinValue(20L);
		assertTrue(instance.isCorrectRequiredValue(valueResult));
		
		instance.setMinValue(null);
		instance.setMaxValue(50L);
		assertTrue(instance.isCorrectRequiredValue(valueResult));
		
		instance.setMaxValue(20L);
		assertFalse(instance.isCorrectRequiredValue(valueResult));
		
		instance.setMinValue(30L);
		instance.setMaxValue(50L);
		assertTrue(instance.isCorrectRequiredValue(valueResult));
		
		instance.setMinValue(50L);
		instance.setMaxValue(60L);
		assertFalse(instance.isCorrectRequiredValue(valueResult));
		
		instance.setMinValue(10L);
		instance.setMaxValue(20L);
		assertFalse(instance.isCorrectRequiredValue(valueResult));
	}
	
	/**
	 * Test of isCorrectRequiredValue method, of class TextAreaElementDTO.
	 */
	@Test
	public void testIsCorrectRequiredValue_Date() {
		TextAreaElementDTO instance = new TextAreaElementDTO();
		instance.setType(TextAreaType.DATE.getCode());
		
		final Calendar calendar = Calendar.getInstance();
		final long now = calendar.getTimeInMillis();
		
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		final long yesterday = calendar.getTimeInMillis();
		
		calendar.add(Calendar.DAY_OF_MONTH, 2);
		final long tomorrow = calendar.getTimeInMillis();
		
		final ValueResult valueResult = new ValueResult();
		valueResult.setValueObject(Long.toString(now));
		
		assertTrue(instance.isCorrectRequiredValue(valueResult));
		
		instance.setMinValue(tomorrow);
		assertFalse(instance.isCorrectRequiredValue(valueResult));
		
		instance.setMinValue(yesterday);
		assertTrue(instance.isCorrectRequiredValue(valueResult));
		
		instance.setMinValue(null);
		instance.setMaxValue(tomorrow);
		assertTrue(instance.isCorrectRequiredValue(valueResult));
		
		instance.setMaxValue(yesterday);
		assertFalse(instance.isCorrectRequiredValue(valueResult));
		
		instance.setMinValue(yesterday);
		instance.setMaxValue(tomorrow);
		assertTrue(instance.isCorrectRequiredValue(valueResult));
		
		instance.setMinValue(tomorrow);
		instance.setMaxValue(tomorrow);
		assertFalse(instance.isCorrectRequiredValue(valueResult));
		
		instance.setMinValue(yesterday);
		instance.setMaxValue(yesterday);
		assertFalse(instance.isCorrectRequiredValue(valueResult));
	}
	
	/**
	 * Test of isCorrectRequiredValue method, of class TextAreaElementDTO.
	 */
	@Test
	public void testIsCorrectRequiredValue_String_Paragraph() {
		TextAreaElementDTO instance = new TextAreaElementDTO();
		instance.setType(TextAreaType.PARAGRAPH.getCode());
		
		final ValueResult valueResult = new ValueResult();
		valueResult.setValueObject("azerty");
		
		assertTrue(instance.isCorrectRequiredValue(valueResult));
		
		instance.setLength(42);
		assertTrue(instance.isCorrectRequiredValue(valueResult));
		
		instance.setLength(2);
		assertFalse(instance.isCorrectRequiredValue(valueResult));
	}
	
	/**
	 * Test of isCorrectRequiredValue method, of class TextAreaElementDTO.
	 */
	@Test
	public void testIsCorrectRequiredValue_String_Text() {
		TextAreaElementDTO instance = new TextAreaElementDTO();
		instance.setType(TextAreaType.TEXT.getCode());
		
		final ValueResult valueResult = new ValueResult();
		valueResult.setValueObject("azerty");
		
		assertTrue(instance.isCorrectRequiredValue(valueResult));
		
		instance.setLength(42);
		assertTrue(instance.isCorrectRequiredValue(valueResult));
		
		instance.setLength(2);
		assertFalse(instance.isCorrectRequiredValue(valueResult));
	}
	
	/**
	 * Test of isCorrectRequiredValue method, of class TextAreaElementDTO.
	 */
	@Test
	public void testIsCorrectRequiredValue_String_Null() {
		TextAreaElementDTO instance = new TextAreaElementDTO();
		
		final ValueResult valueResult = new ValueResult();
		valueResult.setValueObject("azerty");
		
		assertTrue(instance.isCorrectRequiredValue(valueResult));
		
		instance.setLength(42);
		assertTrue(instance.isCorrectRequiredValue(valueResult));
		
		instance.setLength(2);
		assertFalse(instance.isCorrectRequiredValue(valueResult));
	}

}
