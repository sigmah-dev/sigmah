package org.sigmah.client.ui.res.icon.dashboard.funding;

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

import org.sigmah.shared.dto.referential.ProjectModelType;

import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * Utility class to get icons for the different project type.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class FundingIconProvider {

	/**
	 * Provides only static methods.
	 */
	private FundingIconProvider() {
	}

	/**
	 * Defines the available size for these icons.
	 * 
	 * @author tmi
	 */
	public static enum IconSize {
		SMALL,
		SMALL_MEDIUM,
		MEDIUM,
		LARGE;
	}

	/**
	 * Gets the small icon for the given type.
	 * 
	 * @param type
	 *          The type.
	 * @return the small icon.
	 */
	public static AbstractImagePrototype getProjectTypeIcon(ProjectModelType type) {
		return getProjectTypeIcon(type, IconSize.SMALL);
	}

	/**
	 * Gets the icon for the given type at the given size.
	 * 
	 * @param type
	 *          The type.
	 * @param size
	 *          The size.
	 * @return The icon.
	 */
	public static AbstractImagePrototype getProjectTypeIcon(ProjectModelType type, IconSize size) {

		if (type == null) {
			throw new IllegalArgumentException("The type must not be null.");
		}

		switch (type) {
			case FUNDING:
				switch (size) {
					case MEDIUM:
						return FundingIconImageBundle.ICONS.fundingMedium();
					case LARGE:
						return FundingIconImageBundle.ICONS.fundingLarge();
					case SMALL_MEDIUM:
						return FundingIconImageBundle.ICONS.fundingMediumTransparent();
					default:
						return FundingIconImageBundle.ICONS.fundingSmall();
				}
			case LOCAL_PARTNER:
				switch (size) {
					case MEDIUM:
						return FundingIconImageBundle.ICONS.localPartnerMedium();
					case LARGE:
						return FundingIconImageBundle.ICONS.localPartnerLarge();
					case SMALL_MEDIUM:
						return FundingIconImageBundle.ICONS.localPartnerMediumTransparent();
					default:
						return FundingIconImageBundle.ICONS.localPartnerSmall();
				}
			case NGO:
				switch (size) {
					case MEDIUM:
						return FundingIconImageBundle.ICONS.ngoMedium();
					case LARGE:
						return FundingIconImageBundle.ICONS.ngoLarge();
					case SMALL_MEDIUM:
						return FundingIconImageBundle.ICONS.ngoMediumTransparent();
					default:
						return FundingIconImageBundle.ICONS.ngoSmall();
				}
			default:
				return FundingIconImageBundle.ICONS.ngoSmall();
		}
	}
}
