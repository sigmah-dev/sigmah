package org.sigmah.shared.computation.instruction;

import java.util.Map;
import java.util.Stack;
import org.sigmah.shared.computation.dependency.Dependency;
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
@Deprecated
public class Tag implements Instruction {
	
	public static enum Scope {
		
		SELF,
		GIVEN_PROJECT_MODEL,
		FUNDING_SOURCES,
		FUNDED_PROJECTS
	}
	
	public static enum Category {
		
		CONTRIBUTION,
		BUDGET_SPENT,
		BUDGET_TOTAL;
		
		private final String code;

		private Category() {
			this.code = name().toLowerCase().replace('_', '.');
		}

		public String getCode() {
			return code;
		}
	}
	
	/**
	 * Scope of this tag.
	 */
	private Scope scope;
	
	private Integer projectModelId;
	
	/**
	 * Category of fields.
	 */
	private Category category;
	
	@Override
	public void execute(Stack<ComputedValue> stack, Map<Dependency, ComputedValue> variables) {
		// TODO: Ajouter une valeur CollectionValue dans la pile.
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
}
