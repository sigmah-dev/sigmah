package org.sigmah.shared.dto;

import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

/**
 * DTO mapping class for entity PhaseModelDefinition.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class PhaseModelDefinitionDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1745325814814487880L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return "PhaseModelDefinition";
	}

	// Definition id.
	@Override
	public Integer getId() {
		return get("id");
	}

	public void setId(Integer id) {
		set("id", id);
	}
}
