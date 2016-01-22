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

import org.sigmah.client.util.NumberUtils;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Widget;

/**
 * Ratio bar widget.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class RatioBar extends Widget {
    
    private static final String STYLE_OUTER = "blockStat";
    private static final String STYLE_INNER_LOW = "blockStatBgBlack";
    private static final String STYLE_INNER_MEDIUM = "blockStatBgYellow";
    private static final String STYLE_INNER_HIGH = "blockStatBgYellow";
    private static final String STYLE_INNER_VERY_HIGH = "blockStatBgRed";
    private static final String STYLE_INNER_UNDEFINED = "blockStatUndefined";

	private final DivElement innerDiv;
	private final DivElement outerDiv;

	public RatioBar(final double ratio) {

		innerDiv = Document.get().createDivElement();
		final int r = updateRatioStyle(ratio);

		outerDiv = Document.get().createDivElement();
		outerDiv.addClassName(STYLE_OUTER);
		outerDiv.appendChild(innerDiv);

		setElement(outerDiv);
		setTitle(r + "%");
	}

	public RatioBar(final double ratio, final String titleRatioLabel) {
		this(ratio);
		setTitle(getTitle() + titleRatioLabel);
	}

	private int updateRatioStyle(double ratio) {

		// Adjusts the ration.
		ratio = NumberUtils.adjustRatio(ratio);

		// Computes the style name.
		final String className;
		if (ratio > 60 && ratio < 80) {
			className = STYLE_INNER_MEDIUM;
		} else if (ratio >= 80 && ratio < 100) {
			className = STYLE_INNER_HIGH;
		} else if (ratio >= 100) {
			className = STYLE_INNER_VERY_HIGH;
		} else {
			className = STYLE_INNER_LOW;
		}

		final int ratioAsInt = (int) ratio;

		// Styles.
		innerDiv.setClassName(className);
		innerDiv.getStyle().setProperty("width", (ratioAsInt > 100 ? 100 : ratioAsInt) + "%");

		if (ratio >= 100) {
			innerDiv.setInnerText(ratioAsInt + "%");
		} else {
			innerDiv.setInnerText("");
		}

		return ratioAsInt;
	}
    
    public void setRatio(double ratio) {
        updateRatioStyle(ratio);
        setTitle(ratio + "%");
    }
    
    public void setRatioUndefined() {
        innerDiv.setClassName(STYLE_INNER_UNDEFINED);
        innerDiv.getStyle().setWidth(100, Style.Unit.PCT);
    }
}
