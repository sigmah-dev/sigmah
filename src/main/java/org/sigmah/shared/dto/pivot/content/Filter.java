package org.sigmah.shared.dto.pivot.content;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.sigmah.server.report.model.adapter.FilterAdapter;
import org.sigmah.shared.util.DateRange;

/**
 * <p>
 * Server-side version of the {@link org.sigmah.shared.util.Filter} object.
 * </p>
 * <p>
 * This version adds {@code javax.xml.bind.annotation}s to the class (not supported by client-side).
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
@XmlJavaTypeAdapter(FilterAdapter.class)
public class Filter extends org.sigmah.shared.util.Filter {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -7973838332936879586L;

	@Override
	@XmlTransient
	public Date getMinDate() {
		return super.getMinDate();
	}

	@Override
	@XmlTransient
	public Date getMaxDate() {
		return super.getMaxDate();
	}

	@Override
	@XmlElement
	public DateRange getDateRange() {
		return super.getDateRange();
	}

}
