package org.sigmah.server.servlet.exporter.data;

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

import javax.persistence.EntityManager;

import org.sigmah.server.dispatch.CommandHandler;
import org.sigmah.server.domain.Contact;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.Project;
import org.sigmah.server.handler.GetValueHandler;
import org.sigmah.server.servlet.exporter.base.Exporter;
import org.sigmah.shared.command.GetValue;

import com.google.inject.Injector;
import org.sigmah.server.domain.base.EntityId;
import org.sigmah.shared.command.result.ValueResult;

/**
 * Base synthesis data for project and org unit synthesis exports.
 * 
 * @author sherzod
 */
public abstract class BaseSynthesisData extends ExportData {

	protected final EntityManager entityManager;
	private final CommandHandler<GetValue, ValueResult> handler;
	private final boolean withContacts;

	/*
	 * private final Locale locale; private final Translator translator;
	 */
	public BaseSynthesisData(final Exporter exporter, final Injector injector, final boolean withContacts) {
		super(exporter, 3);
		entityManager = injector.getInstance(EntityManager.class);
		handler = injector.getInstance(GetValueHandler.class);
		this.withContacts = withContacts;
	}

	public CommandHandler<GetValue, ValueResult> getHandler() {
		return handler;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public abstract Project getProject();

	public abstract OrgUnit getOrgUnit();

	public abstract Contact getContact();
	
	/**
	 * Returns the container with the given class.
	 * 
	 * @param clazz
	 *			Class of the container to retrieve.
	 * @return The container with the given class.
	 */
	public EntityId<Integer> getContainerWithClass(final Class<?> clazz) {
		
		if (clazz.equals(Project.class)) {
			return getProject();
		} else if (clazz.equals(OrgUnit.class)) {
			return getOrgUnit();
		} else if (clazz.equals(Contact.class)) {
			return getContact();
		} else {
			throw new UnsupportedOperationException("Container class '" + clazz + "' is not supported.");
		}
	}
	
	public boolean isWithContacts() {
		return withContacts;
	}
}
