package org.sigmah.server.domain.quality;

import java.util.HashMap;
import java.util.HashSet;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.Organization;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Quality framework domain entity.
 * </p>
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.QUALITY_FRAMEWORK_TABLE)
public class QualityFramework extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -3263048667330191985L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.QUALITY_FRAMEWORK_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.QUALITY_FRAMEWORK_COLUMN_LABEL, nullable = false, length = EntityConstants.QUALITY_FRAMEWORK_LABEL_MAX_LENGTH)
	@NotNull
	@Size(max = EntityConstants.QUALITY_FRAMEWORK_LABEL_MAX_LENGTH)
	private String label;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@OneToMany(mappedBy = "qualityFramework", cascade = CascadeType.ALL)
	private List<CriterionType> types;

	@OneToMany(mappedBy = "qualityFramework", cascade = CascadeType.ALL)
	private List<QualityCriterion> criteria;

	@ManyToOne
	@JoinColumn(name = EntityConstants.ORGANIZATION_COLUMN_ID)
	private Organization organization;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * Reset the object identifier.
	 * 
	 * @param modelesReset
	 *          the map of the reseted objects (original object, transformed object).
	 * @param modelesImport
	 *          the list of object that have been transformed or are being transformed.
	 */
	public void resetImport(HashMap<Object, Object> modelesReset, HashSet<Object> modelesImport) {
		this.id = null;

		if (this.types != null) {
			for (CriterionType cryCriterionType : types) {
				if (!modelesImport.contains(cryCriterionType)) {
					modelesImport.add(cryCriterionType);
					if (!modelesReset.containsKey(cryCriterionType)) {
						CriterionType key = cryCriterionType;
						cryCriterionType.resetImport(this);
						modelesReset.put(key, cryCriterionType);
					} else {
						cryCriterionType = (CriterionType) modelesReset.get(cryCriterionType);
					}
				}
			}
		}

		if (this.criteria != null) {
			for (QualityCriterion qualityCriterion : criteria) {
				if (!modelesImport.contains(qualityCriterion)) {
					modelesImport.add(qualityCriterion);
					if (!modelesReset.containsKey(qualityCriterion)) {
						QualityCriterion key = qualityCriterion;
						qualityCriterion.resetImport(modelesReset, modelesImport);
						modelesReset.put(key, qualityCriterion);
					} else {
						qualityCriterion = (QualityCriterion) modelesReset.get(qualityCriterion);
					}
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendToString(final ToStringBuilder builder) {
		builder.append("label", label);
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public Integer getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<CriterionType> getTypes() {
		return types;
	}

	public void setTypes(List<CriterionType> types) {
		this.types = types;
	}

	public List<QualityCriterion> getCriteria() {
		return criteria;
	}

	public void setCriteria(List<QualityCriterion> criteria) {
		this.criteria = criteria;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

}
