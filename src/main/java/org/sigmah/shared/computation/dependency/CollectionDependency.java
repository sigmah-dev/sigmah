package org.sigmah.shared.computation.dependency;

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
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class CollectionDependency implements Dependency {
	
	private Scope scope;
	private String elementCode;
			
	private FlexibleElementDTO flexibleElement;

	public CollectionDependency() {
	}

	public CollectionDependency(Scope scope, String elementCode) {
		this.scope = scope;
		this.elementCode = elementCode;
		
		if (elementCode != null && elementCode.length() > 1 && elementCode.charAt(0) == Instructions.ID_PREFIX) {
			this.flexibleElement = createFlexibleElement(elementCode);
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

	@Override
	public boolean isResolved() {
		return flexibleElement != null;
	}
	
	@Override
	public String toString() {
		final StringBuilder stringBuilder = new StringBuilder()
				.append(scope.getLinkedProjectTypeName())
				.append(ValueResultUtils.DEFAULT_VALUE_SEPARATOR)
				.append(scope.getModelName())
				.append(ValueResultUtils.DEFAULT_VALUE_SEPARATOR);
		
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
