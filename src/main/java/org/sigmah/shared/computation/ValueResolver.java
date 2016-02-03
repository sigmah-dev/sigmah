package org.sigmah.shared.computation;

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.Collection;
import java.util.Map;
import org.sigmah.shared.computation.value.ComputedValue;
import org.sigmah.shared.dto.element.FlexibleElementDTO;

/**
 * Retrieve values of the dependencies of a <code>Computation</code> element.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.1
 */
public interface ValueResolver {
	
	/**
	 * Resolve the values of the given elements.
	 * 
	 * @param elements Elements to resolve.
	 * @param containerId Identifier of the container (project or orgunit).
	 * @param callback To be called when every value has been retrieved.
	 */
	void resolve(Collection<FlexibleElementDTO> elements, int containerId, AsyncCallback<Map<Integer, ComputedValue>> callback);
	
}
