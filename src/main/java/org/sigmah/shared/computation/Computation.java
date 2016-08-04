package org.sigmah.shared.computation;

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

import org.sigmah.shared.computation.instruction.Variable;
import org.sigmah.shared.computation.instruction.Instruction;
import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import org.sigmah.client.ui.widget.Loadable;
import org.sigmah.shared.computation.dependency.Dependency;
import org.sigmah.shared.computation.dependency.SingleDependency;
import org.sigmah.shared.computation.instruction.BadVariable;
import org.sigmah.shared.computation.value.ComputationError;
import org.sigmah.shared.computation.value.ComputedValue;
import org.sigmah.shared.computation.value.ComputedValues;
import org.sigmah.shared.dto.element.FlexibleElementContainer;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.event.ValueEvent;
import org.sigmah.shared.dto.element.event.ValueEventWrapper;

/**
 * Executes rules to calculate the value of a computation element.
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.1
 */
public class Computation {

    private final List<Instruction> instructions;
    private Set<Dependency> dependencies;

    /**
     * Creates a new computation with the given instructions.
     *
     * @param instructions
     *          Instructions.
     *
     * @see Computations#parse(java.lang.String, java.util.List)
     */
    public Computation(List<Instruction> instructions) {
        this.instructions = instructions;
    }

    /**
     * Compute the value for the given container and resolver (for client-side).
     *
	 * @param container
     *          Container (project or orgunit).
     * @param modifications
     *          Unsaved modifications.
	 * @param resolver
     *          Value resolver.
	 * @param callback
     *          Called when the value has been computed.
     * @param loadables
     *          Element to mask during the computation.
     */
    public void computeValueWithModificationsAndResolver(final FlexibleElementContainer container, final List<ValueEvent> modifications, 
            final ValueResolver resolver, final AsyncCallback<String> callback, final Loadable... loadables) {

        final HashSet<Dependency> dependencies = new HashSet<Dependency>(getDependencies());

        final HashMap<Dependency, ComputedValue> variables = new HashMap<Dependency, ComputedValue>();
        for (final ValueEvent modification : modifications) {
            final FlexibleElementDTO source = modification.getSourceElement();
			final Dependency dependency = new SingleDependency(source);
            variables.put(dependency, ComputedValues.from(modification.getSingleValue()));
            dependencies.remove(dependency);
        }

        computeValueWithVariablesDependenciesAndResolver(container.getId(), variables, dependencies, resolver, callback, loadables);
    }

    /**
     * Compute the value for the given container and resolver (for server-side).
     *
	 * @param containerId
     *          Identifier of the container (project or orgunit).
     * @param modifications
     *          Unsaved modifications.
	 * @param resolver
     *          Value resolver.
	 * @param callback
     *          Called when the value has been computed.
     */
    public void computeValueWithWrappersAndResolver(final int containerId, final List<ValueEventWrapper> modifications, 
            final ValueResolver resolver, final AsyncCallback<String> callback) {
        
        final HashSet<Dependency> dependencies = new HashSet<Dependency>(getDependencies());

        final HashMap<Dependency, ComputedValue> variables = new HashMap<Dependency, ComputedValue>();
        for (final ValueEventWrapper modification : modifications) {
            final FlexibleElementDTO source = modification.getSourceElement();
			final Dependency dependency = new SingleDependency(source);
            variables.put(dependency, ComputedValues.from(modification.getSingleValue()));
            dependencies.remove(dependency);
        }

        computeValueWithVariablesDependenciesAndResolver(containerId, variables, dependencies, resolver, callback);
    }

    /**
     * Compute the value for the given container and resolver.
     *
     * @param containerId
     *          Identifier of the container (project or orgunit).
	 * @param resolver
     *          Value resolver.
	 * @param callback
     *          Called when the value has been computed.
     */
    public void computeValueWithResolver(final int containerId, final ValueResolver resolver, final AsyncCallback<String> callback) {
        computeValueWithVariablesDependenciesAndResolver(containerId, new HashMap<Dependency, ComputedValue>(), getDependencies(), resolver, callback);
    }
    
