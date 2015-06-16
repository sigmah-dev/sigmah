package org.sigmah.server.domain;

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
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.layout.Layout;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Phase model domain entity.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.PHASE_MODEL_TABLE)
public class PhaseModel extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 6961563905925156300L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.PHASE_MODEL_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.PHASE_MODEL_COLUMN_NAME, nullable = false, length = 8192)
	private String name;

	@Column(name = EntityConstants.PHASE_MODEL_COLUMN_DISPLAY_ORDER, nullable = true)
	private Integer displayOrder;

	@Column(name = EntityConstants.PHASE_MODEL_COLUMN_GUIDE, columnDefinition = EntityConstants.COLUMN_DEFINITION_TEXT, nullable = true)
	private String guide;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@OneToMany
	@JoinTable(name = EntityConstants.PHASE_MODEL_SELF_LINK_TABLE, joinColumns = { 
		@JoinColumn(name = EntityConstants.PHASE_MODEL_COLUMN_ID)
	}, inverseJoinColumns = { 
		@JoinColumn(name = EntityConstants.PHASE_MODEL_SELF_LINK_COLUMN)
	}, uniqueConstraints = { 
		@UniqueConstraint(columnNames = {
			EntityConstants.PHASE_MODEL_COLUMN_ID,
			EntityConstants.PHASE_MODEL_SELF_LINK_COLUMN
		})
	})
	private List<PhaseModel> successors = new ArrayList<PhaseModel>();

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.PROJECT_MODEL_COLUMN_ID, nullable = false)
	@NotNull
	private ProjectModel parentProjectModel;

	// Trick: using '@ManyToOne' to avoid automatic load of the object (see '@OneToOne' lazy issue).
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.LAYOUT_COLUMN_ID, nullable = true)
	private Layout layout;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.PHASE_MODEL_COLUMN_DEFINITION_ID, nullable = true)
	private PhaseModelDefinition definition;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	public PhaseModel() {
	}

	public void addSuccessor(final PhaseModel successor) {
		if (successor != null) {
			successors.add(successor);
		}
	}

	/**
	 * Reset identifiers of the object.
	 * 
	 * @param parentProjectModel
	 *          the parent project model
	 * @param keepPrivacyGroups
	 *			<code>false</code> to set privacy group value to <code>null</code>, <code>true</code> to let it as is.
	 */
	public void resetImport(final ProjectModel parentProjectModel, boolean keepPrivacyGroups) {
		this.id = null;
		this.parentProjectModel = parentProjectModel;
		if (this.successors != null) {
			for (PhaseModel successor : successors) {
				successor.resetImport(null, keepPrivacyGroups);
			}
		}
		if (this.layout != null) {
			this.layout.resetImport(keepPrivacyGroups);
		}
		if (this.definition != null) {
			this.definition.resetImport();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("name", name);
		builder.append("displayOrder", displayOrder);
		builder.append("guide", guide);
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<PhaseModel> getSuccessors() {
		return successors;
	}

	public void setSuccessors(List<PhaseModel> successors) {
		this.successors = successors;
	}

	public ProjectModel getParentProjectModel() {
		return parentProjectModel;
	}

	public void setParentProjectModel(ProjectModel parentProjectModel) {
		this.parentProjectModel = parentProjectModel;
	}

	public void setLayout(Layout layout) {
		this.layout = layout;
	}

	public Layout getLayout() {
		return layout;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public PhaseModelDefinition getDefinition() {
		return definition;
	}

	public void setDefinition(PhaseModelDefinition definition) {
		this.definition = definition;
	}

	public String getGuide() {
		return guide;
	}

	public void setGuide(String guide) {
		this.guide = guide;
	}

}
