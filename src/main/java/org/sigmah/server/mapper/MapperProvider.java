package org.sigmah.server.mapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.dozer.DozerBeanMapper;
import org.dozer.loader.api.BeanMappingBuilder;
import org.dozer.loader.api.FieldsMappingOptions;
import org.dozer.loader.api.TypeMappingBuilder;
import org.dozer.loader.api.TypeMappingOption;
import org.dozer.loader.api.TypeMappingOptions;
import org.sigmah.server.domain.base.Entity;
import org.sigmah.shared.dto.base.DTO;
import org.sigmah.shared.dto.base.mapping.CustomMappingField;
import org.sigmah.shared.dto.base.mapping.IsMappingMode;
import org.sigmah.shared.dto.base.mapping.MappingField;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Provides the Dozer mapper.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class MapperProvider implements Provider<org.dozer.Mapper> {

	private MappingModeDefinitions definitions;

	@Inject
	public MapperProvider(MappingModeDefinitions definitions) {
		this.definitions = definitions;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.dozer.Mapper get() {

		// Dozer mapping files.
		final List<String> mappingFiles = new ArrayList<String>();
		mappingFiles.add("dozer/dozer-admin-mapping.xml");
		mappingFiles.add("dozer/dozer-schema-mapping.xml");

		final DozerBeanMapper mapper = new DozerBeanMapper(mappingFiles);

		// Custom mapping with DTO modes.
		for (final MappingModeDefinition<?, ?> def : definitions.getModes()) {
			addMappings(mapper, def.getEntityClass(), def.getDtoClass(), def.getModes());
		}

		return mapper;

	}

	private static <E extends Entity, D extends DTO> void addMappings(final DozerBeanMapper mapper, final Class<E> entityClass, final Class<D> dtoClass,
			IsMappingMode[] modes) {
		for (final BeanMappingBuilder mapping : addDTOMapping(entityClass, dtoClass, modes)) {
			mapper.addMapping(mapping);
		}
	}

	private static <E extends Entity, D extends DTO> List<BeanMappingBuilder> addDTOMapping(final Class<E> entityClass, final Class<D> dtoClass,
			IsMappingMode[] modes) {

		final ArrayList<BeanMappingBuilder> builders = new ArrayList<>();

		// For each enum value, builds a custom mapping.
		if (modes != null) {
			for (final IsMappingMode mode : modes) {

				final BeanMappingBuilder builder = new BeanMappingBuilder() {

					@Override
					protected void configure() {

						// Basic mapping options.
						final TypeMappingOption[] basicOptions = new TypeMappingOption[] {
																																							TypeMappingOptions.oneWay(),
																																							TypeMappingOptions.wildcard(true),
																																							TypeMappingOptions.mapNull()
						};

						// Mapping builder.
						// The Dozer map-id attribute is the getMapId() method of the enum value.
						final TypeMappingBuilder tmb = mapping(entityClass, dtoClass, ArrayUtils.add(basicOptions, TypeMappingOptions.mapId(mode.getMapId())));

						// Custom fields.
						if (mode.getCustomFields() != null) {

							for (final CustomMappingField custom : mode.getCustomFields()) {

								final IsMappingMode customFieldMappingMode = custom.getMappingMode();

								if (customFieldMappingMode != null) {
									tmb.fields(custom.getEntityAttributeName(), custom.getDTOMapKey(), FieldsMappingOptions.useMapId(customFieldMappingMode.getMapId()));

								} else {
									tmb.fields(custom.getEntityAttributeName(), custom.getDTOMapKey());
								}
							}

						}

						// Excluded fields.
						if (mode.getExcludedFields() != null) {

							for (final MappingField excluded : mode.getExcludedFields()) {
								tmb.exclude(excluded.getEntityAttributeName());
							}

						}

					}

				};

				builders.add(builder);

			}
		}

		return builders;

	}

}
