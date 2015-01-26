/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.shared.domain.report;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.hibernate.annotations.Cascade;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Entity
public class ProjectReportModelSection implements Serializable {
    private final static long serialVersionUID = 1L;
    
    private Integer id;
    private Integer projectModelId;
    private Integer parentSectionModelId;
    private String name;
    private Integer index;
    private Integer numberOfTextarea;
    private List<ProjectReportModelSection> subSections;
    private List<KeyQuestion> keyQuestions;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentSectionModelId() {
        return parentSectionModelId;
    }

    public void setParentSectionModelId(Integer parentSectionModelId) {
        this.parentSectionModelId = parentSectionModelId;
    }

    
    public Integer getProjectModelId() {
        return projectModelId;
    }

    public void setProjectModelId(Integer projectModelId) {
        this.projectModelId = projectModelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name="sort_order")
    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getNumberOfTextarea() {
        return numberOfTextarea;
    }

    public void setNumberOfTextarea(Integer numberOfTextarea) {
        this.numberOfTextarea = numberOfTextarea;
    }

    @OneToMany(mappedBy = "parentSectionModelId")  
    @Cascade(value=org.hibernate.annotations.CascadeType.DELETE_ORPHAN) 
    @OrderBy("index ASC, id ASC")
    public List<ProjectReportModelSection> getSubSections() {
        return subSections;
    }

    public void setSubSections(List<ProjectReportModelSection> subSections) {
        this.subSections = subSections;
    }

    @OneToMany(mappedBy = "sectionId")
    @OrderBy("index ASC, id ASC")
    public List<KeyQuestion> getKeyQuestions() {
        return keyQuestions;
    }
    
    public void setKeyQuestions(List<KeyQuestion> keyQuestions) {
        this.keyQuestions = keyQuestions;
    }
    
	/**
	 * Reset the identifiers of the object.
	 * 
	 * @param projectModelId
	 *            the project-report parent identifier.
	 * 
	 * @param parentSectionModelId
	 *            project-report-section parent identifier.
	 * 
	 * @param modelesReset
	 *            the map of the reseted objects (original object, transformed
	 *            object).
	 * @param modelesImport
	 *            the list of object that have been transformed or are being
	 *            transformed.
	 */
	public void resetImport(Integer projectModelId, Integer parentSectionModelId, HashMap<Object, Object> modelesReset, HashSet<Object> modelesImport) {
		this.id = null;
		this.projectModelId = projectModelId;
		this.parentSectionModelId = parentSectionModelId;

		if (this.subSections != null) {
			for (ProjectReportModelSection projetModelSection : this.subSections) {
				if (!modelesReset
						.containsKey(projetModelSection)) {
					ProjectReportModelSection key = projetModelSection;
					projetModelSection.resetImport(null, this.id, modelesReset, modelesImport);
					modelesReset.put(key, projetModelSection);
				} else {
					projetModelSection = (ProjectReportModelSection)modelesReset
							.get(projetModelSection);
				}
			}
		}
		if (this.keyQuestions != null) {
			for (KeyQuestion keyQuestion : keyQuestions) {
				keyQuestion.resetImport(this.id, modelesReset, modelesImport);
			}
		}
	}
}
