package org.sigmah.shared.computation.dependency;

import org.sigmah.shared.util.Visitor;

/**
 * Describes a visitor of dependencies.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.2
 */
public interface DependencyVisitor extends Visitor {
	
	/**
	 * Visit the given <code>SingleDependency</code>.
	 * 
	 * @param dependency
	 *			Dependency to visit.
	 */
	void visit(SingleDependency dependency);
	
	/**
	 * Visit the given <code>CollectionDependency</code>.
	 * 
	 * @param dependency
	 *			Dependency to visit.
	 */
	void visit(CollectionDependency dependency);
	
	/**
	 * Visit the given <code>ContributionDependency</code>.
	 * 
	 * @param dependency
	 *			Dependency to visit.
	 */
	void visit(ContributionDependency dependency);
}
