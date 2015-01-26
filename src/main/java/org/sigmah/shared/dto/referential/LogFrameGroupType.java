package org.sigmah.shared.dto.referential;

import org.sigmah.shared.command.result.Result;

/**
 * The different types of log frame groups.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public enum LogFrameGroupType implements Result {

	SPECIFIC_OBJECTIVE,
	EXPECTED_RESULT,
	ACTIVITY,
	PREREQUISITE;

}
