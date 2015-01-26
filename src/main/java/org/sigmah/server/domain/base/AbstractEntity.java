package org.sigmah.server.domain.base;

import javax.persistence.MappedSuperclass;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Abstract entity, parent class of all domain entities.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@MappedSuperclass
public abstract class AbstractEntity implements Entity {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8891014061292938116L;

	/**
	 * <p>
	 * <b><em>Default {@code toString} method only displays centralized properties.</em><br/>
	 * See {@link #appendToString(ToStringBuilder)} to append other properties to {@code toString} builder.</b>
	 * </p>
	 * {@inheritDoc}
	 * 
	 * @see #appendToString(ToStringBuilder)
	 */
	@Override
	public String toString() {

		final ToStringBuilder builder = new ToStringBuilder(this);

		if (this instanceof EntityId) {
			// EntityId specific properties.
			builder.append("id", ((EntityId<?>) this).getId());
		}

		appendToString(builder); // Appends child entity specific properties.

		// TODO Uncoment following lines once centralized dates are available.
		// builder.append("creationDate", getCreationDate());
		// builder.append("editionDate", getEditionDate());
		// builder.append("deletionDate", getDeletionDate());

		return builder.toString();
	}

	/**
	 * <p>
	 * Allows sub entities to append other properties to the given {@code builder}.
	 * </p>
	 * <p>
	 * Use given builder this way:
	 * 
	 * <pre>
	 * builder.append(&quot;Property name&quot;, property);
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param builder
	 *          The {@code toString} builder (never {@code null}).
	 */
	protected abstract void appendToString(final ToStringBuilder builder);

	// TODO Centralize creation, edition and deletion dates into AbstractEntity.

	// @Column(name = "creation_date")
	// @Temporal(TemporalType.TIMESTAMP)
	// private Date creationDate;
	//
	// @Column(name = "edition_date")
	// @Temporal(TemporalType.TIMESTAMP)
	// private Date editionDate;
	//
	// @Column(name = "deletion_date")
	// @Temporal(TemporalType.TIMESTAMP)
	// private Date deletionDate;
	//
	// public Date getCreationDate() {
	// return creationDate;
	// }
	//
	// public void setCreationDate(Date creationDate) {
	// this.creationDate = creationDate;
	// }
	//
	// public Date getEditionDate() {
	// return editionDate;
	// }
	//
	// public void setEditionDate(Date editionDate) {
	// this.editionDate = editionDate;
	// }
	//
	// public Date getDeletionDate() {
	// return deletionDate;
	// }
	//
	// public void setDeletionDate(Date deletionDate) {
	// this.deletionDate = deletionDate;
	// }

}
