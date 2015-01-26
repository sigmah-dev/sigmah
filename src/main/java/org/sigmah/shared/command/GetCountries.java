package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.country.CountryDTO;

/**
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetCountries extends AbstractCommand<ListResult<CountryDTO>> {

	private boolean containingProjects;
	private CountryDTO.Mode mappingMode;

	public GetCountries() {
		// Serialization.
	}

	public GetCountries(boolean containingProjects) {
		this(containingProjects, null);
	}

	public GetCountries(CountryDTO.Mode mappingMode) {
		this(false, mappingMode);
	}

	public GetCountries(boolean containingProjects, CountryDTO.Mode mappingMode) {
		this.containingProjects = containingProjects;
		this.mappingMode = mappingMode;
	}

	public boolean isContainingProjects() {
		return containingProjects;
	}

	public CountryDTO.Mode getMappingMode() {
		return mappingMode;
	}
}
