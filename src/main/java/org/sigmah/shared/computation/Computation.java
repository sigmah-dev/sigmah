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
import org.sigmah.shared.computation.instruction.BadVariable;
import org.sigmah.shared.computation.instruction.HasHumanReadableFormat;
import org.sigmah.shared.computation.instruction.Operator;
import org.sigmah.shared.computation.instruction.OperatorPriority;
import org.sigmah.shared.computation.instruction.Tag;
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
    private Set<FlexibleElementDTO> dependencies;

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

        final HashSet<FlexibleElementDTO> dependencies = new HashSet<FlexibleElementDTO>(getDependencies());

        final HashMap<Integer, ComputedValue> variables = new HashMap<Integer, ComputedValue>();
        for (final ValueEvent modification : modifications) {
            final FlexibleElementDTO source = modification.getSourceElement();
            variables.put(source.getId(), ComputedValues.from(modification.getSingleValue()));
            dependencies.remove(source);
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
        
        final HashSet<FlexibleElementDTO> dependencies = new HashSet<FlexibleElementDTO>(getDependencies());

        final HashMap<Integer, ComputedValue> variables = new HashMap<Integer, ComputedValue>();
        for (final ValueEventWrapper modification : modifications) {
            final FlexibleElementDTO source = modification.getSourceElement();
            variables.put(source.getId(), ComputedValues.from(modification.getSingleValue()));
            dependencies.remove(source);
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
        computeValueWithVariablesDependenciesAndResolver(containerId, new HashMap<Integer, ComputedValue>(), getDependencies(), resolver, callback);
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
    private void computeValueWithVariablesDependenciesAndResolver(final int containerId, final Map<Integer, ComputedValue> variables, 
            final Set<FlexibleElementDTO> dependencies, final ValueResolver resolver, final AsyncCallback<String> callback, final Loadable... loadables) {
        
        if (dependencies.isEmpty()) {
            // Resolver is not needed, every required value is available.
            callback.onSuccess(computeValue(variables).toString());
            setLoading(false, loadables);
        } else {
            // Resolving values.
            resolver.resolve(dependencies, containerId, new AsyncCallback<Map<Integer, ComputedValue>>() {

                @Override
                public void onFailure(Throwable caught) {
                    callback.onFailure(caught);
                    setLoading(false, loadables);
                }

                @Override
                public void onSuccess(Map<Integer, ComputedValue> result) {
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
    ComputedValue computeValue(Map<Integer, ComputedValue> variables) {
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
    public Set<FlexibleElementDTO> getDependencies() {
        if (dependencies != null) {
            return dependencies;
        }

        final LinkedHashSet<FlexibleElementDTO> elements = new LinkedHashSet<FlexibleElementDTO>();

        for (final Instruction instruction : instructions) {
            if (instruction instanceof Variable) {
                elements.add(((Variable) instruction).getFlexibleElement());
            }
			else if (instruction instanceof Tag) {
				// TODO: Ajouter 
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

        final Set<FlexibleElementDTO> dependencies = getDependencies();
        
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
        final Stack<String> stack = new Stack<String>();

        for (final Instruction instruction : instructions) {
            if (instruction instanceof Operator) {
                addOperatorToStack((Operator) instruction, stack);
            } else if (instruction instanceof HasHumanReadableFormat) {
                stack.add(((HasHumanReadableFormat) instruction).toHumanReadableString());
            } else {
                stack.add(instruction.toString());
            }
        }

        return stack.peek();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final Stack<String> stack = new Stack<String>();

        for (final Instruction instruction : instructions) {
            if (instruction instanceof Operator) {
                addOperatorToStack((Operator) instruction, stack);
            } else {
                stack.add(instruction.toString());
            }
        }

        return stack.peek();
    }

    /**
     * Adds the given operator to the <code>Stack</code> of <code>String</code>s.
     *
     * @param operator Operator to add.
	 * @param stack Stack of Strings.
     */
    private void addOperatorToStack(final Operator operator, final Stack<String> stack) {
        final StringBuilder builder = new StringBuilder();
        final String right = stack.pop();

        if (operator.getPriority() == OperatorPriority.UNARY) {
            addUnaryOperatorToStack(builder, operator, right);
        } else if (operator.getPriority().ordinal() > OperatorPriority.ADD_SUBSTRACT.ordinal()) {
            addOperatorWithHighPriorityToStack(stack, builder, operator, right);
        } else {
            builder.append(stack.pop())
                    .append(' ').append(operator).append(' ')
                    .append(right);
        }

        stack.add(builder.toString());
    }

    /**
     * Adds the given operator to the <code>Stack</code> of <code>String</code>s.
     *
	 * @param stack Stack of Strings.
	 * @param builder Builder for the current operator <code>String</code>.
	 * @param operator Operator to add.
	 * @param right Right operand.
     */
    private void addOperatorWithHighPriorityToStack(final Stack<String> stack, final StringBuilder builder, final Operator operator, final String right) {
        final String left = stack.pop();
        if (left.contains(" ")) {
            builder.append('(').append(left).append(") ");
        } else {
            builder.append(left).append(' ');
        }
        builder.append(operator);
        if (right.contains(" ")) {
            builder.append(" (").append(right).append(')');
        } else {
            builder.append(' ').append(right);
        }
    }

    /**
     * Adds the given operator to the <code>Stack</code> of <code>String</code>s.
     *
	 * @param builder Builder for the current operator <code>String</code>.
	 * @param operator Operator to add.
	 * @param right Right operand.
     */
    private void addUnaryOperatorToStack(final StringBuilder builder, final Operator operator, final String right) {
        builder.append(operator);
        if (right.contains(" ")) {
            builder.append('(').append(right).append(')');
        } else {
            builder.append(right);
        }
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
