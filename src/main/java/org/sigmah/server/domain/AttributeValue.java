package org.sigmah.server.domain;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Attribute value domain entity.
 * </p>
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.ATTRIBUTE_VALUE_TABLE)
public class AttributeValue extends AbstractEntityId<AttributeValueId> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -9081370879826092607L;

	@EmbeddedId
	private AttributeValueId id;

	@Column(name = EntityConstants.ATTRIBUTE_VALUE_COLUMN_VALUE)
	private boolean value;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.ATTRIBUTE_COLUMN_ID, nullable = false, insertable = false, updatable = false)
	@NotNull
	private Attribute attribute;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.SITE_COLUMN_ID, nullable = false, insertable = false, updatable = false)
	@NotNull
	private Site site;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	public AttributeValue() {
	}

	public AttributeValue(Site site, Attribute attribute, boolean value) {
		setId(new AttributeValueId(site.getId(), attribute.getId()));
		setSite(site);
		setAttribute(attribute);
		setValue(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("value", value);
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	@Override
	public AttributeValueId getId() {
		return this.id;
	}

	@Override
	public void setId(AttributeValueId id) {
		this.id = id;
	}

	public Attribute getAttribute() {
		return this.attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	public Site getSite() {
		return this.site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public boolean getValue() {
		return this.value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

}
