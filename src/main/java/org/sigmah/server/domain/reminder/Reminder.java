package org.sigmah.server.domain.reminder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Reminder domain entity.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.REMINDER_TABLE)
public class Reminder extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 2360748872630231054L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.REMINDER_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.REMINDER_COLUMN_LABEL, length = EntityConstants.REMINDER_LABEL_MAX_LENGTH, nullable = false)
	@NotNull
	@Size(max = EntityConstants.REMINDER_LABEL_MAX_LENGTH)
	private String label;

	@Column(name = EntityConstants.REMINDER_COLUMN_EXPECTED_DATE, nullable = false)
	@Temporal(value = TemporalType.TIMESTAMP)
	@NotNull
	private Date expectedDate;

	@Column(name = EntityConstants.REMINDER_COLUMN_COMPLETION_DATE, nullable = true)
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date completionDate;

	@Column(name = EntityConstants.REMINDER_COLUMN_DELETED)
	private Boolean deleted;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(optional = false)
	@JoinColumn(name = EntityConstants.REMINDER_COLUMN_REMINDER_LIST_ID, nullable = false)
	@NotNull
	private ReminderList parentList;

	@OneToMany(mappedBy = "reminder", cascade = {
																								CascadeType.PERSIST,
																								CascadeType.MERGE,
																								CascadeType.REMOVE
	})
	private List<ReminderHistory> history = new ArrayList<ReminderHistory>(0);

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	@Transient
	public boolean isCompleted() {
		return completionDate != null;
	}

	public void addHistory(final ReminderHistory hist) {
		hist.setReminder(this);
		history.add(hist);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("label", label);
		builder.append("expectedDate", expectedDate);
		builder.append("completionDate", completionDate);
		builder.append("deleted", deleted);
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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Date getExpectedDate() {
		return expectedDate;
	}

	public void setExpectedDate(Date expectedDate) {
		this.expectedDate = expectedDate;
	}

	public Date getCompletionDate() {
		return completionDate;
	}

	public void setCompletionDate(Date completionDate) {
		this.completionDate = completionDate;
	}

	public ReminderList getParentList() {
		return parentList;
	}

	public void setParentList(ReminderList parentList) {
		this.parentList = parentList;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public List<ReminderHistory> getHistory() {
		return history;
	}

	public void setHistory(List<ReminderHistory> history) {
		this.history = history;
	}

}
