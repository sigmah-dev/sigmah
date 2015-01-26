package org.sigmah.server.mapper;

import org.sigmah.server.domain.base.Entity;
import org.sigmah.shared.dto.base.DTO;
import org.sigmah.shared.dto.base.mapping.IsMappingMode;

/**
 * Defines a mapping mode.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class MappingModeDefinition<E extends Entity, D extends DTO> {

	private final Class<E> entityClass;
	private final Class<D> dtoClass;
	private final IsMappingMode[] modes;

	public MappingModeDefinition(Class<E> entityClass, Class<D> dtoClass, IsMappingMode... modes) {
		this.entityClass = entityClass;
		this.dtoClass = dtoClass;
		this.modes = modes;
	}

	public Class<E> getEntityClass() {
		return entityClass;
	}

	public Class<D> getDtoClass() {
		return dtoClass;
	}

	public IsMappingMode[] getModes() {
		return modes;
	}

}
