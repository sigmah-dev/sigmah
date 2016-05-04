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

import org.sigmah.client.page.Page;
import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.SecureNavigationResult;

/**
 * <p>
 * Secures the navigation to a given page.
 * </p>
 *
 * This command returns two information:
 * <ul>
 * <li>A boolean flag that grants or refuses the access to the page.</li>
 * <li>An updated instance of {@link org.sigmah.shared.command.result.Authentication Authentication}.</li>
 * </ul>
 *
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class SecureNavigationCommand extends AbstractCommand<SecureNavigationResult> {

	/**
	 * Page user is trying to access.
	 */
	private Page page;

	/**
	 * Empty constructor necessary for RPC serialization.
	 */
	public SecureNavigationCommand() {
		// Serialization.
	}

	/**
	 * Initializes a new command securing page navigation.
	 * 
	 * @param page
	 *          The page the user is trying to access.
	 */
	public SecureNavigationCommand(final Page page) {
		this.page = page;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("page", page);
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}
}
