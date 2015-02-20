package org.sigmah;


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
