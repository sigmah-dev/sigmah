package org.sigmah.shared.computation.instruction;

import java.util.Map;
import java.util.Stack;
import org.sigmah.shared.computation.value.ComputedValue;

/**
 * Reference zero to multiples fields marked by the tag.
 * Tags can be composites and include sub-tags.
 * 
 * Examples: <code>@distribution</code> and <code>@budget.spent</code>.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.2
 */
public class Tag implements Instruction {
	
	/**
	 * Category of fields.
	 */
	private String label;
	
	/**
	 * Child category. Optional.
	 */
	private String child;

	@Override
	public void execute(Stack<ComputedValue> stack, Map<Integer, ComputedValue> variables) {
		// TODO: Ajouter une valeur CollectionValue dans la pile.
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
}
