package org.sigmah.server.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.layout.Layout;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Project banner domain entity.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.PROJECT_BANNER_TABLE)
public class ProjectBanner extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -1266259112071917788L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.PROJECT_BANNER_COLUMN_ID)
	private Integer id;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	// Trick: using '@ManyToOne' to avoid automatic load of the object (see '@OneToOne' lazy issue).
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.PROJECT_MODEL_COLUMN_ID)
	private ProjectModel projectModel;

	// Trick: using '@ManyToOne' to avoid automatic load of the object (see '@OneToOne' lazy issue).
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.LAYOUT_COLUMN_ID, nullable = false)
	@NotNull
	private Layout layout;

	public ProjectBanner() {
	}

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * Reset the identifiers of the object.
	 * 
	 * @param parentProjectModel
	 *          the parent project model
	 */
	public void resetImport(final ProjectModel parentProjectModel) {
		this.id = null;
		this.projectModel = parentProjectModel;
		if (this.layout != null) {
			this.layout.resetImport();
		}
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

	public ProjectModel getProjectModel() {
		return projectModel;
	}

	public void setProjectModel(ProjectModel projectModel) {
		this.projectModel = projectModel;
	}

	public Layout getLayout() {
		return layout;
	}

	public void setLayout(Layout layout) {
		this.layout = layout;
	}

}
