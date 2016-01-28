package org.sigmah.shared.dto.referential;

import org.junit.Assert;
import org.junit.Test;
import org.sigmah.shared.dto.element.CheckboxElementDTO;
import org.sigmah.shared.dto.element.ComputationElementDTO;
import org.sigmah.shared.dto.element.CoreVersionElementDTO;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.element.FilesListElementDTO;
import org.sigmah.shared.dto.element.IndicatorsListElementDTO;
import org.sigmah.shared.dto.element.MessageElementDTO;
import org.sigmah.shared.dto.element.QuestionElementDTO;
import org.sigmah.shared.dto.element.ReportElementDTO;
import org.sigmah.shared.dto.element.ReportListElementDTO;
import org.sigmah.shared.dto.element.TextAreaElementDTO;
import org.sigmah.shared.dto.element.TripletsListElementDTO;

/**
 * Test class for <code>LogicalElementTypes</code>.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class LogicalElementTypesTest {
    
    /**
     * Test of getElementType method, of class LogicalElementTypes.
     */
    @Test
    public void testWithNull() {
        System.out.println("withNull");
        
        final LogicalElementType type = LogicalElementTypes.of(null);
        
        Assert.assertEquals(NoElementType.INSTANCE, type);
        Assert.assertNull(type.toElementTypeEnum());
        Assert.assertNull(type.toTextAreaType());
        Assert.assertNull(type.toDefaultFlexibleElementType());
    }
    
    /**
     * Test of getElementType method, of class LogicalElementTypes.
     */
    @Test
    public void testWithCheckboxElement() {
        System.out.println("withCheckboxElement");
        
        final LogicalElementType type = LogicalElementTypes.of(new CheckboxElementDTO());
        
        Assert.assertEquals(ElementTypeEnum.CHECKBOX, type);
        Assert.assertEquals(type, type.toElementTypeEnum());
        Assert.assertNull(type.toTextAreaType());
        Assert.assertNull(type.toDefaultFlexibleElementType());
    }
    
    /**
     * Test of getElementType method, of class LogicalElementTypes.
     */
    @Test
    public void testWithComputationElement() {
        System.out.println("withComputationElement");
        
        final LogicalElementType type = LogicalElementTypes.of(new ComputationElementDTO());
        
        Assert.assertEquals(ElementTypeEnum.COMPUTATION, type);
        Assert.assertEquals(type, type.toElementTypeEnum());
        Assert.assertNull(type.toTextAreaType());
        Assert.assertNull(type.toDefaultFlexibleElementType());
    }
    
    /**
     * Test of getElementType method, of class LogicalElementTypes.
     */
    @Test
    public void testWithCoreVersionElement() {
        System.out.println("withCoreVersionElement");
        
        final LogicalElementType type = LogicalElementTypes.of(new CoreVersionElementDTO());
        
        Assert.assertEquals(ElementTypeEnum.CORE_VERSION, type);
        Assert.assertEquals(type, type.toElementTypeEnum());
        Assert.assertNull(type.toTextAreaType());
        Assert.assertNull(type.toDefaultFlexibleElementType());
    }
    
    /**
     * Test of getElementType method, of class LogicalElementTypes.
     */
    @Test
    public void testWithDefaultFlexibleElement() {
        System.out.println("withDefaultFlexibleElement");
        
        for (final DefaultFlexibleElementType defaultFlexibleElementType : DefaultFlexibleElementType.values()) {
            final DefaultFlexibleElementDTO defaultFlexibleElementDTO = new DefaultFlexibleElementDTO();
            defaultFlexibleElementDTO.setType(defaultFlexibleElementType);
            
            final LogicalElementType type = LogicalElementTypes.of(defaultFlexibleElementDTO);

            Assert.assertEquals(defaultFlexibleElementType, type);
            Assert.assertEquals(ElementTypeEnum.DEFAULT, type.toElementTypeEnum());
            Assert.assertEquals(defaultFlexibleElementDTO.getElementType(), type.toElementTypeEnum());
            Assert.assertNull(type.toTextAreaType());
            Assert.assertEquals(defaultFlexibleElementType, type.toDefaultFlexibleElementType());
        }
    }
    
    /**
     * Test of getElementType method, of class LogicalElementTypes.
     */
    @Test
    public void testWithFilesListElement() {
        System.out.println("withFilesListElement");
        
        final LogicalElementType type = LogicalElementTypes.of(new FilesListElementDTO());
        
        Assert.assertEquals(ElementTypeEnum.FILES_LIST, type);
        Assert.assertEquals(type, type.toElementTypeEnum());
        Assert.assertNull(type.toTextAreaType());
        Assert.assertNull(type.toDefaultFlexibleElementType());
    }
    
    /**
     * Test of getElementType method, of class LogicalElementTypes.
     */
    @Test
    public void testWithIndicatorsListElement() {
        System.out.println("withIndicatorsListElement");
        
        final LogicalElementType type = LogicalElementTypes.of(new IndicatorsListElementDTO());
        
        Assert.assertEquals(ElementTypeEnum.INDICATORS, type);
        Assert.assertEquals(type, type.toElementTypeEnum());
        Assert.assertNull(type.toTextAreaType());
        Assert.assertNull(type.toDefaultFlexibleElementType());
    }
    
    /**
     * Test of getElementType method, of class LogicalElementTypes.
     */
    @Test
    public void testWithMessageElement() {
        System.out.println("withMessageElement");
        
        final LogicalElementType type = LogicalElementTypes.of(new MessageElementDTO());
        
        Assert.assertEquals(ElementTypeEnum.MESSAGE, type);
        Assert.assertEquals(type, type.toElementTypeEnum());
        Assert.assertNull(type.toTextAreaType());
        Assert.assertNull(type.toDefaultFlexibleElementType());
    }
    
    /**
     * Test of getElementType method, of class LogicalElementTypes.
     */
    @Test
    public void testWithQuestionElement() {
        System.out.println("withQuestionElement");
        
        final LogicalElementType type = LogicalElementTypes.of(new QuestionElementDTO());
        
        Assert.assertEquals(ElementTypeEnum.QUESTION, type);
        Assert.assertEquals(type, type.toElementTypeEnum());
        Assert.assertNull(type.toTextAreaType());
        Assert.assertNull(type.toDefaultFlexibleElementType());
    }
    
    /**
     * Test of getElementType method, of class LogicalElementTypes.
     */
    @Test
    public void testWithReportElement() {
        System.out.println("withReportElement");
        
        final LogicalElementType type = LogicalElementTypes.of(new ReportElementDTO());
        
        Assert.assertEquals(ElementTypeEnum.REPORT, type);
        Assert.assertEquals(type, type.toElementTypeEnum());
        Assert.assertNull(type.toTextAreaType());
        Assert.assertNull(type.toDefaultFlexibleElementType());
    }
    
    /**
     * Test of getElementType method, of class LogicalElementTypes.
     */
    @Test
    public void testWithReportListElement() {
        System.out.println("withReportListElement");
        
        final LogicalElementType type = LogicalElementTypes.of(new ReportListElementDTO());
        
        Assert.assertEquals(ElementTypeEnum.REPORT_LIST, type);
        Assert.assertEquals(type, type.toElementTypeEnum());
        Assert.assertNull(type.toTextAreaType());
        Assert.assertNull(type.toDefaultFlexibleElementType());
    }
    
    /**
     * Test of getElementType method, of class LogicalElementTypes.
     */
    @Test
    public void testWithTextAreaElement() {
        System.out.println("withTextAreaElement");
        
        for (final TextAreaType textAreaType : TextAreaType.values()) {
            final TextAreaElementDTO textAreaElementDTO = new TextAreaElementDTO();
            textAreaElementDTO.setType(textAreaType.getCode());
            
            final LogicalElementType type = LogicalElementTypes.of(textAreaElementDTO);

            Assert.assertEquals(textAreaType, type);
            Assert.assertEquals(ElementTypeEnum.TEXT_AREA, type.toElementTypeEnum());
            Assert.assertEquals(textAreaElementDTO.getElementType(), type.toElementTypeEnum());
            Assert.assertEquals(textAreaType, type.toTextAreaType());
            Assert.assertNull(type.toDefaultFlexibleElementType());
        }
    }
    
    /**
     * Test of getElementType method, of class LogicalElementTypes.
     */
    @Test
    public void testWithTripletsListElement() {
        System.out.println("withTripletsListElement");
        
        final LogicalElementType type = LogicalElementTypes.of(new TripletsListElementDTO());
        
        Assert.assertEquals(ElementTypeEnum.TRIPLETS, type);
        Assert.assertEquals(type, type.toElementTypeEnum());
        Assert.assertNull(type.toTextAreaType());
        Assert.assertNull(type.toDefaultFlexibleElementType());
    }
    
}
