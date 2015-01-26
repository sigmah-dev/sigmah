package org.sigmah.shared.domain.importation;

import java.io.Serializable;
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
import javax.persistence.Transient;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Filter;
import org.sigmah.shared.domain.Deleteable;

@Entity
@Table(name = "importation_scheme_variable")
@Filter(name = "hideDeleted", condition = "DateDeleted is null")
public class Variable implements Serializable, Deleteable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8635303324640543633L;

	private Long id;
	private String name;
	private String reference;
	private ImportationScheme importationScheme;
	private Date dateDeleted;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "var_id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "var_name", nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "var_reference", nullable = false)
	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "sch_id", nullable = false)
	public ImportationScheme getImportationScheme() {
		return importationScheme;
	}

	public void setImportationScheme(ImportationScheme importationScheme) {
		this.importationScheme = importationScheme;
	}

	@Column(name = "datedeleted")
	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDateDeleted() {
		return dateDeleted;
	}

	public void setDateDeleted(Date dateDeleted) {
		this.dateDeleted = dateDeleted;
	}

	@Override
	public void delete() {
		Date now = new Date();
		setDateDeleted(now);
	}

	@Override
	@Transient
	public boolean isDeleted() {
		return getDateDeleted() != null;
	}

}
