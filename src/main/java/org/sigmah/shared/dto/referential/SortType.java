package org.sigmah.shared.dto.referential;

import org.sigmah.shared.command.result.Result;

/**
 * Sort types enumeration.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public enum SortType implements Result {

	DEFINED,
	NATURAL_VALUE,
	NATURAL_LABEL,
	CUSTOM;

}
