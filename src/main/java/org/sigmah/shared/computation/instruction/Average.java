package org.sigmah.shared.computation.instruction;

import java.util.Map;
import java.util.Stack;
import org.sigmah.shared.computation.value.ComputedValue;
import org.sigmah.shared.computation.value.DoubleValue;

/**
 * Average function.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.2
 */
public class Average implements VariadicFuntion {

	private int numberOfArguments;
	
	@Override
	public void execute(Stack<ComputedValue> stack, Map<Integer, ComputedValue> variables) {
		final AverageReductor reductor = new AverageReductor();
		for (int index = 0; index < numberOfArguments; index++) {
			stack.pop().feedToReductor(reductor);
		}
		stack.push(reductor.reduce());
	}

	@Override
	public void setNumberOfArguments(int numberOfArguments) {
		this.numberOfArguments = numberOfArguments;
	}
	
	private static class AverageReductor implements Reductor {
		
		private ComputedValue sum = new DoubleValue(0.0);
		private int count;

		@Override
		public void feed(ComputedValue value) {
			sum = value.addTo(sum);
			count++;
		}

		@Override
		public ComputedValue reduce() {
			return new DoubleValue(count).divide(sum);
		}
		
	}
	
}
