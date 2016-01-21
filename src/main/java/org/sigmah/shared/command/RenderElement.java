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

import org.sigmah.server.domain.element.ReportElement;
import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.StringResult;

/**
 * Renders a {@link org.sigmah.server.domain.element.ReportElement ReportElement} in the specified format, saves the
 * file to the server, and returns the name of the temporary file that can be used to initiate a download.
 *
 * @author Alex Bertram (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
// FIXME Should not reference server entity 'ReportElement'.
public class RenderElement extends AbstractCommand<StringResult> {

	public static enum Format {
		PNG,
		Excel,
		Excel_Data,
		PowerPoint,
		PDF,
		Word
	}

	private Format format;
	private ReportElement element;

	public RenderElement() {
		// Serialization.
	}

	public RenderElement(ReportElement element, Format format) {
		this.element = element;
		this.format = format;
	}

	/**
	 * @return The format into which to render the element.
	 */
	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	/**
	 * @return The element to be rendered
	 */
	public ReportElement getElement() {
		return element;
	}

	public void setElement(ReportElement element) {
		this.element = element;
	}
}
