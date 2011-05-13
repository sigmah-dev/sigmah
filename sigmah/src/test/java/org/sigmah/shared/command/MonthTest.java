package org.sigmah.shared.command;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

public class MonthTest {

	
	@Test
	public void rollover() {
		
		Month june2010 = new Month(2010, 6);
	
		Month elevenMonthsLater = june2010.plus(11);
		
		assertThat(elevenMonthsLater.getYear(), equalTo(2011));
		assertThat(elevenMonthsLater.getMonth(), equalTo(5));
		
	}
	
	@Test
	public void rollback() {
		
		Month jan2010 = new Month(2010,1);
		Month earlier = jan2010.previous();
		
		assertThat(earlier.getYear(), equalTo(2009));
		assertThat(earlier.getMonth(), equalTo(12));
		
	}
	
}
