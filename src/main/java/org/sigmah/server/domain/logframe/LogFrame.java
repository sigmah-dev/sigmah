package org.sigmah.server.domain.logframe;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * LogFrame domain entity.
 * </p>
 * <p>
 * Represents the entire log frame of a project.<br/>
 * A log frame defines a main objective and contains a list of specific objectives and a list of prerequisites.
 * </p>
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.LOGFRAME_TABLE)
public class LogFrame extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 3670543377662206665L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.LOGFRAME_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.LOGFRAME_COLUMN_MAIN_OBJECTIVE, columnDefinition = EntityConstants.COLUMN_DEFINITION_TEXT)
	private String mainObjective;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(optional = false, cascade = {
																					CascadeType.MERGE,
																					CascadeType.PERSIST,
																					CascadeType.REFRESH
	}, fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.LOGFRAME_COLUMN_LOGFRAME_MODEL_ID, nullable = false)
	@NotNull
	private LogFrameModel logFrameModel;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.PROJECT_COLUMN_ID)
	private Project parentProject;

	@OneToMany(mappedBy = "parentLogFrame", cascade = CascadeType.ALL, orphanRemoval = true)
	// Use @Sort instead of @OrderBy as hibernate biffs because the code lives in the log_frame_element table.
	@org.hibernate.annotations.Sort
	private List<SpecificObjective> specificObjectives = new ArrayList<SpecificObjective>(0);

	@OneToMany(mappedBy = "parentLogFrame", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy(value = "code ASC")
	private List<Prerequisite> prerequisites = new ArrayList<Prerequisite>();

	@OneToMany(mappedBy = "parentLogFrame", cascade = CascadeType.ALL, orphanRemoval = true)
	// Use this hibernate specific annotation to make LogFrame Entity delete its child
	@org.hibernate.annotations.Sort
	private List<LogFrameGroup> groups = new ArrayList<LogFrameGroup>();

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * Duplicates this log frame (omits IDs).<br>
	 * 
	 * @return A copy of this log frame.
	 */
	public LogFrame copy(final LogFrameCopyContext context) {

		final LogFrame copy = new LogFrame();
		copy.logFrameModel = this.logFrameModel;
		copy.mainObjective = this.mainObjective;

		// Copying groups
		copy.groups = new ArrayList<LogFrameGroup>();
		for (final LogFrameGroup group : this.groups) {
			final LogFrameGroup groupCopy = group.copy(copy);
			context.addGroupCopy(group, groupCopy);
			copy.groups.add(groupCopy);
		}

		// Copying objectives
		copy.specificObjectives = new ArrayList<SpecificObjective>();
		for (final SpecificObjective objective : this.specificObjectives) {
			copy.specificObjectives.add(objective.copy(copy, context));
		}

		// Copying prerequisites
		copy.prerequisites = new ArrayList<Prerequisite>();
		for (final Prerequisite prerequisite : this.prerequisites) {
			copy.prerequisites.add(prerequisite.copy(copy, context));
		}

		return copy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("mainObjective", mainObjective);
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public LogFrameModel getLogFrameModel() {
		return logFrameModel;
	}

	public void setLogFrameModel(LogFrameModel logFrameModel) {
		this.logFrameModel = logFrameModel;
	}

	public String getMainObjective() {
		return mainObjective;
	}

	public void setMainObjective(String mainObjective) {
		this.mainObjective = mainObjective;
	}

	public List<SpecificObjective> getSpecificObjectives() {
		return specificObjectives;
	}

	public void setSpecificObjectives(List<SpecificObjective> specificObjectives) {
		this.specificObjectives = specificObjectives;
	}

	public List<Prerequisite> getPrerequisites() {
		return prerequisites;
	}

	public void setPrerequisites(List<Prerequisite> prerequisites) {
		this.prerequisites = prerequisites;
	}

	public Project getParentProject() {
		return parentProject;
	}

	public void setParentProject(Project parentProject) {
		this.parentProject = parentProject;
	}

	public List<LogFrameGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<LogFrameGroup> groups) {
		this.groups = groups;
	}

}
