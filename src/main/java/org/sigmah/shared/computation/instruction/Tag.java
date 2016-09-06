package org.sigmah.shared.computation.instruction;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
