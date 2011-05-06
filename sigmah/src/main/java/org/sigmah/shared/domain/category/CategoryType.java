package org.sigmah.shared.domain.category;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.sigmah.shared.domain.Organization;

/**
 * Global category type.
 * 
 * @author tmi
 * 
 */
@Entity
@Table(name = "category_type")
public class CategoryType implements Serializable {

    private static final long serialVersionUID = -1069628345470292474L;

    private Integer id;
    private String label;
    private List<CategoryElement> elements = new ArrayList<CategoryElement>();
    private CategoryIcon icon;
    private Organization organization;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_category_type")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "label", nullable = false, length = 8192)
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @OneToMany(mappedBy = "parentType", cascade = CascadeType.ALL)
    @OrderBy("label asc")
    public List<CategoryElement> getElements() {
        return elements;
    }

    public void setElements(List<CategoryElement> elements) {
        this.elements = elements;
    }

    @Column(name = "icon_name", nullable = false, length = 8192)
    @Enumerated(value = EnumType.STRING)
    public CategoryIcon getIcon() {
        return icon;
    }

    public void setIcon(CategoryIcon icon) {
        this.icon = icon;
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
     */
    public void resetImport(){
    	this.id = null;
    	if(elements!=null){
    		for(CategoryElement categoryElement : elements){
    			categoryElement.resetImport();
    		}
    	}
    }
}
