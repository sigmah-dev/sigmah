package org.sigmah.shared.dto.profile;

import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

/**
 * DTO mapping class for entity profile.PrivacyGroup.
 * 
 * @author nrebiai
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class PrivacyGroupDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -8951877538079370046L;

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "profile.PrivacyGroup";

	// DTO 'base' attributes keys.
	public static final String TITLE = "title";
	public static final String CODE = "code";
	public static final String UPDATED = "updated";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append(CODE, getCode());
		builder.append(TITLE, getTitle());
		builder.append(UPDATED, isUpdated());
	}

	// Code.
	public Integer getCode() {
		return get(CODE);
	}

	public void setCode(Integer code) {
		set(CODE, code);
	}

	// Title.
	public String getTitle() {
		return get(TITLE);
	}

	public void setTitle(String title) {
		set(TITLE, title);
	}

	// Updated.
	public boolean isUpdated() {
		return ClientUtils.isTrue(get(UPDATED));
	}

	public void setUpdated(boolean updated) {
		set(UPDATED, updated);
	}

}
