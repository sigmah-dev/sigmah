package org.sigmah.server.domain.reminder;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Monitored Point List domain entity.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.MONITORED_POINT_LIST_TABLE)
public class MonitoredPointList extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 3500189016079098270L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.MONITORED_POINT_LIST_COLUMN_ID)
	private Integer id;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@OneToMany(mappedBy = "parentList", cascade = CascadeType.ALL)
	private List<MonitoredPoint> points = new ArrayList<MonitoredPoint>();

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	public void addMonitoredPoint(MonitoredPoint point) {
		if (point == null) {
			return;
		}
		point.setParentList(this);
		points.add(point);
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public List<MonitoredPoint> getPoints() {
		return points;
	}

	public void setPoints(List<MonitoredPoint> points) {
		this.points = points;
	}

}
