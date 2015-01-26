package org.sigmah.server.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Authentication domain entity.
 * </p>
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.AUTHENTICATION_TABLE)
public class Authentication extends AbstractEntityId<String> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1653320385158332573L;

	/**
	 * The secure id of this Authentication, which is a 128-bit random number represented as a 32-character hexadecimal
	 * string.
	 */
	@Id
	@Column(name = EntityConstants.AUTHENTICATION_COLUMN_ID, unique = true, nullable = false, length = EntityConstants.AUTHENTICATION_ID_MAX_LENGTH)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SecureSequenceGenerator")
	@org.hibernate.annotations.GenericGenerator(name = "SecureSequenceGenerator", strategy = "org.sigmah.server.auth.SecureSequenceGenerator")
	@Size(max = EntityConstants.AUTHENTICATION_ID_MAX_LENGTH)
	private String id;

	@Column(name = EntityConstants.COLUMN_DATE_CREATED)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateCreated;

	@Column(name = EntityConstants.AUTHENTICATION_COLUMN_DATE_LAST_ACTIVE)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateLastActive;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = EntityConstants.USER_COLUMN_ID, nullable = false)
	@NotNull
	private User user;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	public Authentication() {
		// Default empty constructor.
	}

	/**
	 * Creates a new session object for the given {@code user}, with a secure session id and starting at the current time.
	 * 
	 * @param user
	 *          The user.
	 */
	public Authentication(final User user) {
		// setId(SecureTokenGenerator.generate());
		final Date now = new Date();
		setUser(user);
		setDateCreated(now);
		setDateLastActive(now);
	}

	public long minutesSinceLastActivity() {
		return ((new Date()).getTime() - getDateLastActive().getTime()) / 1000 / 60;
	}

	@Transient
	public boolean isExpired() {
		// TODO: when do we invalidate tokens?
		// return minutesSinceLastActivity() > 30;
		return false;
	}

	public void setDateLastActive() {
		setDateLastActive(new Date());
	}

	/**
	 * <p>
	 * Overrides default behaviour to only display {@link User#getUserCompleteName(User)} value.
	 * </p>
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return User.getUserCompleteName(user);
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public void setId(String sessionId) {
		this.id = sessionId;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getDateCreated() {
		return this.dateCreated;
	}

	private void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateLastActive() {
		return this.dateLastActive;
	}

	public void setDateLastActive(Date dateLastActive) {
		this.dateLastActive = dateLastActive;
	}

}
