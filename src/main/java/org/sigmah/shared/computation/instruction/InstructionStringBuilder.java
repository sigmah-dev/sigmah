package org.sigmah.shared.computation.instruction;

import java.util.Stack;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class InstructionStringBuilder {
	
	private final Stack<String> stack = new Stack<String>();
	
	public void add(Instruction instruction) {
		if (instruction instanceof Operator) {
			addOperatorToStack((Operator) instruction);
		} else {
			stack.add(instruction.toString());
		}
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
}
