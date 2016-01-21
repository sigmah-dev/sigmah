package org.sigmah;

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



import org.junit.Assert;
import org.junit.Test;
import org.sigmah.client.util.NumberUtils;


/**
 * Test of the ratio method from {@link NumberUtils}.
 * 
 * @author Abderrazek Chine (abderrazek.chine@netapsys.fr)
 */
public class DecFormatageTest  {

	@Test
	public void test() {
		Double res=NumberUtils.ratio(50, 100);
		
		Assert.assertEquals(res,50.00,0.0);
		
		Double ret=Double.valueOf( NumberUtils.truncate(76.8976543, 2) );
		
		Assert.assertEquals(ret,76.89,0.0);
	}
	
}
