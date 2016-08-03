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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.sigmah.server.domain.OrgUnitModel;
import org.sigmah.server.domain.PhaseModel;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.server.domain.element.CheckboxElement;
import org.sigmah.server.domain.element.ComputationElement;
import org.sigmah.server.domain.element.CoreVersionElement;
import org.sigmah.server.domain.element.DefaultFlexibleElement;
import org.sigmah.server.domain.element.FilesListElement;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.element.IndicatorsListElement;
import org.sigmah.server.domain.element.MessageElement;
import org.sigmah.server.domain.element.QuestionElement;
import org.sigmah.server.domain.element.ReportElement;
import org.sigmah.server.domain.element.ReportListElement;
import org.sigmah.server.domain.element.TextAreaElement;
import org.sigmah.server.domain.element.TripletsListElement;
import org.sigmah.server.domain.layout.Layout;
import org.sigmah.server.domain.layout.LayoutConstraint;
import org.sigmah.server.domain.layout.LayoutGroup;
import org.sigmah.shared.dto.element.BudgetElementDTO;
import org.sigmah.shared.dto.element.CheckboxElementDTO;
import org.sigmah.shared.dto.element.ComputationElementDTO;
import org.sigmah.shared.dto.element.CoreVersionElementDTO;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.element.FilesListElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.IndicatorsListElementDTO;
import org.sigmah.shared.dto.element.MessageElementDTO;
import org.sigmah.shared.dto.element.QuestionElementDTO;
import org.sigmah.shared.dto.element.ReportElementDTO;
import org.sigmah.shared.dto.element.ReportListElementDTO;
import org.sigmah.shared.dto.element.TextAreaElementDTO;
import org.sigmah.shared.dto.element.TripletsListElementDTO;
import org.sigmah.shared.dto.referential.DefaultFlexibleElementType;
import org.sigmah.shared.dto.referential.ElementTypeEnum;
import static org.sigmah.shared.dto.referential.ElementTypeEnum.CHECKBOX;
import static org.sigmah.shared.dto.referential.ElementTypeEnum.CORE_VERSION;
import static org.sigmah.shared.dto.referential.ElementTypeEnum.FILES_LIST;
import org.sigmah.shared.dto.referential.LogicalElementType;
import org.sigmah.shared.dto.referential.NoElementType;
import org.sigmah.shared.dto.referential.TextAreaType;

