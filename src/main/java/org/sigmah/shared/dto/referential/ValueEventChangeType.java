package org.sigmah.shared.dto.referential;

import org.sigmah.shared.command.result.Result;

/**
 * Value change event types enumeration.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public enum ValueEventChangeType implements Result {

	ADD,
	REMOVE,
	EDIT;

}
