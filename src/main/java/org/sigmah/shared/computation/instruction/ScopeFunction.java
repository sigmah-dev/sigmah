package org.sigmah.shared.computation.instruction;

import org.sigmah.shared.computation.dependency.Scope;

/**
 * Defines a scope function.
 * 
 * This type of function is used to scope the arguments of an other function.
 * It does nothing on its own and is used only to parse the format defined by
 * Olivier Sarrat (osarrat@urd.org).
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public interface ScopeFunction extends Function {
	
	void setModelName(String modelName);
	Scope toScope();
}
