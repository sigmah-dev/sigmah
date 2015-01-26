package org.sigmah.server.mapper.converters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dozer.DozerConverter;
import org.dozer.Mapper;
import org.dozer.MapperAware;
import org.sigmah.server.domain.profile.PrivacyGroupPermission;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;
import org.sigmah.shared.dto.referential.PrivacyGroupPermissionEnum;

/**
 * <p>
 * Custom dozer converter mapping a {@code List} of {@link PrivacyGroupPermission} to a {@code Map} of [
 * {@link PrivacyGroupDTO} ; {@link PrivacyGroupPermissionEnum} ] entries.
 * </p>
 * <p>
 * See "{@code dozer-schema-mapping.xml}" configuration file.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class PrivacyGroupsConverter extends DozerConverter<List<PrivacyGroupPermission>, Map<PrivacyGroupDTO, PrivacyGroupPermissionEnum>> implements
																																																																					MapperAware {

	/**
	 * Injected dozer mapper.
	 */
	private Mapper mapper;

	/**
	 * Custom converter initialization.
	 */
	@SuppressWarnings("unchecked")
	public PrivacyGroupsConverter() {
		super((Class<List<PrivacyGroupPermission>>) (Class<?>) List.class, (Class<Map<PrivacyGroupDTO, PrivacyGroupPermissionEnum>>) (Class<?>) Map.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<PrivacyGroupDTO, PrivacyGroupPermissionEnum> convertTo(final List<PrivacyGroupPermission> source,
			Map<PrivacyGroupDTO, PrivacyGroupPermissionEnum> destination) {

		if (source == null) {
			return null;
		}

		destination = new HashMap<>();

		for (final PrivacyGroupPermission p : source) {

			final PrivacyGroupDTO groupDTO = mapper.map(p.getPrivacyGroup(), PrivacyGroupDTO.class);

			destination.put(groupDTO, p.getPermission());
		}

		return destination;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PrivacyGroupPermission> convertFrom(final Map<PrivacyGroupDTO, PrivacyGroupPermissionEnum> source, List<PrivacyGroupPermission> destination) {
		// One-way mapping.
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMapper(final Mapper mapper) {
		this.mapper = mapper;
	}

}
