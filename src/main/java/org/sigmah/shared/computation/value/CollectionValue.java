package org.sigmah.shared.computation.value;

import org.sigmah.shared.computation.instruction.Reductor;
import org.sigmah.shared.dto.element.ComputationElementDTO;

/**
 * Contains multiple values. Can be used by functions.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.2
 */
public class CollectionValue extends ComputedValueAdapter {

	@Override
	public void feedToReductor(Reductor reductor) {
		// TODO: Itérer sur les valeurs présentes en base de données et les ajouter au réducteur.
		reductor.feed(this);
	}
	
}
