package org.sigmah.server.computation;

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

import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import static org.junit.Assert.*;
import org.sigmah.server.domain.OrgUnitBanner;
import org.sigmah.server.domain.OrgUnitDetails;
import org.sigmah.server.domain.OrgUnitModel;
import org.sigmah.server.domain.PhaseModel;
import org.sigmah.server.domain.ProjectBanner;
import org.sigmah.server.domain.ProjectDetails;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.server.domain.element.ComputationElement;
import org.sigmah.server.domain.element.DefaultFlexibleElement;
import org.sigmah.server.domain.element.TextAreaElement;
import org.sigmah.server.domain.layout.Layout;
import org.sigmah.shared.dto.element.ComputationElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.TextAreaElementDTO;
import org.sigmah.shared.dto.referential.DefaultFlexibleElementType;
import org.sigmah.shared.dto.referential.TextAreaType;

/**
 * Test class for <code>ServerComputations</code>.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class ServerComputationsTest {
	
	/**
	 * Test of getAllElementsFromModel method, of class ServerComputations.
	 */
	@Test
	public void testGetAllElementsFromModel_ProjectModel() {
		Collection<FlexibleElementDTO> result = ServerComputations.getAllElementsFromModel((ProjectModel) null);
		assertNotNull(result);
		assertEquals(0, result.size());
		
		final ProjectModel projectModel = getProjectModel();
		projectModel.getPhaseModels().get(0).setLayout(null);
		result = ServerComputations.getAllElementsFromModel(projectModel);
		assertNotNull(result);
		assertEquals(0, result.size());
		
		result = ServerComputations.getAllElementsFromModel(getProjectModel());
		assertNotNull(result);
		assertEquals(3, result.size());
		
		for (final FlexibleElementDTO element : result) {
			if (element.getCode() == null) {
				fail();
				return;
			}
			switch (element.getCode()) {
			case "retail":
				assertEquals(TextAreaElementDTO.class, element.getClass());
				assertEquals(2, (long) element.getId());
				break;
			case "bargain":
				assertEquals(TextAreaElementDTO.class, element.getClass());
				assertEquals(3, (long) element.getId());
				break;
			case "price":
				assertEquals(ComputationElementDTO.class, element.getClass());
				assertEquals(4, (long) element.getId());
				break;
			default:
				fail();
				break;
			}
		}
	}

	/**
	 * Test of getAllElementsFromModel method, of class ServerComputations.
	 */
	@Test
	public void testGetAllElementsFromModel_OrgUnitModel() {
		Collection<FlexibleElementDTO> result = ServerComputations.getAllElementsFromModel((OrgUnitModel) null);
		assertNotNull(result);
		assertEquals(0, result.size());
		
		result = ServerComputations.getAllElementsFromModel(getOrgUnitModel());
		assertNotNull(result);
		assertEquals(3, result.size());
		
		for (final FlexibleElementDTO element : result) {
			if (element.getCode() == null) {
				fail();
				return;
			}
			switch (element.getCode()) {
			case "retail":
				assertEquals(TextAreaElementDTO.class, element.getClass());
				assertEquals(2, (long) element.getId());
				break;
			case "bargain":
				assertEquals(TextAreaElementDTO.class, element.getClass());
				assertEquals(3, (long) element.getId());
				break;
			case "price":
				assertEquals(ComputationElementDTO.class, element.getClass());
				assertEquals(4, (long) element.getId());
				break;
			default:
				fail();
				break;
			}
		}
	}
	
	private ProjectModel getProjectModel() {
		final PhaseModel phaseModel = new PhaseModel();
		phaseModel.setLayout(getLayout());
		
		final ProjectModel projectModel = new ProjectModel();
		projectModel.setProjectDetails(new ProjectDetails());
		projectModel.setProjectBanner(new ProjectBanner());
		projectModel.setPhaseModels(Arrays.asList(phaseModel));
		
		return projectModel;
	}
	
	private OrgUnitModel getOrgUnitModel() {
		final OrgUnitModel orgUnitModel = new OrgUnitModel();
		
		final OrgUnitDetails details = new OrgUnitDetails();
		details.setLayout(getLayout());
		
		orgUnitModel.setBanner(new OrgUnitBanner());
		orgUnitModel.setDetails(details);
		
		return orgUnitModel;
	}

	private Layout getLayout() {
		final DefaultFlexibleElement titleElement = new DefaultFlexibleElement();
		titleElement.setId(1);
		titleElement.setAmendable(true);
		titleElement.setType(DefaultFlexibleElementType.TITLE);
		
		final TextAreaElement retailPriceElement = new TextAreaElement();
		retailPriceElement.setId(2);
		retailPriceElement.setType(TextAreaType.NUMBER.getCode());
		retailPriceElement.setIsDecimal(Boolean.TRUE);
		retailPriceElement.setCode("retail");
		
		final TextAreaElement bargainElement = new TextAreaElement();
		bargainElement.setId(3);
		bargainElement.setType(TextAreaType.NUMBER.getCode());
		bargainElement.setIsDecimal(Boolean.TRUE);
		bargainElement.setCode("bargain");
		
		final ComputationElement computationElement = new ComputationElement();
		computationElement.setId(4);
		computationElement.setRule("retail * bargain");
		computationElement.setCode("price");
		
		final Layout layout = new Layout(4, 1);
		layout.addConstraint(0, 0, titleElement, 0);
		layout.addConstraint(1, 0, retailPriceElement, 0);
		layout.addConstraint(2, 0, bargainElement, 0);
		layout.addConstraint(3, 0, computationElement, 0);
		
		return layout;
	}
	
}
