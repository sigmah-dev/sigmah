package org.sigmah.shared.dto.element;

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
