package org.sigmah.shared.dto.element;

import com.google.gwt.event.shared.HandlerManager;
import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.computation.Computation;
import org.sigmah.shared.computation.value.DoubleValue;
import org.sigmah.shared.dto.PhaseModelDTO;
import org.sigmah.shared.dto.ProjectBannerDTO;
import org.sigmah.shared.dto.ProjectDetailsDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.element.event.ValueEvent;
import org.sigmah.shared.dto.element.event.ValueHandler;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class ComputationElementDTOTest {
    

    /**
     * Test of isCorrectRequiredValue method, of class ComputationElementDTO.
     */
    @Test
    public void testIsCorrectRequiredValue() {
        System.out.println("isCorrectRequiredValue");
        ValueResult result = new ValueResult();
        ComputationElementDTO instance = new ComputationElementDTO();
        instance.setMinimumValue("8");
        instance.setMaximumValue("12");
        
        assertFalse(instance.isCorrectRequiredValue(result));
        
        result.setValueObject("5");
        assertFalse(instance.isCorrectRequiredValue(result));
        
        result.setValueObject("9");
        assertTrue(instance.isCorrectRequiredValue(result));
        
        result.setValueObject("24");
        assertFalse(instance.isCorrectRequiredValue(result));
    }

    /**
     * Test of getEntityName method, of class ComputationElementDTO.
     */
    @Test
    public void testGetEntityName() {
        System.out.println("getEntityName");
        ComputationElementDTO instance = new ComputationElementDTO();
        String result = instance.getEntityName();
        assertEquals(ComputationElementDTO.ENTITY_NAME, result);
    }

    /**
     * Test of fireValueEvent method, of class ComputationElementDTO.
     */
    @Test
    public void testFireValueEvent() {
        System.out.println("fireValueEvent");
        String value = "42";
        ComputationElementDTO instance = new ComputationElementDTO();
        instance.handlerManager = new HandlerManager(instance);
        
        final boolean[] success = {false};
        
        instance.addValueHandler(new ValueHandler() {
            @Override
            public void onValueChange(ValueEvent event) {
                success[0] = "42".equals(event.getSingleValue());
            }
        });
        
        instance.fireValueEvent(value);
        assertTrue(success[0]);
    }

    /**
     * Test of getComputationForModel method, of class ComputationElementDTO.
     */
    @Test
    public void testGetComputationForModel() {
        System.out.println("getComputationForModel");
        ProjectModelDTO model = new ProjectModelDTO();
        model.setProjectBanner(new ProjectBannerDTO());
        model.setPhaseModels(new ArrayList<PhaseModelDTO>());
        model.setProjectDetails(new ProjectDetailsDTO());
        
        ComputationElementDTO instance = new ComputationElementDTO();
        instance.setRule("12 / 3 + 6");
        Computation result = instance.getComputationForModel(model);
        assertEquals("12 ÷ 3 + 6", result.toString());
    }

    /**
     * Test of hasConstraints method, of class ComputationElementDTO.
     */
    @Test
    public void testHasConstraints() {
        System.out.println("hasConstraints");
        ComputationElementDTO instance = new ComputationElementDTO();
        assertFalse(instance.hasConstraints());
        
        instance.setMinimumValue("10");
        assertTrue(instance.hasConstraints());
        
        instance.setMaximumValue("20");
        assertTrue(instance.hasConstraints());
        
        instance.setMinimumValue(null);
        assertTrue(instance.hasConstraints());
    }

    /**
     * Test of getMinimumValueConstraint method, of class ComputationElementDTO.
     */
    @Test
    public void testGetMinimumValueConstraint() {
        System.out.println("getMinimumValueConstraint");
        ComputationElementDTO instance = new ComputationElementDTO();
        instance.setMinimumValue("10");
        assertEquals(new DoubleValue(10.0), instance.getMinimumValueConstraint());
    }

    /**
     * Test of getMaximumValueConstraint method, of class ComputationElementDTO.
     */
    @Test
    public void testGetMaximumValueConstraint() {
        System.out.println("getMaximumValueConstraint");
        ComputationElementDTO instance = new ComputationElementDTO();
        instance.setMaximumValue("30");
        assertEquals(new DoubleValue(30.0), instance.getMaximumValueConstraint());
    }

}
