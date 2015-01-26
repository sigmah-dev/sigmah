package org.sigmah.server.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Org unit permission domain entity.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.ORG_UNIT_PERMISSION_TABLE)
public class OrgUnitPermission extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -4172755680742675052L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.ORG_UNIT_PERMISSION_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.ORG_UNIT_PERMISSION_COLUMN_VIEW_ALL, nullable = false)
	@NotNull
	private boolean viewAll;

	@Column(name = EntityConstants.ORG_UNIT_PERMISSION_COLUMN_EDIT_ALL, nullable = false)
	@NotNull
	private boolean editAll;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne
	@JoinColumn(name = EntityConstants.ORG_UNIT_PERMISSION_COLUMN_ORG_UNIT)
	@NotNull
	private OrgUnit unit;

	@OneToOne
	@JoinColumn(name = EntityConstants.ORG_UNIT_PERMISSION_COLUMN_USER)
	@NotNull
	private User user;

	public OrgUnitPermission() {
	}

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("viewAll", viewAll);
		builder.append("editAll", editAll);
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

	public OrgUnit getUnit() {
		return unit;
	}

	public void setUnit(OrgUnit unit) {
		this.unit = unit;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isViewAll() {
		return viewAll;
	}

	public void setViewAll(boolean viewAll) {
		this.viewAll = viewAll;
	}

	public boolean isEditAll() {
		return editAll;
	}

	public void setEditAll(boolean editAll) {
		this.editAll = editAll;
	}
}
