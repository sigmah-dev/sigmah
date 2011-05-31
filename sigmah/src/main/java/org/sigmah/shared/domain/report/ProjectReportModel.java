/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.shared.domain.report;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.hibernate.annotations.Cascade;
import org.sigmah.shared.domain.Organization;

import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.NotFound;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Entity
public class ProjectReportModel implements Serializable {
    private static final long serialVersionUID = -6595694881605806219L;
    private Integer id;
    private String name;
    private List<ProjectReportModelSection> sections;
    private Organization organization;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy = "projectModelId",cascade = CascadeType.ALL)
    @Cascade(value=org.hibernate.annotations.CascadeType.DELETE_ORPHAN) 
    @OrderBy("index ASC")
    @NotFound(action=NotFoundAction.IGNORE)
    public List<ProjectReportModelSection> getSections() {
        return sections;
    }

    public void setSections(List<ProjectReportModelSection> sections) {
        this.sections = sections;
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
	 * Reset the identifiers of the object.
	 * 
	 * @param modelesReset
	 *            the map of the reseted objects (original object, transformed
	 *            object).
	 * @param modelesImport
	 *            the list of object that have been transformed or are being
	 *            transformed.
	 */
	public void resetImport(HashMap<Object, Object> modelesReset, HashSet<Object> modelesImport) {
		this.id = null;
		if (sections != null) {
			for (ProjectReportModelSection section : sections) {
				section.resetImport(this.id, null, modelesReset, modelesImport);
			}
		}
	}
}
