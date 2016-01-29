package org.sigmah.shared.computation;

import org.sigmah.shared.computation.instruction.BadVariable;
import org.sigmah.shared.computation.instruction.Instructions;
import org.sigmah.shared.computation.instruction.Operator;
import org.sigmah.shared.computation.instruction.Variable;
import org.sigmah.shared.dto.element.FlexibleElementDTO;

/**
 * States of the <code>Computation</code> parser.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.1
 */
enum ParserState {
	
	/**
	 * Waiting for an operand. Initial state of the parser.
	 */
	WAITING_FOR_OPERAND {

		@Override
		int execute(final int offset, final char[] array, final ParserEnvironment environment) {
			for (int index = offset; index < array.length; index++) {
				final char c = array[index];
				if (isDigit(c)) {
					environment.setState(CONSTANT);
					return index;
				} else if (c == MINUS_ALIAS) {
					environment.setState(UNARY_OPERATOR);
					return index;
				} else if (c == LEFT_PARENTHESIS) {
					environment.pushContext();
				} else if (c == RIGHT_PARENTHESIS) {
					environment.popContext();
				} else if (c == ID_MARK) {
					environment.setState(VARIABLE_ID);
					return index;
				} else if (isLetter(c) || c == '_') {
					environment.setState(VARIABLE_CODE);
					return index;
				} else if (!isSpace(c)) {
                    throw new IllegalArgumentException("Formula '" 
                            + new String(array)
                            + "' is unparseable. Got bad element '" + c 
                            + "', accepted elements are: "
                            + "[0-9], " 
                            + MINUS_ALIAS + ", " 
                            + LEFT_PARENTHESIS + ", " 
                            + RIGHT_PARENTHESIS + ", "
                            + ID_MARK + ", "
                            + "[a-zA-Z] or space.");
                }
			}
			return array.length;
		}
		
	},
	
	/**
	 * Reading a constant value.
	 */
	CONSTANT {

		@Override
		int execute(final int offset, final char[] array, final ParserEnvironment environment) {
			final StringBuilder constantBuilder = new StringBuilder();
			
			for (int index = offset; index < array.length; index++) {
				final char c = array[index];
				if (isDigit(c)) {
					constantBuilder.append(c);
				} else if (isDecimalMark(c)) {
					constantBuilder.append(DECIMAL_MARK);
				} else {
					addConstant(constantBuilder.toString(), environment);
					environment.setState(WAITING_FOR_OPERATOR);
					return index;
				}
			}
			
			addConstant(constantBuilder.toString(), environment);
			return array.length;
		}
	},
	
	/**
	 * Reading a flexible element identifier.
	 */
	VARIABLE_ID {

		@Override
		int execute(final int offset, final char[] array, final ParserEnvironment environment) {
			final StringBuilder idBuilder = new StringBuilder();
			
			for (int index = offset; index < array.length; index++) {
				final char c = array[index];
				if (isDigit(c) || c == ID_MARK) {
					idBuilder.append(c);
				} else {
					addVariable(idBuilder.toString(), environment);
					environment.setState(WAITING_FOR_OPERATOR);
					return index;
				}
			}
			
			addVariable(idBuilder.toString(), environment);
			return array.length;
		}
		
	},
	
	/**
	 * Reading a flexible element code.
	 */
	VARIABLE_CODE {

		@Override
		int execute(final int offset, final char[] array, final ParserEnvironment environment) {
			final StringBuilder codeBuilder = new StringBuilder();
			
			for (int index = offset; index < array.length; index++) {
				final char c = array[index];
				if (isLetter(c) || isDigit(c) || c == '_') {
					codeBuilder.append(c);
				} else {
					addVariable(codeBuilder.toString(), environment);
					environment.setState(WAITING_FOR_OPERATOR);
					return index;
				}
			}
			
			addVariable(codeBuilder.toString(), environment);
			return array.length;
		}
		
	},
	
	/**
	 * Waiting for an operator.
	 */
	WAITING_FOR_OPERATOR {

		@Override
		int execute(final int offset, final char[] array, final ParserEnvironment environment) {
			for (int index = offset; index < array.length; index++) {
				final char c = array[index];
				if (c == RIGHT_PARENTHESIS) {
					environment.popContext();
                    
                } else if (c == LEFT_PARENTHESIS && environment.lastInstruction() instanceof BadVariable) {
                    throw new UnsupportedOperationException("Functions are not supported yet.");
					
				} else if (!isSpace(c)) {
					environment.setState(OPERATOR);
					return index;
				}
			}
			return array.length;
		}
	},
	
