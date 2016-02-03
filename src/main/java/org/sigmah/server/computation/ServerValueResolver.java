package org.sigmah.server.computation;

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import org.sigmah.server.dao.base.EntityManagerProvider;
import org.sigmah.shared.computation.ValueResolver;
import org.sigmah.shared.computation.value.ComputedValue;
import org.sigmah.shared.computation.value.ComputedValues;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;

/**
 * Server implementation of {@link ValueResolver}.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.1
 */
public class ServerValueResolver extends EntityManagerProvider implements ValueResolver {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resolve(Collection<FlexibleElementDTO> elements, int containerId, AsyncCallback<Map<Integer, ComputedValue>> callback) {
		final HashMap<Integer, ComputedValue> values = new HashMap<>();
		
		final TypedQuery<String> query = em().createQuery("SELECT v.value FROM Value v WHERE v.containerId = :containerId AND v.element.id = :elementId", String.class);
		query.setParameter("containerId", containerId);
		
		for (final FlexibleElementDTO element : elements) {
			if (!(element instanceof DefaultFlexibleElementDTO)) {
				final Integer id = element.getId();
				query.setParameter("elementId", id);
				
				String result = null;
				try {
					result = query.getSingleResult();
				} catch(NoResultException e) {
					// Ignored.
				}

				values.put(id, ComputedValues.from(result));
			} else {
				// TODO: Should also handle DefaultFlexibleElement.
				throw new UnsupportedOperationException("DefaultFlexibleElement are not supported yet.");
			}
		}
		
		callback.onSuccess(values);
	}
	
}
