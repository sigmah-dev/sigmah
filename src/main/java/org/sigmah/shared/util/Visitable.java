package org.sigmah.shared.util;

/**
 * Describes an object that can be visited by a {@link Visitor}.
 * 
 * @param <V> Type of supported visitor.
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public interface Visitable<V extends Visitor> {
	
	void accept(V visitor);
}
