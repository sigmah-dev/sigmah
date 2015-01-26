package org.sigmah.shared.dto.calendar;

import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

/**
 * This DTO is currently not used.
 * 
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class PersonalEventDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = -3126801656737893590L;

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "calendar.PersonalEvent";

	public PersonalEventDTO() {
		// Serialization.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

}