/**
 * Utility class to ease the usage of computations from server-side.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public final class ServerComputations {
	
	/**
	 * Creates a collection containing every element that has a code from the
	 * given model.
	 * <p>
	 * The created DTO are very simple. They only contains the identifier and
	 * the code properties from the original object.
	 * 
	 * @param projectModel
	 *          Model to use.
	 * @return A collection of every element usable by computations or an empty
	 * collection if the given model is <code>null</code> or if it does not
	 * contains any usable element.
	 */
	public static Collection<FlexibleElementDTO> getAllElementsFromModel(final ProjectModel projectModel) {
		
		if (projectModel == null) {
			return Collections.<FlexibleElementDTO>emptyList();
		}
		
		final ArrayList<FlexibleElementDTO> dtos = new ArrayList<>();
		dtos.addAll(toDTOCollection(projectModel.getProjectDetails().getLayout()));
		for (final PhaseModel phaseModel : projectModel.getPhaseModels()) {
			dtos.addAll(toDTOCollection(phaseModel.getLayout()));
		}
		return dtos;
	}
	
	/**
	 * Creates a collection containing every element that has a code from the
	 * given model.
	 * <p>
	 * The created DTO are very simple. They only contains the identifier and
	 * the code properties from the original object.
	 * 
	 * @param orgUnitModel
	 *          Model to use.
	 * @return A collection of every element usable by computations or an empty
	 * collection if the given model is <code>null</code> or if it does not
	 * contains any usable element.
	 */
	public static Collection<FlexibleElementDTO> getAllElementsFromModel(final OrgUnitModel orgUnitModel) {
		
		if (orgUnitModel == null) {
			return Collections.<FlexibleElementDTO>emptyList();
		}
		
		final ArrayList<FlexibleElementDTO> dtos = new ArrayList<>();
		dtos.addAll(toDTOCollection(orgUnitModel.getDetails().getLayout()));
		return dtos;
	}
	
	public static Collection<Layout> getAllLayoutsFromModel(final ProjectModel projectModel) {
		
		if (projectModel == null) {
			return Collections.<Layout>emptyList();
		}
		
		final ArrayList<Layout> layouts = new ArrayList<>();
		layouts.add(projectModel.getProjectDetails().getLayout());
		
		for (final PhaseModel phaseModel : projectModel.getPhaseModels()) {
			layouts.add(phaseModel.getLayout());
		}
		
		return layouts;
	}

	/**
	 * Extract every element with a code from the given layout and creates a 
	 * simple DTO for each of them.
	 * <p>
	 * The created DTO are very simple. They only contains the identifier and
	 * the code properties from the original object.
	 * 
	 * @param layout
	 *          The layout to read.
	 * @return A collection of every element usable by computations or an empty
	 * collection if the given layout is <code>null</code> or if it does not
	 * contains any usable element.
	 */
	private static Collection<FlexibleElementDTO> toDTOCollection(final Layout layout) {
		
		if (layout == null) {
			return Collections.<FlexibleElementDTO>emptyList();
		}
		
		final ArrayList<FlexibleElementDTO> dtos = new ArrayList<>();
		
		for (final LayoutGroup group : layout.getGroups()) {
			for (final LayoutConstraint constraint : group.getConstraints()) {
				final FlexibleElement element = constraint.getElement();
				
				if (element != null && element.getCode() != null) {
					final FlexibleElementDTO dto = toDTO(element);
					dto.setId(element.getId());
					dto.setCode(element.getCode());
					dtos.add(dto);
				}
			}
		}
		
		return dtos;
	}
	
	public static FlexibleElementDTO getElementWithCodeInModel(final String code, final ProjectModel projectModel) {
		
		if (code == null) {
			return null;
		}
		
		for (final Layout layout : getAllLayoutsFromModel(projectModel)) {
			final FlexibleElementDTO element = getElementWithCodeInLayout(code, layout);
			if (element != null) {
				return element;
			}
		}
		
		return null;
	}
	
	private static FlexibleElementDTO getElementWithCodeInLayout(final String code, final Layout layout) {
		
		if (layout == null) {
			return null;
		}
		
		for (final LayoutGroup group : layout.getGroups()) {
			for (final LayoutConstraint constraint : group.getConstraints()) {
				final FlexibleElement element = constraint.getElement();
				
				if (element != null && code.equals(element.getCode())) {
					final FlexibleElementDTO dto = toDTO(element);
					dto.setId(element.getId());
					dto.setCode(element.getCode());
					return dto;
				}
			}
		}
		
		return null;
	}
	
	// --
	// TODO: Merge the following methods with the ones from the #844 branch.
	// --
	
	private static LogicalElementType logicalElementTypeOf(final FlexibleElement element) {
		
		final LogicalElementType type;
		
		if (element instanceof TextAreaElement) {
			type = TextAreaType.fromCode(((TextAreaElement) element).getType());
		} else if (element instanceof CheckboxElement) {
			type = ElementTypeEnum.CHECKBOX;
		} else if (element instanceof DefaultFlexibleElement) {
			type = ((DefaultFlexibleElement) element).getType();
		} else if (element instanceof FilesListElement) {
			type = ElementTypeEnum.FILES_LIST;
		} else if (element instanceof IndicatorsListElement) {
			type = ElementTypeEnum.INDICATORS;
		} else if (element instanceof MessageElement) {
			type = ElementTypeEnum.MESSAGE;
		} else if (element instanceof QuestionElement) {
			type = ElementTypeEnum.QUESTION;
		} else if (element instanceof ReportElement) {
			type = ElementTypeEnum.REPORT;
		} else if (element instanceof ReportListElement) {
			type = ElementTypeEnum.REPORT_LIST;
		} else if (element instanceof TripletsListElement) {
			type = ElementTypeEnum.TRIPLETS;
		} else if (element instanceof CoreVersionElement) {
			type = ElementTypeEnum.CORE_VERSION;
		} else if (element instanceof ComputationElement) {
			type = ElementTypeEnum.COMPUTATION;
		} else {
			type = null;
		}
		
		if (type != null) {
			return type;
		} else {
			return NoElementType.INSTANCE;
		}
	}
	
	private static FlexibleElementDTO toDTO(final FlexibleElement element) {
		
		final FlexibleElementDTO dto;
		
		final LogicalElementType type = logicalElementTypeOf(element);
		
		switch (type.toElementTypeEnum()) {
		case CHECKBOX:
			dto = new CheckboxElementDTO();
			break;
		case COMPUTATION:
			dto = new ComputationElementDTO();
			break;
		case CORE_VERSION:
			dto = new CoreVersionElementDTO();
			break;
		case DEFAULT:
			if (type == DefaultFlexibleElementType.BUDGET) {
				dto = new BudgetElementDTO();
			} else {
				dto = new DefaultFlexibleElementDTO();
			}
			break;
		case FILES_LIST:
			dto = new FilesListElementDTO();
			break;
		case INDICATORS:
			dto = new IndicatorsListElementDTO();
			break;
		case MESSAGE:
			dto = new MessageElementDTO();
			break;
		case QUESTION:
			dto = new QuestionElementDTO();
			break;
		case REPORT:
			dto = new ReportElementDTO();
			break;
		case REPORT_LIST:
			dto = new ReportListElementDTO();
			break;
		case TEXT_AREA:
			dto = new TextAreaElementDTO();
			break;
		case TRIPLETS:
			dto = new TripletsListElementDTO();
			break;
		default:
			throw new UnsupportedOperationException("Flexible element of type '" + type + "' is not supported.");
		}
		
		return dto;
	}
	
	/**
	 * Private constructor.
	 */
	private ServerComputations() {
		// No initialization.
	}
	
}
