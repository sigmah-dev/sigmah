package org.sigmah.offline.js;

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

import org.sigmah.shared.dto.profile.PrivacyGroupDTO;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class PrivacyGroupJsMapBoxer implements AutoBoxingJsMap.Boxer<PrivacyGroupDTO> {
	@Override
	public String toString(PrivacyGroupDTO object) {
		return new StringBuilder()
			.append(object.getId())
			.append('-')
			.append(object.getCode())
			.append('-')
			.append(object.getTitle())
			.toString();
	}

	@Override
	public PrivacyGroupDTO fromString(String string) {
		final PrivacyGroupDTO privacyGroupDTO = new PrivacyGroupDTO();

		int end = string.indexOf('-');
		if(end == -1) {
			return null;
		}
		try {
			privacyGroupDTO.setId(Integer.parseInt(string.substring(0, end)));
		} catch(NumberFormatException e) {
		}

		int start = end + 1;
		end = string.indexOf(string, start);
		try {
			privacyGroupDTO.setCode(Integer.parseInt(string.substring(start, end)));
		} catch(NumberFormatException e) {
		}

		start = end + 1;
		privacyGroupDTO.setTitle(string.substring(start));

		return privacyGroupDTO;
	}
}
