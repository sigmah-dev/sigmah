package org.sigmah.shared.computation.dependency;

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

import com.google.gwt.core.client.GWT;
import org.sigmah.shared.computation.instruction.Instructions;
import org.sigmah.shared.dto.element.ComputationElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.TextAreaElementDTO;
import org.sigmah.shared.dto.referential.ElementTypeEnum;
import org.sigmah.shared.dto.referential.LogicalElementType;
import org.sigmah.shared.dto.referential.LogicalElementTypes;
import org.sigmah.shared.dto.referential.TextAreaType;
import org.sigmah.shared.util.ValueResultUtils;

/**
 * Dependency to multiple value for the same flexible element.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.2
 */
public class CollectionDependency implements Dependency {
	
	private Scope scope;
	private String elementCode;
	
	private Integer projectModelId;
	private FlexibleElementDTO flexibleElement;

	/**
	 * Empty constructor, required for serialization.
	 */
	public CollectionDependency() {
		// Empty.
	}

	public CollectionDependency(Scope scope, String elementCode) {
		this.scope = scope;
		this.elementCode = elementCode;
		
		if (elementCode != null && elementCode.length() > 1 && elementCode.charAt(0) == Instructions.ID_PREFIX) {
			this.flexibleElement = createFlexibleElement(elementCode);
		}
		final String modelName = scope.getModelName();
		if (modelName != null && modelName.length() > 1 && modelName.charAt(0) == Instructions.ID_PREFIX) {
			final String[] parts = modelName.split(ValueResultUtils.BUDGET_VALUE_SEPARATOR);
			if (parts.length == 2) {
				try {
					projectModelId = Integer.parseInt(parts[0].substring(1));
					scope.setModelName(parts[1]);
				} catch (NumberFormatException e) {
					GWT.log("Given model name starts by the identifier prefix but is not an identifier: " + modelName, e);
				}
			}
		}
	}

	public Scope getScope() {
		return scope;
	}

	public String getElementCode() {
		return elementCode;
	}

	public FlexibleElementDTO getFlexibleElement() {
		return flexibleElement;
	}

	public void setFlexibleElement(FlexibleElementDTO flexibleElement) {
		this.flexibleElement = flexibleElement;
	}

	public Integer getProjectModelId() {
		return projectModelId;
	}

	public void setProjectModelId(Integer projectModelId) {
		this.projectModelId = projectModelId;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isResolved() {
		return flexibleElement != null && projectModelId != null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder stringBuilder = new StringBuilder()
				.append(scope.getLinkedProjectTypeName())
				.append(ValueResultUtils.DEFAULT_VALUE_SEPARATOR);
		
		if (projectModelId != null) {
			stringBuilder.append(Instructions.ID_PREFIX)
					.append(projectModelId)
					.append(ValueResultUtils.BUDGET_VALUE_SEPARATOR);
		}
		return stringBuilder.append(scope.getModelName())
				.append(ValueResultUtils.DEFAULT_VALUE_SEPARATOR)
				.append(flexibleElementString())
				.toString();
	}
	
	/**
	 * Returns a string representation of the flexible element referenced
	 * by this dependency.
	 * 
	 * @return A string representation of the flexible element.
	 */
	public String flexibleElementString() {
		final StringBuilder stringBuilder = new StringBuilder();
		
		if (flexibleElement != null) {
			stringBuilder.append(Instructions.ID_PREFIX)
					.append(flexibleElement.getId())
					.append(ValueResultUtils.BUDGET_VALUE_SEPARATOR)
					.append(flexibleElement.getCode())
					.append(ValueResultUtils.BUDGET_VALUE_SEPARATOR)
					.append(LogicalElementTypes.of(flexibleElement));
		} else {
			stringBuilder.append(elementCode);
		}
		
		return stringBuilder.toString();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toHumanReadableString() {
		final StringBuilder stringBuilder = new StringBuilder()
				.append(scope.getLinkedProjectTypeName())
				.append(ValueResultUtils.DEFAULT_VALUE_SEPARATOR)
				.append(scope.getModelName())
				.append(ValueResultUtils.DEFAULT_VALUE_SEPARATOR);
		
		if (flexibleElement != null) {
			stringBuilder.append(flexibleElement.getCode());
		} else {
			stringBuilder.append(elementCode);
		}
		
		return stringBuilder.toString();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(DependencyVisitor visitor) {
		visitor.visit(this);
	}
	
	/**
	 * Creates a flexible element stub from the given encoded string.
	 * 
	 * The given string must respect the following format:
	 * $<code>id</code>%<code>element code</code>%<code>logical element type</code>
	 * 
	 * @param encodedElement
	 *			Encoded flexible element.
	 * @return A Flexible element stub with the given data
	 * or <code>null</code> if the given string does not match the format.
	 */
	private FlexibleElementDTO createFlexibleElement(final String encodedElement) {
		final String[] parts = encodedElement.split(ValueResultUtils.BUDGET_VALUE_SEPARATOR);
		if (parts.length != 3) {
			GWT.log("Argument starts by the identifier prefix but can't be splitten in 3 parts: " + encodedElement);
			return null;
		}
			
		final String code = parts[1];
		final LogicalElementType type = LogicalElementTypes.fromName(parts[2]);
		try {
			final int flexibleElementId = Integer.parseInt(parts[0].substring(1));
			
			final FlexibleElementDTO element;
			if (type == TextAreaType.NUMBER) {
				final TextAreaElementDTO textAreaElement = new TextAreaElementDTO();
				textAreaElement.setType(type.toTextAreaType().getCode());
				element = textAreaElement;
			}
			else if (type == ElementTypeEnum.COMPUTATION) {
				element = new ComputationElementDTO();
			}
			else {
				return null;
			}
			
			element.setId(flexibleElementId);
			element.setCode(code);
			return element;
		} catch (NumberFormatException e) {
			GWT.log("Argument starts by the identifier prefix but is not an identifier: " + encodedElement, e);
		}
		
		return null;
	}
}
