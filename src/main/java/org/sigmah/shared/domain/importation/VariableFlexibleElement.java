package org.sigmah.shared.domain.importation;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Filter;
import org.sigmah.shared.domain.Deleteable;
import org.sigmah.shared.domain.element.FlexibleElement;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "importation_scheme_variable_flexible_element")
@Filter(name = "hideDeleted", condition = "DateDeleted is null")
public class VariableFlexibleElement implements Serializable, Deleteable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8601037728276093624L;

	private Long id;
	private Variable variable;
	private FlexibleElement flexibleElement;
	private ImportationSchemeModel importationSchemeModel;
	private Boolean isKey;
	private Date dateDeleted;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "var_fle_id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long Id) {
		this.id = Id;
	}

	@Column(name = "var_fle_is_key")
	public Boolean getIsKey() {
		return isKey;
	}

	public void setIsKey(Boolean isKey) {
		this.isKey = isKey;
	}

	@ManyToOne
	@JoinColumn(name = "var_id", updatable = false)
	public Variable getVariable() {
		return variable;
	}

	public void setVariable(Variable variable) {
		this.variable = variable;
	}

	@ManyToOne(optional = false)
	@JoinColumn(name = "id_flexible_element", nullable = false, updatable = false)
	public FlexibleElement getFlexibleElement() {
		return flexibleElement;
	}

	public void setFlexibleElement(FlexibleElement flexibleElement) {
		this.flexibleElement = flexibleElement;
	}

	@ManyToOne(optional = false)
	@JoinColumn(name = "sch_mod_id", nullable = false, updatable = false)
	public ImportationSchemeModel getImportationSchemeModel() {
		return importationSchemeModel;
	}

	public void setImportationSchemeModel(ImportationSchemeModel importationSchemeModel) {
		this.importationSchemeModel = importationSchemeModel;
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
