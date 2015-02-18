package org.sigmah.shared.dto.referential;

import java.io.Serializable;

/**
 * List of possible container for flexible elements.
 * Used by {@link Container} object.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public enum ContainerType implements Serializable {
	PROJECT, ORG_UNIT;
}
