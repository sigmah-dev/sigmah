package org.sigmah.shared.dto.base.mapping;

/**
 * Defines a custom mapping for DTO.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
public interface IsMappingMode {

	/**
	 * Gets the unique mapping mode id.
	 * 
	 * @return The id.
	 */
	String getMapId();

	/**
	 * Gets the mapping properties of the custom fields for the current mapping mode.
	 * 
	 * @return The custom fields.
	 * @see CustomMappingField
	 */
	CustomMappingField[] getCustomFields();

	/**
	 * Gets the mapping properties of the fields excluded for the current mapping mode.
	 * 
	 * @return The excluded fields.
	 * @see MappingField
	 */
	MappingField[] getExcludedFields();

}
