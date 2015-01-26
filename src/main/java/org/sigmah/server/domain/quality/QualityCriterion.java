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
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.Organization;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Quality criterion domain entity.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.QUALITY_CRITERION_TABLE)
public class QualityCriterion extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -9015626378861486393L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.QUALITY_CRITERION_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.QUALITY_CRITERION_COLUMN_CODE, nullable = false, length = EntityConstants.QUALITY_CRITERION_CODE_MAX_LENGTH)
	@NotNull
	@Size(max = EntityConstants.QUALITY_CRITERION_CODE_MAX_LENGTH)
	private String code;

	@Column(name = EntityConstants.QUALITY_CRITERION_COLUMN_LABEL, nullable = false, columnDefinition = EntityConstants.COLUMN_DEFINITION_TEXT)
	@NotNull
	private String label;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(optional = true)
	@JoinColumn(name = EntityConstants.QUALITY_FRAMEWORK_COLUMN_ID, nullable = true)
	private QualityFramework qualityFramework;

	@ManyToOne(optional = true)
	@JoinTable(name = EntityConstants.QUALITY_CRITERION_SELF_LINK_TABLE, joinColumns = { @JoinColumn(name = EntityConstants.QUALITY_CRITERION_SELF_LINK_COLUMN)
	}, inverseJoinColumns = { @JoinColumn(name = EntityConstants.QUALITY_CRITERION_COLUMN_ID)
	}, uniqueConstraints = { @UniqueConstraint(columnNames = {
																														EntityConstants.QUALITY_CRITERION_COLUMN_ID,
																														EntityConstants.QUALITY_CRITERION_SELF_LINK_COLUMN
	})
	})
	private QualityCriterion parentCriterion;

	@OneToMany(mappedBy = "parentCriterion", cascade = CascadeType.ALL)
	private List<QualityCriterion> subCriteria;

	@ManyToOne
	@JoinColumn(name = EntityConstants.ORGANIZATION_COLUMN_ID)
	private Organization organization;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * Reset the identifiers of the object.
	 * 
	 * @param modelesReset
	 *          the map of the reseted objects (original object, transformed object).
	 * @param modelesImport
	 *          the list of object that have been transformed or are being transformed.
	 */
	public void resetImport(HashMap<Object, Object> modelesReset, HashSet<Object> modelesImport) {
		this.id = null;
		if (!modelesImport.contains(this)) {
			modelesImport.add(this);

			if (this.qualityFramework != null && !modelesImport.contains(this.qualityFramework)) {
				modelesImport.add(this.qualityFramework);
				if (!modelesReset.containsKey(this.qualityFramework)) {
					QualityFramework key = this.qualityFramework;
					this.qualityFramework.resetImport(modelesReset, modelesImport);
					modelesReset.put(key, this.qualityFramework);
				} else {
					this.qualityFramework = (QualityFramework) modelesReset.get(this.qualityFramework);
				}
			}

			if (this.parentCriterion != null && !modelesImport.contains(this.parentCriterion)) {
				modelesImport.add(this.parentCriterion);
				if (this.parentCriterion != null) {
					if (!modelesReset.containsKey(parentCriterion)) {
						QualityCriterion key = parentCriterion;
						parentCriterion.resetImport(modelesReset, modelesImport);
						modelesReset.put(key, parentCriterion);
					} else {
						parentCriterion = (QualityCriterion) modelesReset.get(parentCriterion);
					}

				}
			}

			if (this.subCriteria != null) {
				for (QualityCriterion qualityCriterion : subCriteria) {
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
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendToString(final ToStringBuilder builder) {
		builder.append("code", code);
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public QualityFramework getQualityFramework() {
		return qualityFramework;
	}

	public void setQualityFramework(QualityFramework qualityFramework) {
		this.qualityFramework = qualityFramework;
	}

	public QualityCriterion getParentCriterion() {
		return parentCriterion;
	}

	public void setParentCriterion(QualityCriterion parentCriterion) {
		this.parentCriterion = parentCriterion;
	}

	public List<QualityCriterion> getSubCriteria() {
		return subCriteria;
	}

	public void setSubCriteria(List<QualityCriterion> subCriteria) {
		this.subCriteria = subCriteria;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

}
