package org.sigmah.offline.js;

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
