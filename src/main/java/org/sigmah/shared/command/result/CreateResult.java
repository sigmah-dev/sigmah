package org.sigmah.shared.command.result;

import org.sigmah.shared.dto.base.EntityDTO;

/**
 * <p>
 * Result of the command creating a new entity.
 * </p>
 * <p>
 * The {@code entity} attribute represents the created entity (with its id).
 * </p>
 * 
 * @see org.sigmah.shared.command.CreateEntity
 * @see org.sigmah.shared.command.CreateReportDef
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class CreateResult implements Result {

	/**
	 * The created entity.
	 */
	protected EntityDTO<?> entity;

	protected CreateResult() {
		// Serialization.
	}

	public CreateResult(final EntityDTO<?> entity) {
		this.entity = entity;
	}

	/**
	 * Returns the created {@link EntityDTO}.<br>
	 * <em>A cast is necessary to handle proper result type.</em>
	 * 
	 * @return The created {@link EntityDTO}.
	 */
	public EntityDTO<?> getEntity() {
		return entity;
	}

}
