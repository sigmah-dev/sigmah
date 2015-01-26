package org.sigmah.server.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.layout.Layout;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Org unit details domain entity.
 * </p>
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.ORG_UNIT_DETAILS_TABLE)
public class OrgUnitDetails extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8741155131127565295L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.ORG_UNIT_DETAILS_COLUMN_ID)
	private Integer id;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@OneToOne
	@JoinColumn(name = EntityConstants.ORG_UNIT_DETAILS_COLUMN_ORG_UNIT_MODEL)
	private OrgUnitModel orgUnitModel;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = EntityConstants.LAYOUT_COLUMN_ID, nullable = false)
	@NotNull
	private Layout layout;

	public OrgUnitDetails() {
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
	 *          the org-unit model.
	 */
	public void resetImport(final OrgUnitModel orgUnitModel) {
		this.id = null;
		this.orgUnitModel = orgUnitModel;
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
