package org.sigmah.shared.computation.value;

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

import org.junit.Test;
import org.junit.Assert;
import org.sigmah.shared.dto.element.ComputationElementDTO;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class DoubleValueTest {
    
    /**
     * Test of get method, of class DoubleValue.
     */
    @Test
    public void testGet() {
        DoubleValue instance = new DoubleValue(42.0);
        Double expResult = 42.0;
        Double result = instance.get();
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of matchesConstraints method, of class DoubleValue.
     */
    @Test
    public void testMatchesConstraints_ComputedValue_ComputedValue() {
        ComputedValue minimum = ComputedValues.from("5");
        ComputedValue maximum = ComputedValues.from("42");
        ComputedValue nullValue = ComputedValues.from((String) null, false);
        Assert.assertEquals(-1, new DoubleValue(0).matchesConstraints(minimum, maximum));
        Assert.assertEquals(-1, new DoubleValue(0).matchesConstraints(minimum, nullValue));
        Assert.assertEquals(0, new DoubleValue(5).matchesConstraints(minimum, maximum));
        Assert.assertEquals(0, new DoubleValue(10).matchesConstraints(minimum, maximum));
        Assert.assertEquals(0, new DoubleValue(10).matchesConstraints(minimum, nullValue));
        Assert.assertEquals(0, new DoubleValue(10).matchesConstraints(nullValue, nullValue));
        Assert.assertEquals(0, new DoubleValue(10).matchesConstraints(nullValue, maximum));
        Assert.assertEquals(0, new DoubleValue(42).matchesConstraints(minimum, maximum));
        Assert.assertEquals(1, new DoubleValue(50).matchesConstraints(minimum, maximum));
        Assert.assertEquals(1, new DoubleValue(50).matchesConstraints(nullValue, maximum));
    }

    /**
     * Test of matchesConstraints method, of class DoubleValue.
     */
    @Test
    public void testMatchesConstraints_ComputationElementDTO() {
        ComputationElementDTO element = new ComputationElementDTO();
        element.setMinimumValue("5");
        element.setMaximumValue("42");
        Assert.assertEquals(-1, new DoubleValue(0).matchesConstraints(element));
        Assert.assertEquals(0, new DoubleValue(5).matchesConstraints(element));
        Assert.assertEquals(0, new DoubleValue(10).matchesConstraints(element));
        Assert.assertEquals(0, new DoubleValue(42).matchesConstraints(element));
        Assert.assertEquals(1, new DoubleValue(50).matchesConstraints(element));
        
        element.setMinimumValue(null);
        Assert.assertEquals(0, new DoubleValue(0).matchesConstraints(element));
        Assert.assertEquals(0, new DoubleValue(5).matchesConstraints(element));
        Assert.assertEquals(0, new DoubleValue(10).matchesConstraints(element));
        
        element.setMinimumValue("5");
        element.setMaximumValue(null);
        Assert.assertEquals(0, new DoubleValue(10).matchesConstraints(element));
        Assert.assertEquals(0, new DoubleValue(42).matchesConstraints(element));
        Assert.assertEquals(0, new DoubleValue(50).matchesConstraints(element));
    }

    /**
     * Test of addTo method, of class DoubleValue.
     */
    @Test
    public void testAddTo() {
        Assert.assertEquals(ComputedValues.from("32"), ComputedValues.from("17").addTo(ComputedValues.from("15")));
    }

    /**
     * Test of multiplyWith method, of class DoubleValue.
     */
    @Test
    public void testMultiplyWith() {
        Assert.assertEquals(ComputedValues.from("32"), ComputedValues.from("16").multiplyWith(ComputedValues.from("2")));
    }

    /**
     * Test of divide method, of class DoubleValue.
     */
    @Test
    public void testDivide() {
        Assert.assertEquals(ComputedValues.from("32"), ComputedValues.from("3").divide(ComputedValues.from("96")));
    }

    /**
     * Test of substractFrom method, of class DoubleValue.
     */
    @Test
    public void testSubstractFrom() {
        Assert.assertEquals(ComputedValues.from("32"), ComputedValues.from("10").substractFrom(ComputedValues.from("42")));
    }

    /**
     * Test of toString method, of class DoubleValue.
     */
    @Test
    public void testToString() {
        Assert.assertEquals("32", ComputedValues.from("32").toString());
    }

}
