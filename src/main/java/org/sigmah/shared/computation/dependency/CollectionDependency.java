package org.sigmah.shared.computation.dependency;

import org.sigmah.shared.dto.ProjectFundingDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;

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
		final ProjectFundingDTO.LinkedProjectType linkedProjectType = scope.getLinkedProjectType();
		if (linkedProjectType != null) switch (linkedProjectType) {
			case FUNDED_PROJECT:
				return "fundedProjects";
			case FUNDING_PROJECT:
				return "fundingSources";
		}
		throw new UnsupportedOperationException("Unsupported LinkedProjectType: " + linkedProjectType);
	}
	
	@Override
	public String toHumanReadableString() {
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(toString()).append('(');
		if (scope.getModelName() != null) {
			stringBuilder.append(scope.getModelName());
		}
		stringBuilder.append(").");
		// TODO: Trouver un moyen d'incorporer le nom de la fonction de réduction.
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
