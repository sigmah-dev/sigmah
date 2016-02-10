package org.sigmah.server.domain.reminder;

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
import org.sigmah.server.domain.value.File;

/**
 * <p>
 * Monitored point domain entity.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.MONITORED_POINT_TABLE)
public class MonitoredPoint extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 3600773298461293280L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.MONITORED_POINT_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.MONITORED_POINT_COLUMN_LABEL, length = EntityConstants.MONITORED_POINT_LABEL_MAX_LENGTH, nullable = false)
	@NotNull
	@Size(max = EntityConstants.MONITORED_POINT_LABEL_MAX_LENGTH)
	private String label;

	@Column(name = EntityConstants.MONITORED_POINT_COLUMN_EXPECTED_DATE, nullable = false)
	@Temporal(value = TemporalType.TIMESTAMP)
	@NotNull
	private Date expectedDate;

	@Column(name = EntityConstants.MONITORED_POINT_COLUMN_COMPLETION_DATE, nullable = true)
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date completionDate;

	@Column(name = EntityConstants.MONITORED_POINT_COLUMN_DELETED)
	private Boolean deleted;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(optional = true)
	@JoinColumn(name = "id_file", nullable = true)
	private File file;

	@ManyToOne(optional = false)
	@JoinColumn(name = EntityConstants.MONITORED_POINT_COLUMN_MONITORED_POINT_LIST_ID, nullable = false)
	@NotNull
	private MonitoredPointList parentList;

	@OneToMany(mappedBy = "monitoredPoint", cascade = {
																											CascadeType.PERSIST,
																											CascadeType.MERGE,
																											CascadeType.REMOVE
	})
	private List<MonitoredPointHistory> history = new ArrayList<MonitoredPointHistory>(0);

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	@Transient
	public boolean isCompleted() {
		return completionDate != null;
	}

	public void addHistory(final MonitoredPointHistory hist) {
		hist.setMonitoredPoint(this);
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

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public MonitoredPointList getParentList() {
		return parentList;
	}

	public void setParentList(MonitoredPointList parentList) {
		this.parentList = parentList;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public List<MonitoredPointHistory> getHistory() {
		return history;
	}

	public void setHistory(List<MonitoredPointHistory> history) {
		this.history = history;
	}

}
