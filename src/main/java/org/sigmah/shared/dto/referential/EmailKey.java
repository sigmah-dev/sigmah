package org.sigmah.shared.dto.referential;

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

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A key which is used by the mail service to replace strings before sending.
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public interface EmailKey extends IsSerializable {

	/**
	 * Gets the key.
	 * 
	 * @return The key.
	 */
	String getKey();

	/**
	 * Returns if this parameter can be stored in the date layer or not.
	 * 
	 * @return If this parameter can be stored in the date layer or not.
	 */
	boolean isSafe();

}
