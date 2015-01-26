package org.sigmah.server.domain.importation;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Filter;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.Deleteable;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.server.domain.util.EntityFilters;

/**
 * <p>
 * Variable domain entity.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.IMPORTATION_VARIABLE_TABLE)
@Filter(name = EntityFilters.HIDE_DELETED, condition = EntityFilters.IMPORTATION_VARIABLE_HIDE_DELETED_CONDITION)
public class Variable extends AbstractEntityId<Integer> implements Deleteable {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8635303324640543633L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.IMPORTATION_VARIABLE_COLUMN_ID, unique = true, nullable = false)
	private Integer id;

	@Column(name = EntityConstants.IMPORTATION_VARIABLE_COLUMN_NAME, nullable = false)
	@NotNull
	private String name;

	@Column(name = EntityConstants.IMPORTATION_VARIABLE_COLUMN_REFERENCE, nullable = false)
	@NotNull
	private String reference;

	@Column(name = EntityConstants.COLUMN_DATE_DELETED)
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date dateDeleted;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = EntityConstants.IMPORTATION_SCHEME_COLUMN_ID, nullable = false)
	@NotNull
	private ImportationScheme importationScheme;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete() {
		setDateDeleted(new Date());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transient
	public boolean isDeleted() {
		return getDateDeleted() != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("name", name);
		builder.append("reference", reference);
		builder.append("dateDeleted", dateDeleted);
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public ImportationScheme getImportationScheme() {
		return importationScheme;
	}

	public void setImportationScheme(ImportationScheme importationScheme) {
		this.importationScheme = importationScheme;
	}

	public Date getDateDeleted() {
		return dateDeleted;
	}

	public void setDateDeleted(Date dateDeleted) {
		this.dateDeleted = dateDeleted;
	}

}
