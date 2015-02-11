package org.sigmah.server.domain.element;

import javax.persistence.Entity;
import javax.persistence.Table;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Core version element domain entity.
 * </p>
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.CORE_VERSION_ELEMENT_TABLE)
public class CoreVersionElement extends FlexibleElement {
	
}
