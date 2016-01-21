package org.sigmah.client.computation;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sigmah.offline.sync.SuccessCallback;
import org.sigmah.shared.computation.Computation;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.element.ComputationElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementContainer;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.event.ValueEvent;
import org.sigmah.shared.dto.element.event.ValueHandler;

/**
 * Manage computation element triggers.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.1
 */
public class ComputationTriggerManager {

	@Inject
	private ClientValueResolver valueResolver;
	
	private final Map<ComputationElementDTO, Computation> computations = new HashMap<ComputationElementDTO, Computation>();
	private final Map<FlexibleElementDTO, List<ComputationElementDTO>> dependencies = new HashMap<FlexibleElementDTO, List<ComputationElementDTO>>();
	private final Map<FlexibleElementDTO, Field<Object>> components = new HashMap<FlexibleElementDTO, Field<Object>>();
	
	private FlexibleElementContainer container;
	
	/**
	 * Prepare the trigger manager.
	 * 
	 * @param project Project to display.
	 */
	public void prepareForProject(final ProjectDTO project) {
		this.container = project;
		
		dependencies.clear();
		computations.clear();
		components.clear();
		
		final ProjectModelDTO model = project.getProjectModel();
		
		for (final ProjectDTO.LocalizedElement<ComputationElementDTO> localizedElement : project.getLocalizedElements(ComputationElementDTO.class)) {
			final ComputationElementDTO computationElement = localizedElement.getElement();
			final Computation computation = computationElement.getComputationForModel(model);
			computations.put(computationElement, computation);
			
			for (final FlexibleElementDTO dependency : computation.getDependencies()) {
				List<ComputationElementDTO> list = dependencies.get(dependency);
				
				if (list == null) {
					list = new ArrayList<ComputationElementDTO>();
					dependencies.put(dependency, list);
				}
				
				list.add(computationElement);
			}
		}
	}
	
	/**
	 * Add a value change handler to the given element if it is a dependency
	 * of a computation.
	 * 
	 * @param element Element to listen.
	 * @param component Component associated to the given element.
	 * @param modifications Value change list.
	 */
	public void listenToValueChangesOfElement(final FlexibleElementDTO element, final Component component, final List<ValueEvent> modifications) {
		
		if (element instanceof ComputationElementDTO) {
			components.put(element, (Field<Object>) component);
		}
		
		final List<ComputationElementDTO> computationElements = dependencies.get(element);
		
		if (computationElements != null) {
			element.addValueHandler(new ValueHandler() {

				@Override
				public void onValueChange(ValueEvent event) {
					for (final ComputationElementDTO computationElement : computationElements) {
						final Computation computation = computations.get(computationElement);
						computation.computeValueWithModificationsAndResolver(container, modifications, valueResolver, new SuccessCallback<String>() {

							@Override
							public void onSuccess(String result) {
								final Field<Object> field = components.get(computationElement);
								if (field != null) {
									field.setValue(result);
								}
								computationElement.fireValueEvent(result);
							}
						});
					}
				}
			});
		}
	}
	
}