    /**
     * Compute the value with the given values.
     * <p>
     * If some dependencies are not resolved, call the resolver to retrieves
     * the values. Otherwise, the computation is done directly.
     * </p>
     *
	 * @param containerId
     *          Identifier of the container (project or orgunit).
	 * @param variables
     *          Map of the already resolved variables.
     * @param dependencies
     *          Not yet resolved dependencies.
	 * @param resolver
     *          Value resolver.
	 * @param callback
     *          Called when the value has been computed.
     * @param loadables
     *          Element to mask during the computation.
     */
    private void computeValueWithVariablesDependenciesAndResolver(final int containerId, final Map<Dependency, ComputedValue> variables, 
            final Set<Dependency> dependencies, final ValueResolver resolver, final AsyncCallback<String> callback, final Loadable... loadables) {
        
        if (dependencies.isEmpty()) {
            // Resolver is not needed, every required value is available.
            callback.onSuccess(computeValue(variables).toString());
            setLoading(false, loadables);
        } else {
            // Resolving values.
            resolver.resolve(dependencies, containerId, new AsyncCallback<Map<Dependency, ComputedValue>>() {

                @Override
                public void onFailure(Throwable caught) {
                    callback.onFailure(caught);
                    setLoading(false, loadables);
                }

                @Override
                public void onSuccess(Map<Dependency, ComputedValue> result) {
                    variables.putAll(result);
                    callback.onSuccess(computeValue(variables).toString());
                    setLoading(false, loadables);
                }
            });
        }
    }

    /**
     * Compute the value with the given variables.
     *
     * @param variables
     *          Values of the variables.
     *
     * @return Result of the computation.
     */
    ComputedValue computeValue(Map<Dependency, ComputedValue> variables) {
        final Stack<ComputedValue> stack = new Stack<ComputedValue>();

        for (final Instruction instruction : instructions) {
            instruction.execute(stack, variables);
        }

        return stack.peek();
    }

    /**
     * Retrieves the required dependencies.
     *
     * @return A set of the dependencies required to compute this rule.
     */
    public Set<Dependency> getDependencies() {
        if (dependencies != null) {
            return dependencies;
        }

        final LinkedHashSet<Dependency> elements = new LinkedHashSet<Dependency>();

        for (final Instruction instruction : instructions) {
            if (instruction instanceof Variable) {
                elements.add(((Variable) instruction).getDependency());
            }
        }

        this.dependencies = elements;

        return elements;
    }

    /**
     * Returns <code>true</code> if this computation was made from a bad formula.
     * 
     * @return <code>true</code> if this computation was made from a bad formula, <code>false</code> otherwise.
     */
    public boolean isBadFormula() {
        return instructions.size() == 1 && ComputedValues.from(toString()) == ComputationError.BAD_FORMULA;
    }
    
    /**
     * Find and return the bad references used in this computation.
     *
     * @return A set of every bad reference.
     */
    public Set<String> getBadReferences() {
        final HashSet<String> errors = new HashSet<String>();

        for (final Instruction instruction : instructions) {
            if (instruction instanceof BadVariable) {
                errors.add(((BadVariable) instruction).getReference());
            }
        }

        return errors;
    }
    
    /**
     * Identify the changes that are part of the dependencies of this computation.
     * 
     * @param changes
     *          List of changes.
     * @return The list of changes that made the given computation breach its constraints.
     */
    public List<ValueEventWrapper> getRelatedChanges(final List<ValueEventWrapper> changes) {
        final ArrayList<ValueEventWrapper> result = new ArrayList<ValueEventWrapper>();

        final Set<Dependency> dependencies = getDependencies();
        
        for (final ValueEventWrapper change : changes) {
            if (dependencies.contains(change.getSourceElement())) {
                result.add(change);
            }
        }
        
        return result;
    }

    /**
     * Returns this computation as a human readable <code>String</code>.
     * <p>
     * Flexible elements are referenced by their code.
     * </p>
     *
     * @return A String representing this computation.
     */
    public String toHumanReadableString() {
        return new ComputationStringBuilder()
				.setHumanReadableFormat(true)
				.add(instructions)
				.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ComputationStringBuilder()
				.add(instructions)
				.toString();
    }

    /**
     * Change the loading state of the given loadables.
     *
     * @param loading State to set.
     * @param loadables Loadable to change.
     */
    private void setLoading(final boolean loading, final Loadable... loadables) {
        for (final Loadable loadable : loadables) {
            if (loadable != null) {
                loadable.setLoading(loading);
            }
        }
    }

}
