package org.sigmah.shared.computation.dependency;

import org.sigmah.shared.dto.element.FlexibleElementDTO;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class SingleDependency implements Dependency {
	
	private FlexibleElementDTO flexibleElement;

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 53 * hash + (flexibleElement != null ? flexibleElement.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SingleDependency other = (SingleDependency) obj;
		if (flexibleElement != null) {
			return flexibleElement.equals(other.flexibleElement);
		} else {
			return other.flexibleElement == null;
		}
	}
	
}
