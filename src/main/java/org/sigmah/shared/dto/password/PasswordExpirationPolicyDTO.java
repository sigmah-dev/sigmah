package org.sigmah.shared.dto.password;

import java.util.Date;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.organization.OrganizationDTO;

/**
 * DTO mapping class for entity PasswordExpirationPolicy.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class PasswordExpirationPolicyDTO extends AbstractModelDataEntityDTO<Integer> {
	
	public static final String ENTITY_NAME = "password.PasswordExpirationPolicy";

	public static final String EXPIRATION_POLICY = "expirationPolicy";
	public static final String ORGANIZATION = "organization";
	public static final String REFERENCE_DATE = "referenceDate";
	public static final String FREQUENCY = "frequency";
	public static final String RESET_NEW_USER_PASSWORD = "resetNewUserPassword";
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}
	
	/**
	 * Returns <code>true</code>
	 * @param lastPasswordChange
	 * @return 
	 */
	public boolean isExpired(Date lastPasswordChange) {
		return getType().isExpired(this, lastPasswordChange);
	}
	
	// ---
	// Getters and setters.
	// ---
	
	public ExpirationPolicy getType() {
		return get(EXPIRATION_POLICY);
	}

	public void setType(ExpirationPolicy type) {
		set(EXPIRATION_POLICY, type);
	}
	
	public OrganizationDTO getOrganization() {
		return get(ORGANIZATION);
	}

	public void setOrganization(OrganizationDTO organization) {
		set(ORGANIZATION, organization);
	}
	
	public Date getReferenceDate() {
		return get(REFERENCE_DATE);
	}
    
    public void setReferenceDate(Date referenceDate) {
		set(REFERENCE_DATE, referenceDate);
	}
	
	public Integer getFrequency() {
		return get(FREQUENCY);
	}
    
    public void setFrequency(Integer frequency) {
		set(FREQUENCY, frequency);
	}
	
	public boolean isResetNewUserPassword() {
		return get(RESET_NEW_USER_PASSWORD);
	}
    
    public void setResetNewUserPassword(boolean resetNewUserPassword) {
		set(RESET_NEW_USER_PASSWORD, resetNewUserPassword);
	}

}
