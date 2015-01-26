package org.sigmah.shared.dto.reminder;

import java.util.List;

import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

/**
 * DTO mapping class for entity reminder.MonitoredPointList.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class MonitoredPointListDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1001655394559887157L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return "reminder.MonitoredPointList";
	}

	// Monitored points
	public List<MonitoredPointDTO> getPoints() {
		return get("points");
	}

	public void setPoints(List<MonitoredPointDTO> points) {
		set("points", points);
	}
}
