package org.sigmah.shared.computation.value;

import org.sigmah.shared.computation.instruction.Reductor;
import org.sigmah.shared.dto.element.ComputationElementDTO;

/**
 * Contains multiple values. Can be used by functions.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.2
 */
public class CollectionValue implements ComputedValue {

	@Override
	public void feedToReductor(Reductor reductor) {
		// TODO: Itérer sur les valeurs présentes en base de données et les ajouter au réducteur.
		reductor.feed(this);
	}
	
	@Override
	public Double get() {
		return null;
	}

	@Override
	public int matchesConstraints(ComputedValue minimum, ComputedValue maximum) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public int matchesConstraints(ComputationElementDTO element) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public ComputedValue addTo(ComputedValue other) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public ComputedValue multiplyWith(ComputedValue other) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public ComputedValue divide(ComputedValue other) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public ComputedValue substractFrom(ComputedValue other) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
}
