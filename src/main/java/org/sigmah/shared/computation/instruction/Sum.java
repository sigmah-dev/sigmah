package org.sigmah.shared.computation.instruction;

import java.util.Map;
import java.util.Stack;
import org.sigmah.shared.computation.dependency.Dependency;
import org.sigmah.shared.computation.value.ComputedValue;
import org.sigmah.shared.computation.value.DoubleValue;

/**
 * Sum function.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.2
 */
public class Sum implements ReduceFunction, VariadicFunction {

	private int numberOfArguments = 1;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "sum";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(Stack<ComputedValue> stack, Map<Dependency, ComputedValue> variables) {
		final SumReductor reductor = new SumReductor();
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
		return new Sum();
	}
	
	/**
	 * Reductor used to calculate the sum.
	 */
	private static class SumReductor implements Reductor {
		
		private ComputedValue sum = new DoubleValue(0.0);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void feed(ComputedValue value) {
			sum = value.addTo(sum);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ComputedValue reduce() {
			return sum;
		}
		
	}
	
}
