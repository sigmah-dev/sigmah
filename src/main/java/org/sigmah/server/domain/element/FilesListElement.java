package org.sigmah.server.domain.element;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Files list element domain entity.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.FILES_LIST_ELEMENT_TABLE)
public class FilesListElement extends FlexibleElement {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 4866208826790848338L;

	@Column(name = EntityConstants.FILES_LIST_ELEMENT_COLUMN_MAX_LIMIT, nullable = true)
	private Integer limit;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendToString(final ToStringBuilder builder) {

		builder.append("limit", limit);
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS AND SETTERS.
	//
	// --------------------------------------------------------------------------------

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}
}
