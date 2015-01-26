package org.sigmah.shared.dto.base.mapping;

/**
 * Enables mapping modes.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
public interface HasMappingMode {

	/**
	 * Get the current mapping mode.
	 * 
	 * @return The current mapping mode.
	 */
	IsMappingMode getCurrentMappingMode();

	/**
	 * Sets the current mapping mode.
	 * 
	 * @param currentMappingMode
	 *          the current mapping mode.
	 */
	void setCurrentMappingMode(IsMappingMode currentMappingMode);

}
