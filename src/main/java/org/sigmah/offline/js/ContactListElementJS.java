package org.sigmah.offline.js;

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

import org.sigmah.shared.dto.element.ContactListElementDTO;

public final class ContactListElementJS extends FlexibleElementJS {

	protected ContactListElementJS() {
	}

	public static ContactListElementJS toJavaScript(ContactListElementDTO contactListElementDTO) {
		final ContactListElementJS contactListElementJS = Values.createJavaScriptObject(ContactListElementJS.class);

		contactListElementJS.setLimit(contactListElementDTO.getLimit());

		return contactListElementJS;
	}

	public ContactListElementDTO toContactListElementDTO() {
		final ContactListElementDTO contactListElementDTO = new ContactListElementDTO();

		contactListElementDTO.setLimit(getLimitInteger());

		return contactListElementDTO;
	}

	public native boolean hasLimit() /*-{
		return typeof this.limit != 'undefined';
	}-*/;

	public native int getLimit() /*-{
		return this.limit;
	}-*/;

	public Integer getLimitInteger() {
		if(hasLimit()) {
			return getLimit();
		}
		return null;
	}

	public native void setLimit(int limit) /*-{
		this.limit = limit;
	}-*/;

	public void setLimit(Integer limit) {
		if(limit != null) {
			setLimit(limit.intValue());
		}
	}
}
