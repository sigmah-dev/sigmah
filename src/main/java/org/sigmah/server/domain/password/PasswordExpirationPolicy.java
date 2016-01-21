package org.sigmah.server.domain.password;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
