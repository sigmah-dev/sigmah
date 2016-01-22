package org.sigmah.client.ui.res.icon.orgunit;

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
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ImageBundle;

/**
 * Provides access to the application's icons through GWT's magic ImageBundle generator.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@SuppressWarnings("deprecation")
public interface OrgUnitImageBundle extends ImageBundle {

	final OrgUnitImageBundle ICONS = (OrgUnitImageBundle) GWT.create(OrgUnitImageBundle.class);

	@Resource(value = "orgunit.png")
	AbstractImagePrototype orgUnitLarge();

	@Resource(value = "orgunit2.png")
	AbstractImagePrototype orgUnitSmall();

	@Resource(value = "orgunit3.png")
	AbstractImagePrototype orgUnitSmallTransparent();

}
