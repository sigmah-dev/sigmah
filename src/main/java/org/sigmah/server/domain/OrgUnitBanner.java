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
 * Org unit banner domain entity.
 * </p>
 * 
 * @author tmi (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
@Entity
@Table(name = EntityConstants.ORG_UNIT_BANNER_TABLE)
public class OrgUnitBanner extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -3799793707072202713L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.ORG_UNIT_BANNER_COLUMN_ID)
	private Integer id;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	// Trick: using '@ManyToOne' to avoid automatic load of the object (see '@OneToOne' lazy issue).
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.ORG_UNIT_BANNER_COLUMN_ORG_UNIT_MODEL)
	private OrgUnitModel orgUnitModel;

	// Trick: using '@ManyToOne' to avoid automatic load of the object (see '@OneToOne' lazy issue).
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.LAYOUT_COLUMN_ID, nullable = false)
	@NotNull
	private Layout layout;

	public OrgUnitBanner() {
	}

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * Reset the identifiers of the object.
	 * 
	 * @param orgUnitModel
	 *          the parent org-unit model.
	 */
	public void resetImport(final OrgUnitModel orgUnitModel, boolean keepPrivacyGroups) {
		this.id = null;
		this.orgUnitModel = orgUnitModel;
		if (this.layout != null) {
			this.layout.resetImport(keepPrivacyGroups);
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

	public OrgUnitModel getOrgUnitModel() {
		return orgUnitModel;
	}

	public void setOrgUnitModel(OrgUnitModel orgUnitModel) {
		this.orgUnitModel = orgUnitModel;
	}

	public Layout getLayout() {
		return layout;
	}

	public void setLayout(Layout layout) {
		this.layout = layout;
	}

}
