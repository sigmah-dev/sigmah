package org.sigmah.shared.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.Assert;
import org.junit.Test;
import org.sigmah.shared.dto.element.ComputationElementDTO;

/**
 * Test class for <code>Collections</code>.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class CollectionsTest {

    /**
     * Test of join method, of class Collections.
     */
    @Test
    public void testJoin_Collection_String() {
        Assert.assertEquals("", Collections.join(Arrays.<Object>asList(), ", "));
        Assert.assertEquals("origin", Collections.join(java.util.Collections.singleton("origin"), ", "));
        Assert.assertEquals("Azerty, Querty", Collections.join(Arrays.asList("Azerty", "Querty"), ", "));
        Assert.assertEquals("Azerty, Querty, Bepo", Collections.join(Arrays.asList("Azerty", "Querty", "Bepo"), ", "));
    }
    
    /**
     * Test of join method, of class Collections.
     */
    @Test
    public void testJoin_3args() {
        final ComputationElementDTO element1 = new ComputationElementDTO();
        element1.setRule("firstRule");
        
        final ComputationElementDTO element2 = new ComputationElementDTO();
        element2.setRule("secondRule");
        
        Assert.assertEquals("firstRule, secondRule", Collections.join(Arrays.asList(element1, element2), new Collections.Mapper<ComputationElementDTO, String>() {
            
            @Override
            public String forEntry(ComputationElementDTO entry) {
                return entry.getRule();
            }
        }, ", "));
    }

    /**
     * Test of containsOneOf method, of class Collections.
     */
    @Test
    public void testContainsOneOf() {
        final HashSet<String> haystack = new HashSet<String>();
        haystack.add("A");
        haystack.add("B");
        haystack.add("C");
        
        Assert.assertFalse(Collections.containsOneOf(haystack, Arrays.asList("D")));
        Assert.assertTrue(Collections.containsOneOf(haystack, Arrays.asList("B")));
        Assert.assertTrue(Collections.containsOneOf(haystack, Arrays.asList("B", "C")));
        Assert.assertTrue(Collections.containsOneOf(haystack, Arrays.asList("D", "C")));
        Assert.assertTrue(Collections.containsOneOf(haystack, haystack));
    }

    /**
     * Test of map method, of class Collections.
     */
    @Test
    public void testMap() {
        Assert.assertEquals(new ArrayList<String>(Arrays.asList("12", "42", "94")), Collections.map(Arrays.asList(12, 42, 94), new Collections.Mapper<Integer, String>() {
            
            @Override
            public String forEntry(Integer entry) {
                return entry.toString();
            }
        }));
        
        Assert.assertEquals(new ArrayList<Double>(Arrays.asList(12.0, 42.0, 94.0)), Collections.map(Arrays.asList(12, 42, 94), new Collections.Mapper<Integer, Double>() {
            
            @Override
            public Double forEntry(Integer entry) {
                return Double.valueOf(entry);
            }
        }));
    }

}
