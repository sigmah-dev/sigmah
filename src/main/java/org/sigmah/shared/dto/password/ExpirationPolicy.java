package org.sigmah.shared.dto.password;

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
import java.util.Date;

import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.dto.password.PasswordExpirationPolicyDTO;

/**
 * Enumeration for password expiration policies.
 * 
 * @author Renato Almeida (renatoaf.ufcg@gmail.com)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public enum ExpirationPolicy {
	
	WEEKLY {
		@Override
		public boolean isExpired(PasswordExpirationPolicyDTO policyContext, Date lastChange) {
			if (policyContext.getFrequency() <= 0) {
				return false;
			}
			Date currentDate = new Date();
			return currentDate.getTime() - lastChange.getTime() > ( WEEK * policyContext.getFrequency() );
		}
	},
	
	MONTHLY {
		@Override
		public boolean isExpired(PasswordExpirationPolicyDTO policyContext, Date lastChange) {
			if (policyContext.getFrequency() <= 0) {
				return false;
			}
			Date currentDate = new Date();
			return currentDate.getTime() - lastChange.getTime() > ( MONTH * policyContext.getFrequency() );
		}
	},
	
	YEARLY {
		@Override
		public boolean isExpired(PasswordExpirationPolicyDTO policyContext, Date lastChange) {
			if (policyContext.getFrequency() <= 0) {
				return false;
			}
			Date currentDate = new Date();
			return currentDate.getTime() - lastChange.getTime() > ( YEAR * policyContext.getFrequency() );
		}
	},
	
	SCHEDULED {
		@Override
		public boolean isExpired(PasswordExpirationPolicyDTO policyContext, Date lastChange) {
			return lastChange.before(policyContext.getReferenceDate());
		}
	},
	
	NEVER {
		@Override
		public boolean isExpired(PasswordExpirationPolicyDTO policyContext, Date date) {
			return false;
		}
	};
	
	private static final String DEFAULT = "default";

	private static final long SECOND = 1000;
	private static final long MINUTE = 60 * SECOND;
	private static final long HOUR = 60 * MINUTE;
	private static final long DAY = 24 * HOUR;
	private static final long WEEK = 7 * DAY;
	private static final long MONTH = 30 * DAY;
	private static final long YEAR = 365 * DAY;
	
	public abstract boolean isExpired(PasswordExpirationPolicyDTO policyContext, Date lastChange);
	
	public static String getName(ExpirationPolicy policy) {
		final String policyName;
		
		switch (policy) {
			case WEEKLY:
				policyName = I18N.CONSTANTS.weekly();
				break;
			case MONTHLY:
				policyName = I18N.CONSTANTS.monthly();
				break;
			case YEARLY:
				policyName = I18N.CONSTANTS.yearly();
				break;
			case NEVER:
				policyName = I18N.CONSTANTS.never();
				break;
			case SCHEDULED:
				policyName = I18N.CONSTANTS.scheduled();
				break;
			default:
				policyName = DEFAULT;
		}
		
		return policyName;
	}

	public static String getFrequencyName(ExpirationPolicy policy) {
		final String frequencyName;
		switch (policy) {
			case WEEKLY:
				frequencyName = I18N.CONSTANTS.weeks();
				break;
			case MONTHLY:
				frequencyName = I18N.CONSTANTS.months();
				break;
			case YEARLY:
				frequencyName = I18N.CONSTANTS.years();
				break;
			default:
				frequencyName = DEFAULT;
		}
		
		return frequencyName;
	}
	
	@Override
	public String toString() {
		if(GWT.isClient()) {
			return getName(this);
		} else {
			return name();
		}
	}

	public boolean requiresReferenceDate() {
		return this == SCHEDULED;
	}

	public boolean requiresFrequency() {
		return this == WEEKLY || this == MONTHLY || this == YEARLY;
	}
}
