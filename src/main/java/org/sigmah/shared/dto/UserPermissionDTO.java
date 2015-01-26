package org.sigmah.shared.dto;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataDTO;

/**
 * Projection DTO of the {@link org.sigmah.server.domain.UserPermission UserPermission} domain object.
 *
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class UserPermissionDTO extends AbstractModelDataDTO {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -8402651897719157011L;

	public UserPermissionDTO() {
		setAllowView(true);
		setAllowViewAll(false);
		setAllowEdit(false);
		setAllowEditAll(false);
		setAllowManageUsers(false);
		setAllowManageAllUsers(false);
		setAllowDesign(false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("name", getName());
		builder.append("firstName", getFirstName());
		builder.append("email", getEmail());
		builder.append("allowView", getAllowView());
		builder.append("allowDesign", getAllowDesign());
		builder.append("allowViewAll", getAllowViewAll());
		builder.append("allowEdit", getAllowEdit());
		builder.append("allowEditAll", getAllowEditAll());
		builder.append("allowManageUsers", getAllowManageUsers());
		builder.append("allowManageAllUsers", getAllowManageAllUsers());
	}

	public void setName(String value) {
		set("name", value);
	}

	/**
	 * Returns the User's name.
	 *
	 * @return the user's name
	 */
	public String getName() {
		return get("name");
	}

	public String getFirstName() {
		return get("firstName");
	}

	public void setFirstName(String firstName) {
		set("firstName", firstName);
	}

	public void setEmail(String value) {
		set("email", value);
	}

	/**
	 * Returns the User's email
	 *
	 * @return the User's email
	 */
	public String getEmail() {
		return get("email");
	}

	public void setAllowView(boolean value) {
		set("allowView", value);
	}

	public void setAllowDesign(boolean value) {
		set("allowDesign", value);
	}

	public boolean getAllowDesign() {
		return (Boolean) get("allowDesign");
	}

	public boolean getAllowView() {
		return (Boolean) get("allowView");
	}

	public void setAllowViewAll(boolean value) {
		set("allowViewAll", value);
	}

	public boolean getAllowViewAll() {
		return (Boolean) get("allowViewAll");
	}

	public void setAllowEdit(boolean value) {
		set("allowEdit", value);
	}

	public boolean getAllowEdit() {
		return (Boolean) get("allowEdit");
	}

	public void setAllowEditAll(boolean value) {
		set("allowEditAll", value);
	}

	public boolean getAllowEditAll() {
		return (Boolean) get("allowEditAll");
	}

	public boolean getAllowManageUsers() {
		return (Boolean) get("allowManageUsers");
	}

	public void setAllowManageUsers(boolean allowManageUsers) {
		set("allowManageUsers", allowManageUsers);
	}

	public boolean getAllowManageAllUsers() {
		return (Boolean) get("allowManageAllUsers");
	}

	public void setAllowManageAllUsers(boolean allowManageAll) {
		set("allowManageAllUsers", allowManageAll);
	}

	public PartnerDTO getPartner() {
		return get("partner");
	}

	public void setPartner(PartnerDTO value) {
		set("partner", value);
	}
}
