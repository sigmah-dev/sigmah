package org.sigmah.shared.util;

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
