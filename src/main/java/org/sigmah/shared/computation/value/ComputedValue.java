package org.sigmah.shared.computation.value;

/**
 * Value computed by a <code>Computation</code>.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.1
 */
public interface ComputedValue {
	
	/**
	 * Retrieves the double value of this object.
	 * 
	 * @return Double value or <code>null</code> if no value is present.
	 */
	Double get();
	
	/**
	 * Add the given value to this one.
	 * <p>
	 * Result will be equals to "<code>other + this</code>".
	 * </p>
	 * 
	 * @param other Value to add.
	 * @return A new value combining this value and the given one.
	 */
	ComputedValue addTo(ComputedValue other);
	
	/**
	 * Multiply the given value to this one.
	 * <p>
	 * Result will be equals to "<code>other * this</code>".
	 * </p>
	 * 
	 * @param other Value to multiply.
	 * @return A new value combining this value and the given one.
	 */
	ComputedValue multiplyWith(ComputedValue other);
	
	/**
	 * Divide the given value by this one.
	 * <p>
	 * Result will be equals to "<code>other / this</code>".
	 * </p>
	 * 
	 * @param other Value to divide.
	 * @return A new value combining this value and the given one.
	 */
	ComputedValue divide(ComputedValue other);
	
	/**
	 * Substract this value from the given one.
	 * <p>
	 * Result will be equals to "<code>other - this</code>".
	 * </p>
	 * 
	 * @param other Value to substract from.
	 * @return A new value combining this value and the given one.
	 */
	ComputedValue substractFrom(ComputedValue other);
	
}
