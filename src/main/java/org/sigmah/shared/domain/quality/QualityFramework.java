package org.sigmah.shared.domain.quality;

import java.io.Serializable;
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

import org.sigmah.shared.domain.Organization;

/**
 * Quality framework entity.
 * 
 * @author tmi
 * 
 */
@Entity
@Table(name = "quality_framework")
public class QualityFramework implements Serializable {

    private static final long serialVersionUID = -3263048667330191985L;

    private Integer id;
    private String label;
    private List<CriterionType> types;
    private List<QualityCriterion> criteria;
    private Organization organization;
    
    public void setId(Integer id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_quality_framework")
    public Integer getId() {
        return id;
    }

    @Column(name = "label", nullable = false, length = 8192)
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @OneToMany(mappedBy = "qualityFramework", cascade = CascadeType.ALL)
    public List<CriterionType> getTypes() {
        return types;
    }

    public void setTypes(List<CriterionType> types) {
        this.types = types;
    }

    @OneToMany(mappedBy = "qualityFramework", cascade = CascadeType.ALL)
    public List<QualityCriterion> getCriteria() {
        return criteria;
    }

    public void setCriteria(List<QualityCriterion> criteria) {
        this.criteria = criteria;
    }
    
    @ManyToOne
    @JoinColumn(name = "id_organization") 
    public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
    
    /**
	 * Reset the object identifier.
	 * 
	 * @param modelesReset
	 *            the map of the reseted objects (original object, transformed
	 *            object).
	 * @param modelesImport
	 *            the list of object that have been transformed or are being
	 *            transformed.
	 */
	public void resetImport(HashMap<Object, Object> modelesReset,
			HashSet<Object> modelesImport) {
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
						cryCriterionType = (CriterionType) modelesReset
								.get(cryCriterionType);
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
						qualityCriterion.resetImport(modelesReset,
								modelesImport);
						modelesReset.put(key, qualityCriterion);
					} else {
						qualityCriterion = (QualityCriterion) modelesReset
								.get(qualityCriterion);
					}
				}
			}
		}
	}

}
