package org.sigmah.shared.domain;

import java.io.Serializable;
import java.util.Date;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Defines the model for an org unit.
 * 
 * @author tmi
 * 
 */
@Entity
@Table(name = "org_unit_model")
@org.hibernate.annotations.FilterDefs({ @org.hibernate.annotations.FilterDef(name = "hideDeleted") })
@org.hibernate.annotations.Filters({ @org.hibernate.annotations.Filter(name = "hideDeleted", condition = "date_deleted is null") })
public class OrgUnitModel implements Serializable {

    private static final long serialVersionUID = -722132644240828016L;

    private Integer id;
    private String name;
    private OrgUnitBanner banner;
    private OrgUnitDetails details;
    private Boolean hasBudget = false;
    private Integer minLevel;
    private Integer maxLevel;
    private String title;
    private Boolean canContainProjects = true;
    private ProjectModelStatus status = ProjectModelStatus.DRAFT;
    private Organization organization;
    private Date dateDeleted;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "org_unit_model_id")
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "name", nullable = false, length = 8192)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "title", nullable = false, length = 8192)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @OneToOne(mappedBy = "orgUnitModel", cascade = CascadeType.ALL)
    public OrgUnitBanner getBanner() {
        return banner;
    }

    public void setBanner(OrgUnitBanner banner) {
        this.banner = banner;
    }

    @OneToOne(mappedBy = "orgUnitModel", cascade = CascadeType.ALL)
    public OrgUnitDetails getDetails() {
        return details;
    }

    public void setDetails(OrgUnitDetails details) {
        this.details = details;
    }

    @Column(name = "has_budget")
    public Boolean getHasBudget() {
        return hasBudget;
    }

    public void setHasBudget(Boolean hasBudget) {
        this.hasBudget = hasBudget;
    }

    @Column(name = "can_contain_projects", nullable = false)
    public Boolean getCanContainProjects() {
        return canContainProjects;
    }

    public void setCanContainProjects(Boolean canContainProjects) {
        this.canContainProjects = canContainProjects;
    }
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    public ProjectModelStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectModelStatus status) {
        this.status = status;
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
     * 
     * @return The date on which this project model was deleted by the user, or null
     *         if this project model is not deleted.
     */
    @Column(name="date_deleted")
    @Temporal(value = TemporalType.TIMESTAMP)
    public Date getDateDeleted() {
        return this.dateDeleted;
    }

    protected void setDateDeleted(Date date) {
        this.dateDeleted = date;
    }

    /**
     * Marks this database as deleted. (Though the row is not removed from the
     * database)
     */
    public void delete() {
        Date now = new Date();
        setDateDeleted(now);
    }
	
	/**
     * Reset the identifiers of the object.
     */
    public void resetImport(){
    	this.id = null;
    	if(this.banner!=null){
    		this.banner.resetImport(this);
    	}
    	if(this.details!=null){
    		this.details.resetImport(this);
    	}
    }

}
