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
@Table(name = "reminder_history")
public class ReminderHistory implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6420022855036073653L;

	private Integer id;
	private Integer userId;

	private Reminder reminder;

	private Date date;
	private ReminderChangeType type;
	private String value;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_reminder_history")
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
	@JoinColumn(name = "id_reminder", nullable = false)
	public Reminder getReminder() {

		return reminder;
	}

	public void setReminder(Reminder reminder) {

		this.reminder = reminder;

	}

}
