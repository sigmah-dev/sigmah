package org.sigmah.client.ui.res.icon.reports;

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

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Toolbar images.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface ToolbarImages extends ClientBundle {

	/**
	 * Instance created by GWT.
	 */
	public static ToolbarImages IMAGES = GWT.create(ToolbarImages.class);

	@Source("text_align_left.png")
	ImageResource textAlignLeft();

	@Source("text_align_center.png")
	ImageResource textAlignCenter();

	@Source("text_align_right.png")
	ImageResource textAlignRight();

	@Source("text_align_justify.png")
	ImageResource textAlignJustify();

	@Source("text_bold.png")
	ImageResource textBold();

	@Source("text_italic.png")
	ImageResource textItalic();

	@Source("text_underline.png")
	ImageResource textUnderline();

	@Source("text_strikethrough.png")
	ImageResource textStrike();

	@Source("text_horizontalrule.png")
	ImageResource textHorizontalRule();

	@Source("text_list_bullets.png")
	ImageResource textListBullets();

	@Source("text_list_numbers.png")
	ImageResource textListNumbers();

	@Source("image_add.png")
	ImageResource imageAdd();

	@Source("compas.png")
	ImageResource compas();

	@Source("compas_green.png")
	ImageResource compasGreen();

	@Source("compas_red.png")
	ImageResource compasRed();
}