	/**
	 * Reading an operator.
	 */
	OPERATOR {

		@Override
		int execute(final int offset, final char[] array, final ParserEnvironment environment) {
			final StringBuilder operatorBuilder = new StringBuilder();
			
			for (int index = offset; index < array.length; index++) {
				final char c = array[index];
				if (!isSpace(c) && !isDigit(c) && c != LEFT_PARENTHESIS) {
					operatorBuilder.append(c);
				} else {
					pushOperator(operatorBuilder.toString(), environment);
					environment.setState(WAITING_FOR_OPERAND);
					return index;
				}
			}
			
			pushOperator(operatorBuilder.toString(), environment);
			return array.length;
		}
	},
	
	/**
	 * Reading an unary operator.
	 */
	UNARY_OPERATOR {

		@Override
		int execute(final int offset, final char[] array, final ParserEnvironment environment) {
			if (array[offset] == MINUS_ALIAS) {
				pushOperator(MINUS_OPERATOR, environment);
			}
			environment.setState(WAITING_FOR_OPERAND);
			return offset + 1;
		}
	};
    
    private static final char LEFT_PARENTHESIS = '(';
    private static final char RIGHT_PARENTHESIS = ')';
    private static final char DECIMAL_MARK = '.';
    private static final char ID_MARK = Instructions.ID_PREFIX;
    private static final char MINUS_ALIAS = '-';
    private static final String MINUS_OPERATOR = "minus";
	
	/**
	 * Execute the current state.
	 * 
	 * @param offset Where to start the analysis.
	 * @param array Rule to parse.
	 * @param environment Environment.
	 * @return New offset where to continue the analysis.
	 */
	abstract int execute(int offset, char[] array, ParserEnvironment environment);
	
	/**
	 * Returns <code>true</code> if the given character is a digit.
	 * 
	 * @param c Character to test.
	 * @return <code>true</code> if the given character is a digit,
	 * <code>false</code> otherwise.
	 */
	private static boolean isDigit(final char c) {
		return c >= '0' && c <= '9';
	}
	
	/**
	 * Returns <code>true</code> if the given character is a decimal mark.
	 * 
	 * @param c Character to test.
	 * @return <code>true</code> if the given character is a decimal mark,
	 * <code>false</code> otherwise.
	 */
	private static boolean isDecimalMark(final char c) {
		return c == ',' || c == '.';
	}
	
	/**
	 * Returns <code>true</code> if the given character is a space.
	 * 
	 * @param c Character to test.
	 * @return <code>true</code> if the given character is a space,
	 * <code>false</code> otherwise.
	 */
	private static boolean isSpace(final char c) {
		return c == ' ';
	}
	
	/**
	 * Returns <code>true</code> if the given character is a letter.
	 * 
	 * @param c Character to test.
	 * @return <code>true</code> if the given character is a letter,
	 * <code>false</code> otherwise.
	 */
	private static boolean isLetter(final char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}
	
	/**
	 * Adds the given constant to the instructions.
	 * 
	 * @param constant Constant to add.
	 * @param environment Environment.
	 */
	private static void addConstant(final String constant, final ParserEnvironment environment) {
		if (constant.length() > 0) {
			environment.add(Instructions.getConstantWithValue(constant));
		}
	}
	
	/**
	 * Adds the given variable to the instructions.
	 * 
	 * @param variable Variable to add.
	 * @param environment Environment.
	 */
	private static void addVariable(final String variable, final ParserEnvironment environment) {
		final FlexibleElementDTO element = environment.getElement(variable);
		if (element != null) {
			environment.add(new Variable(element));
		} else {
			environment.add(new BadVariable(variable));
		}
	}
	
	/**
	 * Push the given operator to the operator stack. It will be added to the
	 * instructions after reading the next operator and if its priority is
	 * superior.
	 * 
	 * @param operator Operator to push.
	 * @param environment Environment.
	 */
	private static void pushOperator(final String operatorName, final ParserEnvironment environment) {
		final Operator operator = Instructions.getOperatorNamed(operatorName);
		
		if (operator != null) {
			final int parentPriority = environment.peekOfStack() == null ? -1 : environment.peekOfStack().getPriority().ordinal();
			final int thisPriority = operator.getPriority().ordinal() - 1;
			
			if(parentPriority > thisPriority) {
				environment.add(environment.popFromStack());
			}
		
			environment.pushOnStack(operator);
			
		} else {
			throw new IllegalArgumentException("Operator '" + operatorName + "' is invalid.");
		}
	}
	
}
