package org.sigmah.shared.dto.referential;

import org.sigmah.shared.command.result.Result;

/**
 * Reminder change types enumeration.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public enum ReminderChangeType implements Result {

	CREATED,
	DATE_MODIFIED,
	LABEL_MODIFIED,
	CLOSED,
	OPENED,
	DELETED;

}
