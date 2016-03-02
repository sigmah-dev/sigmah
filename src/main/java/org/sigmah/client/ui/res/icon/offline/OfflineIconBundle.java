package org.sigmah.client.ui.res.icon.offline;

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
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface OfflineIconBundle extends ClientBundle {
	
	public static final OfflineIconBundle INSTANCE = GWT.create(OfflineIconBundle.class);
	
	ImageResource signalOff();
	ImageResource signalOn();
	
	ImageResource connect();
	ImageResource disconnect();
	
	ImageResource error();
	ImageResource traceOn();
	ImageResource traceOff();
}
