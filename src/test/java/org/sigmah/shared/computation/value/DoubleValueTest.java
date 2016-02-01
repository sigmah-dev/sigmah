package org.sigmah.shared.computation.value;

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
        System.out.println("get");
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
        System.out.println("matchesConstraints");
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
        System.out.println("matchesConstraints");
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
        System.out.println("addTo");
        Assert.assertEquals(ComputedValues.from("32"), ComputedValues.from("17").addTo(ComputedValues.from("15")));
    }

    /**
     * Test of multiplyWith method, of class DoubleValue.
     */
    @Test
    public void testMultiplyWith() {
        System.out.println("multiplyWith");
        Assert.assertEquals(ComputedValues.from("32"), ComputedValues.from("16").multiplyWith(ComputedValues.from("2")));
    }

    /**
     * Test of divide method, of class DoubleValue.
     */
    @Test
    public void testDivide() {
        System.out.println("divide");
        Assert.assertEquals(ComputedValues.from("32"), ComputedValues.from("3").divide(ComputedValues.from("96")));
    }

    /**
     * Test of substractFrom method, of class DoubleValue.
     */
    @Test
    public void testSubstractFrom() {
        System.out.println("substractFrom");
        Assert.assertEquals(ComputedValues.from("32"), ComputedValues.from("10").substractFrom(ComputedValues.from("42")));
    }

    /**
     * Test of toString method, of class DoubleValue.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        Assert.assertEquals("32", ComputedValues.from("32").toString());
    }

}
