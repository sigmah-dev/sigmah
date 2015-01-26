package org.sigmah.server.mapper;

import java.util.ArrayList;

import org.sigmah.server.domain.base.Entity;
import org.sigmah.shared.dto.base.DTO;

/**
 * Contains all the available mapping modes.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class MappingModeDefinitions {

	private final ArrayList<MappingModeDefinition<?, ?>> definitions;

	public MappingModeDefinitions() {
		definitions = new ArrayList<>();
	}

	public ArrayList<MappingModeDefinition<?, ?>> getModes() {
		return definitions;
	}

	public <E extends Entity, D extends DTO> void add(MappingModeDefinition<E, D> def) {
		definitions.add(def);
	}

}
