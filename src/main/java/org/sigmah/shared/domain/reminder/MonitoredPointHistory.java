package org.sigmah.shared.domain.reminder;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "monitored_point_history")
public class MonitoredPointHistory implements Serializable {

	private static final long serialVersionUID = 1784565851559026850L;

	private Integer id;
	private Integer userId;

	private MonitoredPoint monitoredPoint;

	private Date date;
	private ReminderChangeType type;
	private String value;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_monitored_point_history")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "generated_date", nullable = false)
	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Column(name = "id_user", nullable = false)
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	@Column(name = "change_type", nullable = false)
	@Enumerated(value = EnumType.STRING)
	public ReminderChangeType getType() {
		return type;
	}

	public void setType(ReminderChangeType type) {
		this.type = type;
	}

	@Column(name = "value", nullable = true, columnDefinition = "TEXT")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@ManyToOne
	@JoinColumn(name = "id_monitored_point", nullable = false)
	public MonitoredPoint getMonitoredPoint() {

		return monitoredPoint;
	}

	public void setMonitoredPoint(MonitoredPoint monitoredPoint) {

		this.monitoredPoint = monitoredPoint;

	}
}
