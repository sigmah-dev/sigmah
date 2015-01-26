package org.sigmah.server.domain.value;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.Filters;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.Deleteable;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.server.domain.util.EntityFilters;

/**
 * <p>
 * Triplet Value domain entity.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.TRIPLETS_VALUE_TABLE)
@FilterDefs({ @FilterDef(name = EntityFilters.HIDE_DELETED)
})
@Filters({ @Filter(name = EntityFilters.HIDE_DELETED, condition = EntityFilters.TRIPLET_VALUE_HIDE_DELETED_CONDITION)
})
public class TripletValue extends AbstractEntityId<Integer> implements Deleteable {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -6149053567281316649L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.TRIPLETS_VALUE_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.TRIPLETS_VALUE_COLUMN_CODE, nullable = false, columnDefinition = EntityConstants.COLUMN_DEFINITION_TEXT)
	@NotNull
	private String code;

	@Column(name = EntityConstants.TRIPLETS_VALUE_COLUMN_NAME, nullable = false, columnDefinition = EntityConstants.COLUMN_DEFINITION_TEXT)
	@NotNull
	private String name;

	@Column(name = EntityConstants.TRIPLETS_VALUE_COLUMN_PERIOD, nullable = false, columnDefinition = EntityConstants.COLUMN_DEFINITION_TEXT)
	@NotNull
	private String period;

	@Column(name = EntityConstants.TRIPLETS_VALUE_COLUMN_DATE_DELETED)
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date dateDeleted;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------
	@Override
	protected void appendToString(ToStringBuilder builder) {
		builder.append("code", code);
		builder.append("name", name);
		builder.append("period", period);
		builder.append("dateDeleted", dateDeleted);
	}

	/**
	 * Marks this database as deleted. (Though the row is not removed from the database)
	 */
	@Override
	public void delete() {
		setDateDeleted(new Date());
	}

	/**
	 * @return True if this database was deleted by its owner.
	 */
	@Override
	@Transient
	public boolean isDeleted() {
		return getDateDeleted() != null;
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	/**
	 * @return The date on which this database was deleted by the user, or null if this database is not deleted.
	 */
	public Date getDateDeleted() {
		return this.dateDeleted;
	}

	protected void setDateDeleted(Date date) {
		this.dateDeleted = date;
	}

}
