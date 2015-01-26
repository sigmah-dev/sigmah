package org.sigmah.shared.dto.pivot.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * <p>
 * Server-side version of the {@link org.sigmah.shared.util.DateRange} object.
 * </p>
 * <p>
 * This version adds {@code javax.xml.bind.annotation}s to the class (not supported by client-side).
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class DateRange extends org.sigmah.shared.util.DateRange {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 4567802237955870185L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@XmlAttribute(name = "min")
	public Date getMinDate() {
		return super.getMinDate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@XmlAttribute(name = "max")
	public Date getMaxDate() {
		return super.getMaxDate();
	}
}
