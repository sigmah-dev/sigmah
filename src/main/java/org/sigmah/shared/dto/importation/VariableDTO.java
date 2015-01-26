package org.sigmah.shared.dto.importation;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

/**
 * VariableDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class VariableDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -3598029403371970959L;
	
	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "importation.Variable";

	// DTO attributes keys.
	public static final String NAME = "name";
	public static final String REFERENCE = "reference";
	public static final String IMPORTATION_SCHEME = "importationScheme";

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
		builder.append(NAME, getName());
		builder.append(REFERENCE, getReference());
	}

	public String getName() {
		return get(NAME);
	}

	public void setName(String name) {
		set(NAME, name);
	}

	public String getReference() {
		return get(REFERENCE);
	}

	public void setReference(String reference) {
		set(REFERENCE, reference);
	}

	public ImportationSchemeDTO getImportationScheme() {
		return get(IMPORTATION_SCHEME);
	}

	public void setImportationScheme(ImportationSchemeDTO importationScheme) {
		set(IMPORTATION_SCHEME, importationScheme);
	}
}
