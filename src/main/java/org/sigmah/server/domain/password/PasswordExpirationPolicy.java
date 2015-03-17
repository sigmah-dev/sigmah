package org.sigmah.server.domain.password;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import org.sigmah.server.domain.Organization;
import org.sigmah.server.domain.base.AbstractEntityId;

import org.sigmah.shared.dto.password.ExpirationPolicy;

/**
 * Password expiration policy per organization.
 * 
 * @author Renato Almeida (renatoaf.ufcg@gmail.com)
 */
@Entity
@Table(name = "password_expiration_policy")
public class PasswordExpirationPolicy extends AbstractEntityId<Integer> {
	
	private static final long serialVersionUID = -2722884637221828205L;

	private Integer id;
	private ExpirationPolicy type;
	private Organization organization;
	private Date referenceDate;
	private Integer frequency;
	private boolean resetNewUserPassword;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "policy_type", nullable = false)
	@Enumerated(EnumType.STRING)
	@NotNull
	public ExpirationPolicy getType() {
		return type;
	}

	public void setType(ExpirationPolicy type) {
		this.type = type;
	}
 
	@ManyToOne
	@JoinColumn(name = "organization_id", nullable = false)
	@NotNull
	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

    @Column(name = "reference_date", nullable = true)
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getReferenceDate() {
		return referenceDate;
	}
    
    public void setReferenceDate(Date referenceDate) {
		this.referenceDate = referenceDate;
	}

    @Column(name = "frequency", nullable = true)
    public Integer getFrequency() {
		return frequency;
	}
    
    public void setFrequency(Integer frequency) {
		this.frequency = frequency;
	}

    @Column(name = "reset_for_new_users", nullable = true)
    public boolean isResetNewUserPassword() {
		return resetNewUserPassword;
	}
    
    public void setResetNewUserPassword(boolean resetNewUserPassword) {
		this.resetNewUserPassword = resetNewUserPassword;
	}
	
}
