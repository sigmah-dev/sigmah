package org.sigmah.shared.computation.instruction;

import java.util.Map;
import java.util.Stack;
import org.sigmah.shared.computation.dependency.Dependency;
import org.sigmah.shared.computation.value.ComputedValue;
import org.sigmah.shared.computation.value.DoubleValue;

/**
 * Average function.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.2
 */
public class Average implements ReduceFunction, VariadicFunction {

	private int numberOfArguments = 1;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "avg";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(Stack<ComputedValue> stack, Map<Dependency, ComputedValue> variables) {
		final AverageReductor reductor = new AverageReductor();
		for (int index = 0; index < numberOfArguments; index++) {
			stack.pop().feedToReductor(reductor);
		}
		stack.push(reductor.reduce());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setNumberOfArguments(int numberOfArguments) {
		this.numberOfArguments = numberOfArguments;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Function instantiate() {
		return new Average();
	}
	
	/**
	 * Reductor used to calculate the sum.
	 */
	private static class AverageReductor implements Reductor {
		
		private ComputedValue sum = new DoubleValue(0.0);
		private int count;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void feed(ComputedValue value) {
			sum = value.addTo(sum);
			count++;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ComputedValue reduce() {
			if (count > 0) {
				return new DoubleValue(count).divide(sum);
			} else {
				return DoubleValue.ZERO;
			}
		}
		
	}
	
}
