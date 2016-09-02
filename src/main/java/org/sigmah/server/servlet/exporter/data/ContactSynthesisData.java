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

import com.google.inject.Injector;
import org.sigmah.server.domain.Contact;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.Project;
import org.sigmah.server.servlet.exporter.base.Exporter;

public class ContactSynthesisData extends BaseSynthesisData {

	private final Contact contact;

	public ContactSynthesisData(final Exporter exporter, final Integer contactId, final Injector injector) {

		super(exporter, injector, false);
		contact = entityManager.find(Contact.class, contactId);

	}

	@Override
	public Project getProject() {
		return null;
	}

	@Override
	public OrgUnit getOrgUnit() {
		return null;
	}

	@Override
	public Contact getContact() {
		return contact;
	}

}
