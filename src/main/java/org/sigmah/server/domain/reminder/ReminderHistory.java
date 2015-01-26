package org.sigmah.server.domain.reminder;

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
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.shared.dto.referential.ReminderChangeType;

/**
 * <p>
 * Reminder history domain entity.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.REMINDER_HISTORY_TABLE)
public class ReminderHistory extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 6420022855036073653L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.REMINDER_HISTORY_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.REMINDER_HISTORY_COLUMN_USER_ID, nullable = false)
	@NotNull
	private Integer userId;

	@Column(name = EntityConstants.REMINDER_HISTORY_COLUMN_DATE, nullable = false)
	@Temporal(value = TemporalType.TIMESTAMP)
	@NotNull
	private Date date;

	@Column(name = EntityConstants.REMINDER_HISTORY_COLUMN_TYPE, nullable = false)
	@Enumerated(value = EnumType.STRING)
	@NotNull
	private ReminderChangeType type;

	@Column(name = EntityConstants.REMINDER_HISTORY_COLUMN_VALUE, nullable = true, columnDefinition = EntityConstants.COLUMN_DEFINITION_TEXT)
	private String value;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne
	@JoinColumn(name = EntityConstants.REMINDER_COLUMN_ID, nullable = false)
	@NotNull
	private Reminder reminder;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		super.appendToString(builder);
		builder.append("userId", userId);
		builder.append("type", type);
		builder.append("date", date);
		builder.append("value", value);
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public ReminderChangeType getType() {
		return type;
	}

	public void setType(ReminderChangeType type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Reminder getReminder() {
		return reminder;
	}

	public void setReminder(Reminder reminder) {
		this.reminder = reminder;
	}

}
