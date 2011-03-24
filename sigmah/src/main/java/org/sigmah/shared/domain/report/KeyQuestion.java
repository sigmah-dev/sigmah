/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.shared.domain.report;

import java.io.Serializable;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.sigmah.shared.domain.quality.QualityCriterion;

import java.util.HashMap;

/**
 * Represents a key question associated with a report section.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Entity
public class KeyQuestion implements Serializable {
    private final static long serialVersionUID = 1L;

    private Integer id;
    private Integer sectionId;
    private String label;
    private QualityCriterion qualityCriterion;
    private Integer index;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSectionId() {
        return sectionId;
    }
    public void setSectionId(Integer sectionId) {
        this.sectionId = sectionId;
    }

    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }

    @ManyToOne(optional = true)
    public QualityCriterion getQualityCriterion() {
        return qualityCriterion;
    }
    public void setQualityCriterion(QualityCriterion qualityCriterion) {
        this.qualityCriterion = qualityCriterion;
    }

    @Column(name="sort_order")
    public Integer getIndex() {
        return index;
    }
    public void setIndex(Integer index) {
        this.index = index;
    }
    
	/**
	 * Reset the identifiers of the object.
	 * 
	 * @param sectionId
	 *            the parent section identifier.
	 * @param modelesReset
	 *            the map of the reseted objects (original object, transformed
	 *            object).
	 * @param modelesImport
	 *            the list of object that have been transformed or are being
	 *            transformed.
	 */
	public void resetImport(Integer sectionId, HashMap<Object, Object> modelesReset, HashSet<Object> modelesImport) {
		this.id = null;
		this.sectionId = sectionId;
		if (this.qualityCriterion != null) {
			this.qualityCriterion.resetImport(modelesReset, modelesImport);
		}
	}
}
