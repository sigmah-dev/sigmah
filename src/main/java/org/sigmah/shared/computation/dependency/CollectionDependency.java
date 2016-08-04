package org.sigmah.shared.computation.dependency;

import org.sigmah.shared.computation.instruction.Instructions;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
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
			stringBuilder.append(Instructions.ID_PREFIX).append(flexibleElement.getId());
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
}
