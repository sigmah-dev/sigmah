package org.sigmah.shared.domain.importation;

import java.io.Serializable;
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

import org.hibernate.annotations.Filter;
import org.sigmah.shared.domain.Deleteable;
import org.sigmah.shared.domain.OrgUnitModel;
import org.sigmah.shared.domain.ProjectModel;

@Entity
@Table(name = "importation_scheme_model")
@Filter(name = "hideDeleted", condition = "DateDeleted is null")
public class ImportationSchemeModel implements Serializable, Deleteable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4157572319565532207L;

	private Long id;
	private ImportationScheme importationScheme;
	private ProjectModel projectModel;
	private OrgUnitModel orgUnitModel;
	private List<VariableFlexibleElement> variableFlexibleElements = new ArrayList<VariableFlexibleElement>();
	private Date dateDeleted;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "sch_mod_id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(optional = false)
	@JoinColumn(name = "sch_id", nullable = false, updatable = false)
	public ImportationScheme getImportationScheme() {
		return importationScheme;
	}

	public void setImportationScheme(ImportationScheme importationScheme) {
		this.importationScheme = importationScheme;
	}

	@ManyToOne
	@JoinColumn(name = "id_project_model", updatable = false)
	public ProjectModel getProjectModel() {
		return projectModel;
	}

	public void setProjectModel(ProjectModel projectModel) {
		this.projectModel = projectModel;
	}

	/**
	 * @return the orgUnitModel
	 */
	@ManyToOne
	@JoinColumn(name = "org_unit_model_id", updatable = false)
	public OrgUnitModel getOrgUnitModel() {
		return orgUnitModel;
	}

	/**
	 * @param orgUnitModel
	 *            the orgUnitModel to set
	 */
	public void setOrgUnitModel(OrgUnitModel orgUnitModel) {
		this.orgUnitModel = orgUnitModel;
	}

	@OneToMany(mappedBy = "importationSchemeModel", cascade = CascadeType.ALL)
	@Filter(name = "hideDeleted", condition = "DateDeleted is null")
	public List<VariableFlexibleElement> getVariableFlexibleElements() {
		return variableFlexibleElements;
	}

	public void setVariableFlexibleElements(List<VariableFlexibleElement> variableFlexibleElements) {
		this.variableFlexibleElements = variableFlexibleElements;
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
