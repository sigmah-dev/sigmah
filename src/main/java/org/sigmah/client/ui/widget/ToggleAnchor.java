package org.sigmah.client.ui.widget;

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

import com.google.gwt.user.client.ui.HTML;

/**
 * Simple anchor that can be turned into a label and vice versa.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ToggleAnchor extends HTML {

	private String label;
	private String target;
	private String className;
	private boolean anchorMode;

	/**
	 * Creates a new ToggleAnchor and initialize its target to "<code>javascript:;</code>".
	 * 
	 * @param html
	 *          Label of the new link in HTML format
	 */
	public ToggleAnchor(String html) {
		this.label = html;
		this.target = "javascript:;";
		this.anchorMode = false;
		this.className = "";
	}

	/**
	 * Returns the current label of this anchor.
	 * 
	 * @return The current label in HTML format.
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Defines the label of this anchor.
	 * 
	 * @param label
	 *          The new value to display.
	 */
	public void setLabel(String label) {
		this.label = label;
		setAnchorMode(anchorMode);
	}

	/**
	 * Get the class of the anchor
	 * 
	 * @return The anchor class name.
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Set the class of the anchor
	 * 
	 * @param className
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * Retrieves the current target URL of this anchor.
	 * 
	 * @return The target URL in String.
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * Defines the target URL of this anchor.
	 * 
	 * @param target
	 *          The new URL in String.
	 */
	public void setTarget(String target) {
		this.target = target;
		setAnchorMode(anchorMode);
	}

	/**
	 * Tells wether or not this widget is in anchor mode.
	 * 
	 * @return <code>true</code> if this widget is a link, <code>false</code> otherwise.
	 */
	public boolean isAnchorMode() {
		return anchorMode;
	}

	/**
	 * Changes the anchor mode of this widget.
	 * 
	 * @param anchorMode
	 *          <code>true</code> to display an anchor, <code>false</code> otherwise.
	 */
	public void setAnchorMode(boolean anchorMode) {
		this.anchorMode = anchorMode;

		if (anchorMode) {
			setHTML("<a href=\"" + target + "\" class=\"" + className + "\">" + label + "</a>");
		} else {
			setHTML(label);
		}
	}

	/**
	 * Reverse the anchor mode of this widget.
	 */
	public void toggleAnchorMode() {
		setAnchorMode(!this.anchorMode);
	}

	/*
	 * Sets a given style for the HTML. Keeps the anchor hyperlink is present.
	 */
	public void setHTMLStyle(String style) {
		if (anchorMode) {
			setHTML("<p style=\"" + style + "\">" + "<a href=\"" + target + "\" class=\"" + className + "\">" + label + "</a>" + "</p>");
		} else {
			setHTML("<p style=\"" + style + "\">" + label + "</p>");
		}
	}
}
