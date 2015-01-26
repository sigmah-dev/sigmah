package org.sigmah.shared.command;

import java.util.Map;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.base.EntityDTO;

import com.extjs.gxt.ui.client.data.RpcMap;

/**
 * <p>
 * Updates a domain entity on the server.
 * </p>
 * <p>
 * Some entities require specialized commands to create or update. See:
 * <ul>
 * <li>{@link org.sigmah.shared.command.AddPartner}</li>
 * <li>{@link org.sigmah.shared.command.UpdateUserPermissions}</li>
 * <li>{@link org.sigmah.shared.command.UpdateSubscription}</li>
 * </ul>
 * </p>
 *
 * @author Alex Bertram (akbertram@gmail.com)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class UpdateEntity extends AbstractCommand<VoidResult> {

	private Integer id;
	private String entityName;
	private RpcMap changes;

	protected UpdateEntity() {
		// Serialization.
	}

	public UpdateEntity(final EntityDTO<?> model, final Map<String, Object> changes) {
		this(model.getEntityName(), (Integer)model.getId(), changes);
	}

	public UpdateEntity(final String entityName, final Integer id, final Map<String, Object> changes) {
		this.entityName = entityName;
		this.id = id;
		this.changes = new RpcMap();
		this.changes.putAll(changes);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("id", id);
		builder.append("entityName", entityName);
		builder.append("changes", changes);
	}

	public Integer getId() {
		return id;
	}

	public String getEntityName() {
		return entityName;
	}

	public RpcMap getChanges() {
		return changes;
	}

}
