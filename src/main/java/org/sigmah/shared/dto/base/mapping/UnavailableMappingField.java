package org.sigmah.shared.dto.base.mapping;

import org.sigmah.shared.dto.base.DTO;

/**
 * Throws if the field isn't available in a given mapping mode.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class UnavailableMappingField extends RuntimeException {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = -8081248200259540757L;

	/**
	 * The current DTO class.
	 */
	private final Class<? extends DTO> dtoClass;

	/**
	 * The current mapping mode.
	 */
	private final IsMappingMode currentMappingMode;

	/**
	 * The unavailable mapping field.
	 */
	private final MappingField field;

	public UnavailableMappingField(final Class<? extends DTO> dtoClass, final IsMappingMode currentMappingMode, final MappingField field) {
		super("The field " + field + " is unavailable for the current mapping " + currentMappingMode.getMapId() + " in class " + dtoClass.getName() + ".");
		this.dtoClass = dtoClass;
		this.currentMappingMode = currentMappingMode;
		this.field = field;
	}

	public MappingField getField() {
		return field;
	}

	public Class<? extends DTO> getDTOClass() {
		return dtoClass;
	}

	public IsMappingMode getCurrentMappingMode() {
		return currentMappingMode;
	}

}
