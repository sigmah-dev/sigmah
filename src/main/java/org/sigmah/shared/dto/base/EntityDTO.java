package org.sigmah.shared.dto.base;

import java.io.Serializable;

/**
 * A data transfer object with a one-to-one relationship with a JPA @Entity.
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface EntityDTO<K extends Serializable> extends DTO {

	/**
	 * Id property meta-name.
	 */
	final String ID = "id";

	/**
	 * Returns the corresponding DTO entity's id.
	 * 
	 * @return the corresponding DTO entity's id.
	 */
	K getId();

	/**
	 * Returns the corresponding DTO entity's JPA name starting from the "{@code server.domain}" package name.
	 * 
	 * @return the corresponding DTO entity's JPA name.
	 */
	String getEntityName();

}
