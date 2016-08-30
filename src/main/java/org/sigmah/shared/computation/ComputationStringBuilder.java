package org.sigmah.shared.computation;

import java.util.Collection;
import java.util.Stack;
import org.sigmah.shared.computation.instruction.HasHumanReadableFormat;
import org.sigmah.shared.computation.instruction.Instruction;
import org.sigmah.shared.computation.instruction.Instructions;
import org.sigmah.shared.computation.instruction.Operator;
import org.sigmah.shared.computation.instruction.OperatorPriority;
import org.sigmah.shared.computation.instruction.ReduceFunction;
import org.sigmah.shared.util.ValueResultUtils;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.2
 */
class ComputationStringBuilder {
	
	private final Stack<String> stack = new Stack<String>();
	private boolean humanReadableFormat;

	public ComputationStringBuilder add(final Collection<Instruction> instructions) {
		for (final Instruction instruction : instructions) {
			add(instruction);
		}
		return this;
	}
	
	public ComputationStringBuilder add(final Instruction instruction) {
		if (instruction instanceof Operator) {
			addOperatorToStack((Operator) instruction);
		} else if (instruction instanceof ReduceFunction) {
			addReduceFunctionToStack((ReduceFunction) instruction);
		} else {
			if (humanReadableFormat && instruction instanceof HasHumanReadableFormat) {
				stack.add(((HasHumanReadableFormat) instruction).toHumanReadableString());
			} else {
				stack.add(instruction.toString());
			}
		}
		return this;
	}
	
	public ComputationStringBuilder setHumanReadableFormat(boolean humanReadableFormat) {
		this.humanReadableFormat = humanReadableFormat;
		return this;
	}

	@Override
	public String toString() {
		return stack.peek();
	}
	
	/**
     * Adds the given operator to the <code>Stack</code> of <code>String</code>s.
     *
     * @param operator Operator to add.
	 * @param stack Stack of Strings.
     */
    private void addOperatorToStack(final Operator operator) {
        final StringBuilder builder = new StringBuilder();
        final String right = stack.pop();

        if (operator.getPriority() == OperatorPriority.UNARY) {
            addUnaryOperatorToStack(builder, operator, right);
        } else if (operator.getPriority().ordinal() > OperatorPriority.ADD_SUBSTRACT.ordinal()) {
            addOperatorWithHighPriorityToStack(builder, operator, right);
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
    private void addOperatorWithHighPriorityToStack(final StringBuilder builder, final Operator operator, final String right) {
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
	
	private void addReduceFunctionToStack(final ReduceFunction reduceFunction) {
		final String dependency = stack.pop();
		final String[] parts = dependency.split(ValueResultUtils.DEFAULT_VALUE_SEPARATOR);
		
		if (parts.length != 3) {
			throw new IllegalArgumentException("The argument of a reduce function should be splittable into 3 parts.");
		}
		
		final String linkedProjectType = parts[0];
		final String modelName = replaceNullByEmptyString(parts[1]);
		final String fieldCode = replaceNullByEmptyString(parts[2]);
		
		stack.push(linkedProjectType + '(' + modelName + ")." + reduceFunction + '(' + fieldCode + ')');
	}
	
	private String replaceNullByEmptyString(String entry) {
		if (!"null".equals(entry)) {
			return entry;
		} else {
			return "";
		}
	}
	
}
