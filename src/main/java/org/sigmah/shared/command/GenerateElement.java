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

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.Content;
import org.sigmah.shared.dto.pivot.model.ReportElement;

/**
 * <p>
 * Generates and returns to the client the content of an element.
 * </p>
 * <p>
 * Returns: {@link org.sigmah.shared.command.result.Content}.
 * </p>
 *
 * @author Alex Bertram (akbertram@gmail.com)
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @param <T> Type of content to generate.
 */
public class GenerateElement<T extends Content> extends AbstractCommand<T> {

	private ReportElement element;

	protected GenerateElement() {
		// Serialization.
	}

	public GenerateElement(ReportElement element) {
		this.element = element;
	}

	public ReportElement getElement() {
		return element;
	}

	public void setElement(ReportElement element) {
		this.element = element;
	}
}
