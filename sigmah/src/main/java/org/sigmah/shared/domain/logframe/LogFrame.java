package org.sigmah.shared.domain.logframe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
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
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Sort;
import org.sigmah.shared.domain.Project;

/**
 * Represents the entire log frame of a project.<br/>
 * A log frame defines a main objective and contains a list of specific
 * objectives and a list of prerequisites.
 * 
 * @author tmi
 * 
 */
@Entity
@Table(name = "log_frame")
public class LogFrame implements Serializable {

    private static final long serialVersionUID = 3670543377662206665L;

    private Integer id;
    private LogFrameModel logFrameModel;
    private String mainObjective;
    private List<SpecificObjective> specificObjectives = new ArrayList<SpecificObjective>(0);
    private List<Prerequisite> prerequisites = new ArrayList<Prerequisite>();
    private Project parentProject;
    private List<LogFrameGroup> groups = new ArrayList<LogFrameGroup>();

    /**
     * Duplicates this log frame (omits IDs).<br>
     * @return A copy of this log frame.
     */
    public LogFrame copy(LogFrameCopyContext context) {

    	
    	final LogFrame copy = new LogFrame();
        copy.logFrameModel = this.logFrameModel;
        copy.mainObjective = this.mainObjective;
               

        // Copying groups
        copy.groups = new ArrayList<LogFrameGroup>();
        for(final LogFrameGroup group : this.groups) {
            final LogFrameGroup groupCopy = group.copy(copy);
            context.addGroupCopy(group, groupCopy);
            copy.groups.add(groupCopy);
        }

        // Copying objectives
        copy.specificObjectives = new ArrayList<SpecificObjective>();
        for(final SpecificObjective objective : this.specificObjectives)
            copy.specificObjectives.add(objective.copy(copy, context));

        // Copying prerequisites
        copy.prerequisites = new ArrayList<Prerequisite>();
        for(final Prerequisite prerequisite : this.prerequisites)
            copy.prerequisites.add(prerequisite.copy(copy, context));

        return copy;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_log_frame")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne(optional = false, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "id_log_frame_model", nullable = false)
    public LogFrameModel getLogFrameModel() {
        return logFrameModel;
    }

    public void setLogFrameModel(LogFrameModel logFrameModel) {
        this.logFrameModel = logFrameModel;
    }

    @Column(name = "main_objective", columnDefinition = "TEXT")
    public String getMainObjective() {
        return mainObjective;
    }

    public void setMainObjective(String mainObjective) {
        this.mainObjective = mainObjective;
    }

    @OneToMany(mappedBy = "parentLogFrame", cascade = CascadeType.ALL)
    // use @Sort instead of @OrderBy as hibernate biffs because the code lives in the log_frame_element table
    @org.hibernate.annotations.Sort
    @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN) 
    public List<SpecificObjective> getSpecificObjectives() {
        return specificObjectives;
    }

    public void setSpecificObjectives(List<SpecificObjective> specificObjectives) {
        this.specificObjectives = specificObjectives;
    }

    @OneToMany(mappedBy = "parentLogFrame", cascade = CascadeType.ALL)
    @OrderBy(value = "code asc")
    @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN) 
    public List<Prerequisite> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(List<Prerequisite> prerequisites) {
        this.prerequisites = prerequisites;
    }

    @ManyToOne
    @JoinColumn(name = "id_project")
    public Project getParentProject() {
        return parentProject;
    }

    public void setParentProject(Project parentProject) {
        this.parentProject = parentProject;
    }

    @OneToMany(mappedBy = "parentLogFrame", cascade = CascadeType.ALL)
    //Use this hibernate specific annotation to make LogFrame Entity delete its child
    @org.hibernate.annotations.Sort
    @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN) 
    public List<LogFrameGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<LogFrameGroup> groups) {
        this.groups = groups;
    }
}
