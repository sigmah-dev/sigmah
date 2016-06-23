package org.sigmah.shared.command;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.util.Map;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.dto.ActivityDTO;
import org.sigmah.shared.dto.IndicatorGroup;
import org.sigmah.shared.dto.SiteDTO;
import org.sigmah.shared.dto.UserDatabaseDTO;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

import com.extjs.gxt.ui.client.data.RpcMap;

/**
 * Creates and persists a domain entity on the server.
 *
 * Note: Some entities require specialized commands to create or update, such as:
 * <ul>
 * <li>{@link org.sigmah.shared.command.AddPartner}</li>
 * <li>{@link UpdateUserPermissions}</li>
 * <li>{@link org.sigmah.shared.command.CreateReportDef}</li>
 * </ul>
 *
 * Returns {@link org.sigmah.shared.command.result.CreateResult}
 *
 * @author Alex Bertram (akbertram@gmail.com)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class CreateEntity extends AbstractCommand<CreateResult> {

	private String entityName;
	private RpcMap properties;

	public CreateEntity() {
		// Serialization.
	}

	public CreateEntity(AbstractModelDataEntityDTO<?> entity) {
		this(entity.getEntityName(), entity.getProperties());
	}

	public CreateEntity(String entityName, Map<String, ?> properties) {
		this.entityName = entityName;
		this.properties = new RpcMap();
		this.properties.putAll(properties);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("entityName", entityName);
		builder.append("properties", properties);
	}

	/**
	 * @return The name of the entity to create. The name should correspond to one of the classes in
	 *         {@link org.sigmah.server.domain}
	 */
	public String getEntityName() {
		return entityName;
	}

	/**
	 * A map of properties to create.
	 *
	 * Note: For the most part, references to related entities should be specified by id: for example,
	 * {@link org.sigmah.server.domain.Activity#database} should be entered as databaseId in the property map.
	 *
	 * There are some exceptions to this that will take some time to fix:
	 * <ul>
	 * <li>{@link org.sigmah.server.domain.Site#partner}</li>
	 * <li>AdminEntities associated with Sites/Locations</li>
	 * </ul>
	 * See {@link org.sigmah.server.handler.CreateEntityHandler} for the last word.
	 *
	 * @return The properties/fields of the entity to create.
	 */
	public RpcMap getProperties() {
		return properties;
	}

	public static CreateEntity Activity(UserDatabaseDTO db, ActivityDTO act) {
		final CreateEntity cmd = new CreateEntity("Activity", act.getProperties());
		cmd.properties.put("databaseId", db.getId());
		return cmd;
	}

	public static CreateEntity IndicatorGroup(int databaseId, IndicatorGroup indicatorGroup) {
		final CreateEntity cmd = new CreateEntity(IndicatorGroup.ENTITY_NAME, indicatorGroup.getProperties());
		cmd.properties.put("databaseId", databaseId);
		return cmd;
	}

	public static CreateEntity Site(SiteDTO newSite) {
		final CreateEntity cmd = new CreateEntity("Site", newSite.getProperties());
		// cmd.properties.put("activityId", newSite.getActivityId());
		return cmd;
	}
}
