/**
 *
 */
package wblut.geom;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import wblut.math.WB_Epsilon;
import wblut.math.WB_M33;

/**
 * @author FVH
 *
 */
public class WB_VectorTest {

	WB_Vector v1=new WB_Vector();
	WB_Vector v2=new WB_Vector();
	WB_Vector v3=new WB_Vector();
	double[] coord;
	WB_Vector result;
	double res;
	double f,g;
	WB_Vector expectedResult;

	/**
	 * Test method for {@link wblut.geom.WB_Vector#addSelf(double[])}.
	 */
	@Test
	public void testAddSelfDoubleArrayLength3() {
		v1.set(1,2,3);
		coord=new double[]{4,5,6};
		result=v1.addSelf(coord);
		assertSame("addSelf should return calling WB_Vector.", v1, result);
		expectedResult=new WB_Vector(5,7,9);
		assertEquals("addSelf gives unexpected result.", expectedResult, result);
	}

	@Test
	public void testAddSelfDoubleArrayLength2() {
		v1.set(1,2,3);
		coord=new double[]{4,5};
		result=v1.addSelf(coord);
		expectedResult=new WB_Vector(5,7,3);
		assertEquals("addSelf gives unexpected result.", expectedResult, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddSelfDoubleArrayLength1() {
		v1.set(1,2,3);
		coord=new double[]{4};
		result=v1.addSelf(coord);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddSelfDoubleArrayLength4() {
		v1.set(1,2,3);
		coord=new double[]{4,5,6,7};
		result=v1.addSelf(coord);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#addInto(wblut.geom.WB_MutableCoord, double[])}.
	 */
	@Test
	public void testAddIntoWB_MutableCoordDoubleArrayLength3() {
		v1.set(1,2,3);
		coord=new double[]{4,5};
		v1.addInto(v2,coord);
		expectedResult=new WB_Vector(5,7,3);
		assertEquals("addInto gives unexpected result.", expectedResult,v2);
		expectedResult=new WB_Vector(1,2,3);
		assertEquals("addInto modifies calling WB_Vector.", expectedResult,v1);
	}

	@Test
	public void testAddIntoWB_MutableCoordDoubleArrayLength2() {
		v1.set(1,2,3);
		coord=new double[]{4,5};
		v1.addInto(v2,coord);
		expectedResult=new WB_Vector(5,7,3);
		assertEquals("addInto gives unexpected result.", expectedResult,v2);
		expectedResult=new WB_Vector(1,2,3);
		assertEquals("addInto modifies calling WB_Vector.", expectedResult,v1);
	}



	@Test(expected = IllegalArgumentException.class)
	public void testAddIntoDoubleArrayLength1() {
		v1.set(1,2,3);
		coord=new double[]{4};
		v1.addInto(v2,coord);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddIntoDoubleArrayLength4() {
		v1.set(1,2,3);
		coord=new double[]{4,5,6,7};
		v1.addInto(v2,coord);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#add(double[])}.
	 */
	@Test
	public void testAddDoubleArrayLength3() {
		v1.set(1,2,3);
		coord=new double[]{4,5,6};
		result=v1.add(coord);
		expectedResult=new WB_Vector(5,7,9);
		assertEquals("add gives unexpected result.", expectedResult, result);
		expectedResult=new WB_Vector(1,2,3);
		assertEquals("add modifies calling WB_Vector.", expectedResult,v1);
		assertThat("add does not return WB_Vector.",result, instanceOf(WB_Vector.class));
	}

	@Test
	public void testAddDoubleArrayLength2() {
		v1.set(1,2,3);
		coord=new double[]{4,5};
		result=v1.add(coord);
		expectedResult=new WB_Vector(5,7,3);
		assertEquals("add gives unexpected result.", expectedResult, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddDoubleArrayLength1() {
		v1.set(1,2,3);
		coord=new double[]{4};
		result=v1.add(coord);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddDoubleArrayLength4() {
		v1.set(1,2,3);
		coord=new double[]{4,5,6,7};
		result=v1.add(coord);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#addSelf(double, double, double)}.
	 */
	@Test
	public void testAddSelfDoubleDoubleDouble() {
		v1.set(1,2,3);
		result=v1.addSelf(4,5,6);
		assertSame("addSelf should return calling WB_Vector.", v1, result);
		expectedResult=new WB_Vector(5,7,9);
		assertEquals("addSelf gives unexpected result.", expectedResult, result);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#addSelf(wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testAddSelfWB_Coord() {
		v1.set(1,2,3);
		v2.set(4,5,6);
		result=v1.addSelf(v2);
		assertSame("addSelf should return calling WB_Vector.", v1, result);
		expectedResult=new WB_Vector(5,7,9);
		assertEquals("addSelf gives unexpected result.", expectedResult, result);
		expectedResult=new WB_Vector(4,5,6);
		assertEquals("addSelf modifies argument WB_Vector.", expectedResult,v2);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#addInto(wblut.geom.WB_MutableCoord, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testAddIntoWB_MutableCoordWB_Coord() {
		v1.set(1,2,3);
		v2.set(4,5,6);
		result=new WB_Vector();
		v1.addInto(result, v2);
		expectedResult=new WB_Vector(5,7,9);
		assertEquals("addInto gives unexpected result.", expectedResult, result);
		expectedResult=new WB_Vector(1,2,3);
		assertEquals("addInto modifies calling WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(4,5,6);
		assertEquals("addInto modifies argument WB_Vector.", expectedResult,v2);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#add(wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testAddWB_Coord() {
		v1.set(1,2,3);
		v2.set(4,5,6);
		result=v1.add(v2);
		expectedResult=new WB_Vector(5,7,9);
		assertEquals("add gives unexpected result.", expectedResult, result);
		expectedResult=new WB_Vector(1,2,3);
		assertEquals("add modifies calling WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(4,5,6);
		assertEquals("add modifies argument WB_Vector.", expectedResult,v2);
		assertThat("add does not return WB_Vector.",result, instanceOf(WB_Vector.class));
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#add(wblut.geom.WB_Coord, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testAddWB_CoordWB_Coord() {
		v1.set(1,2,3);
		v2.set(4,5,6);
		result=WB_Vector.add(v1,v2);
		expectedResult=new WB_Vector(5,7,9);
		assertEquals("add gives unexpected result.", expectedResult, result);
		expectedResult=new WB_Vector(1,2,3);
		assertEquals("add modifies argument WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(4,5,6);
		assertEquals("add modifies argument WB_Vector.", expectedResult,v2);
		assertThat("add does not return WB_Vector.",result, instanceOf(WB_Vector.class));
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#subSelf(double[])}.
	 */
	@Test
	public void testSubSelfDoubleArrayLength3() {
		v1.set(1,2,3);
		coord=new double[]{4,5,6};
		result=v1.subSelf(coord);
		assertSame("subSelf should return calling WB_Vector.", v1, result);
		expectedResult=new WB_Vector(-3,-3,-3);
		assertEquals("subSelf gives unexpected result.", expectedResult, result);
	}

	@Test
	public void testSubSelfDoubleArrayLength2() {
		v1.set(1,2,3);
		coord=new double[]{4,5};
		result=v1.subSelf(coord);
		expectedResult=new WB_Vector(-3,-3,3);
		assertEquals("subSelf gives unexpected result.", expectedResult, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSubSelfDoubleArrayLength1() {
		v1.set(1,2,3);
		coord=new double[]{4};
		result=v1.subSelf(coord);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSubSelfDoubleArrayLength4() {
		v1.set(1,2,3);
		coord=new double[]{4,5,6,7};
		result=v1.subSelf(coord);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#sub(double[])}.
	 */
	@Test
	public void testSubDoubleArrayLength3() {
		v1.set(1,2,3);
		coord=new double[]{4,5,6};
		result=v1.sub(coord);
		expectedResult=new WB_Vector(-3,-3,-3);
		assertEquals("sub gives unexpected result.", expectedResult, result);
		expectedResult=new WB_Vector(1,2,3);
		assertEquals("sub modifies calling WB_Vector.", expectedResult,v1);
		assertThat("sub does not return WB_Vector.",result, instanceOf(WB_Vector.class));
	}
	@Test
	public void testSubDoubleArrayLength2() {
		v1.set(1,2,3);
		coord=new double[]{4,5};
		result=v1.sub(coord);
		expectedResult=new WB_Vector(-3,-3,3);
		assertEquals("sub gives unexpected result.", expectedResult, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSubDoubleArrayLength1() {
		v1.set(1,2,3);
		coord=new double[]{4};
		result=v1.add(coord);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSubDoubleArrayLength4() {
		v1.set(1,2,3);
		coord=new double[]{4,5,6,7};
		result=v1.add(coord);
	}
	/**
	 * Test method for {@link wblut.geom.WB_Vector#subInto(wblut.geom.WB_MutableCoord, double[])}.
	 */
	@Test
	public void testSubIntoWB_MutableCoordDoubleArrayLength3() {
		v1.set(1,2,3);
		coord=new double[]{4,5,6};
		v1.subInto(v2,coord);
		expectedResult=new WB_Vector(-3,-3,-3);
		assertEquals("subInto gives unexpected result.", expectedResult,v2);
		expectedResult=new WB_Vector(1,2,3);
		assertEquals("subInto modifies calling WB_Vector.", expectedResult,v1);


	}

	@Test
	public void testSubIntoWB_MutableCoordDoubleArrayLength2() {
		v1.set(1,2,3);
		coord=new double[]{4,5};
		v1.subInto(v2,coord);
		expectedResult=new WB_Vector(-3,-3,3);
		assertEquals("subInto gives unexpected result.", expectedResult,v2);
		expectedResult=new WB_Vector(1,2,3);
		assertEquals("subInto modifies calling WB_Vector.", expectedResult,v1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSubIntoDoubleArrayLength1() {
		v1.set(1,2,3);
		coord=new double[]{4};
		v1.subInto(v2,coord);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSubIntoDoubleArrayLength4() {
		v1.set(1,2,3);
		coord=new double[]{4,5,6,7};
		v1.subInto(v2,coord);
	}


	/**
	 * Test method for {@link wblut.geom.WB_Vector#subSelf(wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testSubSelfWB_Coord() {
		v1.set(1,2,3);
		v2.set(4,5,6);
		result=v1.subSelf(v2);
		assertSame("subSelf should return calling WB_Vector.", v1, result);
		expectedResult=new WB_Vector(-3,-3,-3);
		assertEquals("subSelf gives unexpected result.", expectedResult, result);
		expectedResult=new WB_Vector(4,5,6);
		assertEquals("subSelf modifies argument WB_Vector.", expectedResult,v2);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#sub(wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testSubWB_Coord() {
		v1.set(1,2,3);
		v2.set(4,5,6);
		result=v1.sub(v2);
		expectedResult=new WB_Vector(-3,-3,-3);
		assertEquals("sub gives unexpected result.", expectedResult, result);
		expectedResult=new WB_Vector(1,2,3);
		assertEquals("sub modifies calling WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(4,5,6);
		assertEquals("sub modifies argument WB_Vector.", expectedResult,v2);
		assertThat("sub does not return WB_Vector.",result, instanceOf(WB_Vector.class));
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#subInto(wblut.geom.WB_MutableCoord, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testSubIntoWB_MutableCoordWB_Coord() {
		v1.set(1,2,3);
		v2.set(4,5,6);
		result=new WB_Vector();
		v1.subInto(result,v2);
		expectedResult=new WB_Vector(-3,-3,-3);
		assertEquals("subInto gives unexpected result.", expectedResult, result);
		expectedResult=new WB_Vector(1,2,3);
		assertEquals("subInto modifies calling WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(4,5,6);
		assertEquals("subInto modifies argument WB_Vector.", expectedResult,v2);


	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#sub(wblut.geom.WB_Coord, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testSubWB_CoordWB_Coord() {
		v1.set(1,2,3);
		v2.set(4,5,6);
		result=WB_Vector.sub(v1,v2);
		expectedResult=new WB_Vector(-3,-3,-3);
		assertEquals("sub gives unexpected result.", expectedResult, result);
		expectedResult=new WB_Vector(1,2,3);
		assertEquals("sub modifies calling WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(4,5,6);
		assertEquals("sub modifies argument WB_Vector.", expectedResult,v2);
		assertThat("sub does not return WB_Vector.",result, instanceOf(WB_Vector.class));
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#mulSelf(double)}.
	 */
	@Test
	public void testMulSelf() {
		v1.set(1,2,3);
		f=2;
		result=v1.mulSelf(f);
		assertSame("mulSelf should return calling WB_Vector.", v1, result);
		expectedResult=new WB_Vector(2,4,6);
		assertEquals("mulSelf gives unexpected result.", expectedResult, result);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#mulInto(wblut.geom.WB_MutableCoord, double)}.
	 */
	@Test
	public void testMulInto() {
		v1.set(1,2,3);
		f=2;
		v1.mulInto(v2,f);
		expectedResult=new WB_Vector(2,4,6);
		assertEquals("mulInto gives unexpected result.", expectedResult,v2);
		expectedResult=new WB_Vector(1,2,3);
		assertEquals("mulInto modifies calling WB_Vector.", expectedResult,v1);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#mul(double)}.
	 */
	@Test
	public void testMulDouble() {
		v1.set(1,2,3);
		f=2;
		result=v1.mul(f);
		expectedResult=new WB_Vector(2,4,6);
		assertEquals("mul gives unexpected result.", expectedResult, result);
		expectedResult=new WB_Vector(1,2,3);
		assertEquals("mul modifies calling WB_Vector.", expectedResult,v1);
		assertThat("mul does not return WB_Vector.",result, instanceOf(WB_Vector.class));
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#mul(wblut.geom.WB_Coord, double)}.
	 */
	@Test
	public void testMulWB_CoordDouble() {
		v1.set(1,2,3);
		f=2;
		result=WB_Vector.mul(v1,f);
		expectedResult=new WB_Vector(2,4,6);
		assertEquals("mul gives unexpected result.", expectedResult, result);
		expectedResult=new WB_Vector(1,2,3);
		assertEquals("mul modifies calling WB_Vector.", expectedResult,v1);
		assertThat("mul does not return WB_Vector.",result, instanceOf(WB_Vector.class));
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#scaleSelf(double)}.
	 */
	@Test
	public void testScaleSelfDouble() {
		v1.set(1,2,3);
		f=2;
		result=v1.scaleSelf(f);
		assertSame("scaleSelf should return calling WB_Vector.", v1, result);
		expectedResult=new WB_Vector(2,4,6);
		assertEquals("scaleSelf gives unexpected result.", expectedResult, result);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#scaleInto(wblut.geom.WB_MutableCoord, double)}.
	 */
	@Test
	public void testScaleIntoWB_MutableCoordDouble() {
		v1.set(1,2,3);
		f=2;
		v1.scaleInto(v2,f);
		expectedResult=new WB_Vector(2,4,6);
		assertEquals("scaleInto gives unexpected result.", expectedResult,v2);
		expectedResult=new WB_Vector(1,2,3);
		assertEquals("scaleInto modifies calling WB_Vector.", expectedResult,v1);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#scale(double)}.
	 */
	@Test
	public void testScaleDouble() {
		v1.set(1,2,3);
		f=2;
		result=v1.scale(f);
		expectedResult=new WB_Vector(2,4,6);
		assertEquals("scale gives unexpected result.", expectedResult, result);
		expectedResult=new WB_Vector(1,2,3);
		assertEquals("scale modifies calling WB_Vector.", expectedResult,v1);
		assertThat("scale does not return WB_Vector.",result, instanceOf(WB_Vector.class));
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#scaleSelf(double, double, double)}.
	 */
	@Test
	public void testScaleSelfDoubleDoubleDouble() {
		v1.set(1,2,3);
		f=2;
		result=v1.scaleSelf(f,2*f,3*f);
		assertSame("scaleSelf should return calling WB_Vector.", v1, result);
		expectedResult=new WB_Vector(2,8,18);
		assertEquals("scaleSelf gives unexpected result.", expectedResult, result);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#scaleInto(wblut.geom.WB_MutableCoord, double, double, double)}.
	 */
	@Test
	public void testScaleIntoWB_MutableCoordDoubleDoubleDouble() {
		v1.set(1,2,3);
		f=2;
		v1.scaleInto(v2,f,f*2,f*3);
		expectedResult=new WB_Vector(2,8,18);
		assertEquals("scaleInto gives unexpected result.", expectedResult,v2);
		expectedResult=new WB_Vector(1,2,3);
		assertEquals("scaleInto modifies calling WB_Vector.", expectedResult,v1);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#scale(double, double, double)}.
	 */
	@Test
	public void testScaleDoubleDoubleDouble() {
		v1.set(1,2,3);
		f=2;
		result=v1.scale(f,2*f,3*f);
		expectedResult=new WB_Vector(2,8,18);
		assertEquals("scale gives unexpected result.", expectedResult, result);
		expectedResult=new WB_Vector(1,2,3);
		assertEquals("scale modifies calling WB_Vector.", expectedResult,v1);
		assertThat("scale does not return WB_Vector.",result, instanceOf(WB_Vector.class));
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#invert()}.
	 */
	@Test
	public void testInvert() {
		v1.set(1,2,3);
		v1.invert();
		expectedResult=new WB_Vector(-1,-2,-3);
		assertEquals("invert gives unexpected result.", expectedResult,v1);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#addMulSelf(double, double[])}.
	 */
	@Test
	public void testAddMulSelfDoubleDoubleArrayLength3() {
		v1.set(1,2,3);
		coord=new double[]{4,5,6};
		f=2;
		result=v1.addMulSelf(f,coord);
		assertSame("addMulSelf should return calling WB_Vector.", v1, result);
		expectedResult=new WB_Vector(9,12,15);
		assertEquals("addMulSelf gives unexpected result.", expectedResult, result);

	}
	@Test
	public void testAddMulSelfDoubleDoubleArrayLength2() {
		v1.set(1,2,3);
		coord=new double[]{4,5};
		f=2;
		result=v1.addMulSelf(f,coord);
		assertSame("addMulSelf should return calling WB_Vector.", v1, result);
		expectedResult=new WB_Vector(9,12,3);
		assertEquals("addMulSelf gives unexpected result.", expectedResult, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddMulSelfDoubleArrayLength1() {
		v1.set(1,2,3);
		coord=new double[]{4};
		f=2;
		v1.addMulSelf(f,coord);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddMulSelfDoubleArrayLength4() {
		v1.set(1,2,3);
		coord=new double[]{4,5,6,7};
		f=2;
		v1.addMulSelf(f,coord);
	}


	/**
	 * Test method for {@link wblut.geom.WB_Vector#addMulInto(wblut.geom.WB_MutableCoord, double, double[])}.
	 */
	@Test
	public void testAddMulIntoWB_MutableCoordDoubleDoubleArrayLength3() {
		v1.set(1,2,3);
		coord=new double[]{4,5,6};
		f=2;
		v1.addMulInto(v2,f,coord);
		expectedResult=new WB_Vector(9,12,15);
		assertEquals("addMulInto gives unexpected result.", expectedResult,v2);
		expectedResult=new WB_Vector(1,2,3);
		assertEquals("addMulInto modifies calling WB_Vector.", expectedResult,v1);
	}

	@Test
	public void testAddMulIntoWB_MutableCoordDoubleDoubleArrayLength2() {
		v1.set(1,2,3);
		coord=new double[]{4,5};
		f=2;
		v1.addMulInto(v2,f,coord);
		expectedResult=new WB_Vector(9,12,3);
		assertEquals("addMulInto gives unexpected result.", expectedResult,v2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddMulIntoWB_MutableCoordDoubleDoubleArrayLength1() {
		v1.set(1,2,3);
		coord=new double[]{4};
		f=2;
		v1.addMulInto(v2,f,coord);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddMulIntoWB_MutableCoordDoubleDoubleArrayLength4() {
		v1.set(1,2,3);
		coord=new double[]{4,5,6,7};
		f=2;
		v1.addMulInto(v2,f,coord);
	}


	/**
	 * Test method for {@link wblut.geom.WB_Vector#addMul(double, double[])}.
	 */
	@Test
	public void testAddMulDoubleDoubleArrayLength3() {
		v1.set(1,2,3);
		coord=new double[]{4,5,6};
		f=2;
		result=v1.addMul(f,coord);
		expectedResult=new WB_Vector(9,12,15);
		assertEquals("addMul gives unexpected result.", expectedResult, result);
		expectedResult=new WB_Vector(1,2,3);
		assertEquals("addMul modifies calling WB_Vector.", expectedResult,v1);
		assertThat("addMul does not return WB_Vector.",result, instanceOf(WB_Vector.class));
	}

	@Test
	public void testAddMulDoubleDoubleArrayLength2() {
		v1.set(1,2,3);
		coord=new double[]{4,5};
		f=2;
		result=v1.addMul(f,coord);
		expectedResult=new WB_Vector(9,12,3);
		assertEquals("addMul gives unexpected result.", expectedResult, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddMulDoubleDoubleArrayLength1() {
		v1.set(1,2,3);
		coord=new double[]{4};
		f=2;
		result=v1.addMul(f,coord);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddMulDoubleDoubleArrayLength4() {
		v1.set(1,2,3);
		coord=new double[]{4,5,6,7};
		f=2;
		result=v1.addMul(f,coord);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#addMulSelf(double, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testAddMulSelfDoubleWB_Coord() {
		v1.set(1,2,3);
		v2.set(4,5,6);
		f=2;
		result=v1.addMulSelf(f,v2);
		assertSame("addMulSelf should return calling WB_Vector.", v1, result);
		expectedResult=new WB_Vector(9,12,15);
		assertEquals("addMulSelf gives unexpected result.", expectedResult, result);
		expectedResult=new WB_Vector(4,5,6);
		assertEquals("addMulSelf modifies argument WB_Vector.", expectedResult,v2);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#addMulInto(wblut.geom.WB_MutableCoord, double, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testAddMulIntoWB_MutableCoordDoubleWB_Coord() {
		v1.set(1,2,3);
		v2.set(4,5,6);
		f=2;
		result=new WB_Vector();
		v1.addMulInto(result,f,v2);
		expectedResult=new WB_Vector(9,12,15);
		assertEquals("addMulInto gives unexpected result.", expectedResult, result);
		expectedResult=new WB_Vector(1,2,3);
		assertEquals("addMulInto modifies argument WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(4,5,6);
		assertEquals("addMulInto modifies argument WB_Vector.", expectedResult,v2);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#addMul(double, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testAddMulDoubleWB_Coord() {
		v1.set(1,2,3);
		v2.set(4,5,6);
		f=2;
		result=	v1.addMul(f,v2);
		expectedResult=new WB_Vector(9,12,15);
		assertEquals("addMulInto gives unexpected result.", expectedResult, result);
		expectedResult=new WB_Vector(1,2,3);
		assertEquals("addMulInto modifies argument WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(4,5,6);
		assertEquals("addMulInto modifies argument WB_Vector.", expectedResult,v2);
		assertThat("addMul does not return WB_Vector.",result, instanceOf(WB_Vector.class));
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#addMul(wblut.geom.WB_Coord, double, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testAddMulWB_CoordDoubleWB_Coord() {
		v1.set(1,2,3);
		v2.set(4,5,6);
		f=2;
		result=	WB_Vector.addMul(v1,f,v2);
		expectedResult=new WB_Vector(9,12,15);
		assertEquals("addMulInto gives unexpected result.", expectedResult, result);
		expectedResult=new WB_Vector(1,2,3);
		assertEquals("addMulInto modifies argument WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(4,5,6);
		assertEquals("addMulInto modifies argument WB_Vector.", expectedResult,v2);
		assertThat("addMul does not return WB_Vector.",result, instanceOf(WB_Vector.class));
	}



	/**
	 * Test method for {@link wblut.geom.WB_Vector#mulAddMulSelf(double, double, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testMulAddMulSelfDoubleDoubleDoubleArrayLength3() {
		v1.set(1,2,3);
		coord=new double[]{4,5,6};
		f=2;
		g=3;
		result=v1.mulAddMulSelf(f,g,coord);
		assertSame("mulAddMulSelf should return calling WB_Vector.", v1, result);
		expectedResult=new WB_Vector(14,19,24);
		assertEquals("mulAddMulSelf gives unexpected result.", expectedResult, result);

	}

	@Test
	public void testMulAddMulSelfDoubleDoubleDoubleArrayLength2() {
		v1.set(1,2,3);
		coord=new double[]{4,5,6};
		f=2;
		g=3;
		result=v1.mulAddMulSelf(f,g,coord);
		assertSame("mulAddMulSelf should return calling WB_Vector.", v1, result);
		expectedResult=new WB_Vector(14,19,24);
		assertEquals("mulAddMulSelf gives unexpected result.", expectedResult, result);

	}
	@Test(expected = IllegalArgumentException.class)
	public void testMulAddMulSelfDoubleDoubleDoubleArrayLength1() {
		v1.set(1,2,3);
		coord=new double[]{4};
		f=2;
		g=3;
		result=v1.mulAddMulSelf(f,g,coord);
	}
	@Test(expected = IllegalArgumentException.class)
	public void testMulAddMulSelfDoubleDoubleDoubleArrayLength4() {
		v1.set(1,2,3);
		coord=new double[]{4,5,6,7};
		f=2;
		g=3;
		result=v1.mulAddMulSelf(f,g,coord);
	}


	/**
	 * Test method for {@link wblut.geom.WB_Vector#mulAddMulInto(wblut.geom.WB_MutableCoord, double, double, double[])}.
	 */
	@Test
	public void testMulAddMulIntoWB_MutableCoordDoubleDoubleDoubleArrayLength3() {
		v1.set(1,2,3);
		coord=new double[]{4,5,6};
		f=2;
		g=3;
		result=new WB_Vector();
		v1.mulAddMulInto(result,f,g,coord);
		expectedResult=new WB_Vector(14,19,24);
		assertEquals("mulAddMulInto gives unexpected result.", expectedResult, result);
		expectedResult=new WB_Vector(1,2,3);
		assertEquals("mulAddMulInto modifies calling WB_Vector.", expectedResult,v1);

	}

	@Test
	public void testMulAddMulIntoWB_MutableCoordDoubleDoubleDoubleArrayLength2() {
		v1.set(1,2,3);
		coord=new double[]{4,5};
		f=2;
		g=3;
		result=new WB_Vector();
		v1.mulAddMulInto(result,f,g,coord);
		expectedResult=new WB_Vector(14,19,3);
		assertEquals("mulAddMulInto gives unexpected result.", expectedResult, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMulAddMulIntoWB_MutableCoordDoubleDoubleDoubleArrayLength1() {
		v1.set(1,2,3);
		coord=new double[]{4};
		f=2;
		g=3;
		result=new WB_Vector();
		v1.mulAddMulInto(result,f,g,coord);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMulAddMulIntoWB_MutableCoordDoubleDoubleDoubleArrayLength4() {
		v1.set(1,2,3);
		coord=new double[]{4,5,6,7};
		f=2;
		g=3;
		result=new WB_Vector();
		v1.mulAddMulInto(result,f,g,coord);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#mulAddMul(double, double, double[])}.
	 */
	@Test
	public void testMulAddMulDoubleDoubleDoubleArrayLength3() {
		v1.set(1,2,3);
		coord=new double[]{4,5,6};
		f=2;
		g=3;
		result=v1.mulAddMul(f,g,coord);
		expectedResult=new WB_Vector(14,19,24);
		assertEquals("mulAddMul gives unexpected result.", expectedResult, result);
		expectedResult=new WB_Vector(1,2,3);
		assertEquals("mulAddMul modifies calling WB_Vector.", expectedResult,v1);
		assertThat("mulAddMul does not return WB_Vector.",result, instanceOf(WB_Vector.class));
	}

	@Test
	public void testMulAddMulDoubleDoubleDoubleArrayLength2() {
		v1.set(1,2,3);
		coord=new double[]{4,5};
		f=2;
		g=3;
		result=v1.mulAddMul(f,g,coord);
		expectedResult=new WB_Vector(14,19,3);
		assertEquals("mulAddMul gives unexpected result.", expectedResult, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMulAddMulDoubleDoubleDoubleArrayLength1() {
		v1.set(1,2,3);
		coord=new double[]{4};
		f=2;
		g=3;
		result=v1.mulAddMul(f,g,coord);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMulAddMulDoubleDoubleDoubleArrayLength4() {
		v1.set(1,2,3);
		coord=new double[]{4,5,6,7};
		f=2;
		g=3;
		result=v1.mulAddMul(f,g,coord);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#mulAddMulSelf(double, double, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testMulAddMulSelfWB_Coord() {
		v1.set(1,2,3);
		v2.set(4,5,6);
		f=2;
		g=3;
		result=v1.mulAddMulSelf(f,g,v2);
		assertSame("mulAddMulSelf should return calling WB_Vector.", v1, result);
		expectedResult=new WB_Vector(14,19,24);
		assertEquals("mulAddMulSelf gives unexpected result.", expectedResult, result);
		expectedResult=new WB_Vector(4,5,6);
		assertEquals("mulAddMulSelf modifies argument WB_Vector.", expectedResult,v2);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#mulAddMulInto(wblut.geom.WB_MutableCoord, double, double, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testMulAddMulIntoWB_MutableCoordDoubleDoubleWB_Coord() {
		v1.set(1,2,3);
		v2.set(4,5,6);
		f=2;
		g=3;
		result=new WB_Vector();
		v1.mulAddMulInto(result,f,g,v2);
		expectedResult=new WB_Vector(14,19,24);
		assertEquals("mulAddMulInto gives unexpected result.", expectedResult, result);
		expectedResult=new WB_Vector(1,2,3);
		assertEquals("mulAddMulInto modifies calling WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(4,5,6);
		assertEquals("mulAddMulInto modifies argument WB_Vector.", expectedResult,v2);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#mulAddMul(double, double, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testMulAddMulDoubleDoubleWB_Coord() {
		v1.set(1,2,3);
		v2.set(4,5,6);
		f=2;
		g=3;
		result=v1.mulAddMul(f,g,v2);
		expectedResult=new WB_Vector(14,19,24);
		assertEquals("mulAddMul gives unexpected result.", expectedResult, result);
		expectedResult=new WB_Vector(1,2,3);
		assertEquals("mulAddMul modifies calling WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(4,5,6);
		assertEquals("mulAddMul modifies argument WB_Vector.", expectedResult,v2);
		assertThat("mulAddMul does not return WB_Vector.",result, instanceOf(WB_Vector.class));
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#mulAddMul(double, wblut.geom.WB_Coord, double, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testMulAddMulDoubleWB_CoordDoubleWB_Coord() {
		v1.set(1,2,3);
		v2.set(4,5,6);
		f=2;
		g=3;
		result=WB_Vector.mulAddMul(f,v1,g,v2);
		expectedResult=new WB_Vector(14,19,24);
		assertEquals("mulAddMul gives unexpected result.", expectedResult, result);
		expectedResult=new WB_Vector(1,2,3);
		assertEquals("mulAddMul modifies calling WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(4,5,6);
		assertEquals("mulAddMul modifies argument WB_Vector.", expectedResult,v2);
		assertThat("mulAddMul does not return WB_Vector.",result, instanceOf(WB_Vector.class));
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#divSelf(double)}.
	 */
	@Test
	public void testDivSelf() {
		v1.set(1,2,3);
		f=2;
		result=v1.divSelf(f);
		assertSame("divSelf should return calling WB_Vector.", v1, result);
		expectedResult=new WB_Vector(0.5,1,1.5);
		assertEquals("divSelf gives unexpected result.", expectedResult, result);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#divInto(wblut.geom.WB_MutableCoord, double)}.
	 */
	@Test
	public void testDivInto() {
		v1.set(1,2,3);
		f=2;
		v1.divInto(v2,f);
		expectedResult=new WB_Vector(0.5,1,1.5);
		assertEquals("divInto gives unexpected result.", expectedResult,v2);
		expectedResult=new WB_Vector(1,2,3);
		assertEquals("divInto modifies calling WB_Vector.", expectedResult,v1);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#div(double)}.
	 */
	@Test
	public void testDivDouble() {
		v1.set(1,2,3);
		f=2;
		result=v1.div(f);
		expectedResult=new WB_Vector(0.5,1,1.5);
		assertEquals("div gives unexpected result.", expectedResult, result);
		expectedResult=new WB_Vector(1,2,3);
		assertEquals("div modifies calling WB_Vector.", expectedResult,v1);
		assertThat("div does not return WB_Vector.",result, instanceOf(WB_Vector.class));
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#div(wblut.geom.WB_Coord, double)}.
	 */
	@Test
	public void testDivWB_CoordDouble() {
		v1.set(1,2,3);
		f=2;
		result=WB_Vector.div(v1,f);
		expectedResult=new WB_Vector(0.5,1,1.5);
		assertEquals("div gives unexpected result.", expectedResult, result);
		expectedResult=new WB_Vector(1,2,3);
		assertEquals("div modifies calling WB_Vector.", expectedResult,v1);
		assertThat("div does not return WB_Vector.",result, instanceOf(WB_Vector.class));
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#dot(wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testDotWB_Coord() {
		v1.set(-1,2,3);
		v2.set(4,-5,6);
		res=v1.dot(v2);
		assertEquals("dot gives unexpected result.",4, res,WB_Epsilon.EPSILON);
		expectedResult=new WB_Vector(-1,2,3);
		assertEquals("dot modifies calling WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(4,-5,6);
		assertEquals("dot modifies argument WB_Vector.", expectedResult,v2);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#dot(wblut.geom.WB_Coord, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testDotWB_CoordWB_Coord() {
		v1.set(-1,2,3);
		v2.set(4,-5,6);
		res=WB_Vector.dot(v1,v2);
		assertEquals("dot gives unexpected result.", 4,res,WB_Epsilon.EPSILON);
		expectedResult=new WB_Vector(-1,2,3);
		assertEquals("dot modifies calling WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(4,-5,6);
		assertEquals("dot modifies argument WB_Vector.", expectedResult,v2);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#dot2D(wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testDot2DWB_Coord() {
		v1.set(-1,2,3);
		v2.set(4,-5,6);
		res=v1.dot2D(v2);
		assertEquals("dot gives unexpected result.", -14,res,WB_Epsilon.EPSILON);
		expectedResult=new WB_Vector(-1,2,3);
		assertEquals("dot modifies calling WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(4,-5,6);
		assertEquals("dot modifies argument WB_Vector.", expectedResult,v2);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#dot2D(wblut.geom.WB_Coord, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testDot2DWB_CoordWB_Coord() {
		v1.set(-1,2,3);
		v2.set(4,-5,6);
		res=WB_Vector.dot2D(v1,v2);
		assertEquals("dot gives unexpected result.", -14,res,WB_Epsilon.EPSILON);
		expectedResult=new WB_Vector(-1,2,3);
		assertEquals("dot modifies calling WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(4,-5,6);
		assertEquals("dot modifies argument WB_Vector.", expectedResult,v2);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#absDot(wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testAbsDotWB_Coord() {
		v1.set(1,-2,-3);
		v2.set(-4,5,-6);
		res=v1.absDot(v2);
		assertEquals("absDot gives unexpected result.", 4,res,WB_Epsilon.EPSILON);
		expectedResult=new WB_Vector(1,-2,-3);
		assertEquals("absDot modifies calling WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(-4,5,-6);
		assertEquals("absDot modifies argument WB_Vector.", expectedResult,v2);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#absDot(wblut.geom.WB_Coord, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testAbsDotWB_CoordWB_Coord() {
		v1.set(1,-2,-3);
		v2.set(-4,5,-6);
		res=WB_Vector.absDot(v1,v2);
		assertEquals("absDot gives unexpected result.",4, res,WB_Epsilon.EPSILON);
		expectedResult=new WB_Vector(1,-2,-3);
		assertEquals("absDot modifies argument WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(-4,5,-6);
		assertEquals("absDot modifies argument WB_Vector.", expectedResult,v2);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#absDot2D(wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testAbsDot2DWB_Coord() {
		v1.set(1,-2,3);
		v2.set(4,5,6);
		res=v1.absDot2D(v2);
		assertEquals("absDot2D gives unexpected result.", 6,res,WB_Epsilon.EPSILON);
		expectedResult=new WB_Vector(1,-2,3);
		assertEquals("absDot2D modifies calling WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(4,5,6);
		assertEquals("absDot2D modifies argument WB_Vector.", expectedResult,v2);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#absDot2D(wblut.geom.WB_Coord, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testAbsDot2DWB_CoordWB_Coord() {
		v1.set(1,-2,3);
		v2.set(4,5,6);
		res=WB_Vector.absDot2D(v1,v2);
		assertEquals("absDot2D gives unexpected result.", 6,res,WB_Epsilon.EPSILON);
		expectedResult=new WB_Vector(1,-2,3);
		assertEquals("absDot2D modifies argument WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(4,5,6);
		assertEquals("absDot2D modifies argument WB_Vector.", expectedResult,v2);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#crossSelf(wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testCrossSelf() {
		v1.set(1,2,3);
		v2.set(4,5,6);
		result=v1.crossSelf(v2);
		assertSame("crossSelf should return calling WB_Vector.", v1, result);
		expectedResult=new WB_Vector(-3,6,-3);
		assertEquals("crossSelf gives unexpected result.", expectedResult, result);
		expectedResult=new WB_Vector(4,5,6);
		assertEquals("crossSelf modifies argument WB_Vector.", expectedResult,v2);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#crossInto(wblut.geom.WB_MutableCoord, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testCrossInto() {
		v1.set(1,2,3);
		v2.set(4,5,6);
		result=new WB_Vector();
		v1.crossInto(result,v2);
		expectedResult=new WB_Vector(-3,6,-3);
		assertEquals("crossSelf gives unexpected result.", expectedResult, result);
		expectedResult=new WB_Vector(1,2,3);
		assertEquals("crossSelf modifies calling WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(4,5,6);
		assertEquals("crossSelf modifies argument WB_Vector.", expectedResult,v2);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#cross(wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testCrossWB_Coord() {
		v1.set(1,2,3);
		v2.set(4,5,6);
		result=v1.cross(v2);
		expectedResult=new WB_Vector(-3,6,-3);
		assertEquals("cross gives unexpected result.", expectedResult, result);
		expectedResult=new WB_Vector(1,2,3);
		assertEquals("cross modifies calling WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(4,5,6);
		assertEquals("cross modifies argument WB_Vector.", expectedResult,v2);
		assertThat("cross does not return WB_Vector.",result, instanceOf(WB_Vector.class));
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#cross(wblut.geom.WB_Coord, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testCrossWB_CoordWB_Coord() {
		v1.set(1,2,3);
		v2.set(4,5,6);
		result=WB_Vector.cross(v1,v2);
		expectedResult=new WB_Vector(-3,6,-3);
		assertEquals("cross gives unexpected result.", expectedResult, result);
		expectedResult=new WB_Vector(1,2,3);
		assertEquals("cross modifies argument WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(4,5,6);
		assertEquals("cross modifies argument WB_Vector.", expectedResult,v2);
		assertThat("cross does not return WB_Vector.",result, instanceOf(WB_Vector.class));
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#scalarTriple(wblut.geom.WB_Coord, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testScalarTripleWB_CoordWB_Coord() {
		v1.set(1,0,0);
		v2.set(0,2,0);
		v3.set(0,0,3);
		res=v1.scalarTriple(v2,v3);
		assertEquals("scalar triple gives unexpected result.", 6.0,res,WB_Epsilon.EPSILON);
		res=v2.scalarTriple(v3,v1);
		assertEquals("scalar triple gives unexpected result.", 6.0, res,WB_Epsilon.EPSILON);
		res=v3.scalarTriple(v1,v2);
		assertEquals("scalar triple gives unexpected result.", 6.0, res,WB_Epsilon.EPSILON);
		res=v1.scalarTriple(v3,v2);
		assertEquals("scalar triple gives unexpected result.",  -6.0, res,WB_Epsilon.EPSILON);
		res=v1.scalarTriple(v1,v2);
		assertEquals("scalar triple gives unexpected result.",  0.0, res,WB_Epsilon.EPSILON);
		expectedResult=new WB_Vector(1,0,0);
		assertEquals("scalar triple modifies argument WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(0,2,0);
		assertEquals("scalar triple modifies argument WB_Vector.", expectedResult,v2);
		expectedResult=new WB_Vector(0,0,3);
		assertEquals("scalar triple modifies argument WB_Vector.", expectedResult,v3);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#scalarTriple(wblut.geom.WB_Coord, wblut.geom.WB_Coord, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testScalarTripleWB_CoordWB_CoordWB_Coord() {
		v1.set(1,0,0);
		v2.set(0,2,0);
		v3.set(0,0,3);
		res=WB_Vector.scalarTriple(v1,v2,v3);
		assertEquals("scalar triple gives unexpected result.",  6.0, res,WB_Epsilon.EPSILON);
		res=WB_Vector.scalarTriple(v2,v3,v1);
		assertEquals("scalar triple gives unexpected result.",  6.0, res,WB_Epsilon.EPSILON);
		res=WB_Vector.scalarTriple(v3,v1,v2);
		assertEquals("scalar triple gives unexpected result.", 6.0, res,WB_Epsilon.EPSILON);
		res=WB_Vector.scalarTriple(v1,v3,v2);
		assertEquals("scalar triple gives unexpected result.",  -6.0, res,WB_Epsilon.EPSILON);
		res=v1.scalarTriple(v1,v2);
		assertEquals("scalar triple gives unexpected result.",  0.0, res,WB_Epsilon.EPSILON);
		expectedResult=new WB_Vector(1,0,0);
		assertEquals("scalar triple modifies argument WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(0,2,0);
		assertEquals("scalar triple modifies argument WB_Vector.", expectedResult,v2);
		expectedResult=new WB_Vector(0,0,3);
		assertEquals("scalar triple modifies argument WB_Vector.", expectedResult,v3);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#tensor(wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testTensorWB_Coord() {
		v1.set(1,2,3);
		v2.set(4,5,6);
		WB_M33 tensor=v1.tensor(v2);
		WB_M33 expectedTensor=new WB_M33(4,5,6,8,10,12,12,15,18);
		assertEquals("tensor gives unexpected result.",  expectedTensor,tensor);
		expectedResult=new WB_Vector(1,2,3);
		assertEquals("tensor modifies argument WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(4,5,6);
		assertEquals("tensor modifies argument WB_Vector.", expectedResult,v2);

	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#tensor(wblut.geom.WB_Coord, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testTensorWB_CoordWB_Coord() {
		v1.set(1,2,3);
		v2.set(4,5,6);
		WB_M33 tensor=WB_Vector.tensor(v1,v2);
		WB_M33 expectedTensor=new WB_M33(4,5,6,8,10,12,12,15,18);
		assertEquals("tensor gives unexpected result.",  expectedTensor,tensor);
		expectedResult=new WB_Vector(1,2,3);
		assertEquals("tensor modifies argument WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(4,5,6);
		assertEquals("tensor modifies argument WB_Vector.", expectedResult,v2);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#getAngle(wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testGetAngleWB_Coord() {
		v1.set(2,0,0);
		for(int i=0;i<1000;i++){
			v2.set(3*Math.cos(Math.PI*i*0.001),3*Math.sin(Math.PI*i*0.001),0);
			res=WB_Vector.getAngle(v1,v2);
			assertEquals("getAngle gives unexpected result.",  Math.PI*i*0.001,res, WB_Epsilon.EPSILON);
			assertEquals("getAngle gives unexpected result.", res,v2.getAngle(v1), WB_Epsilon.EPSILON);
		}
		for(int i=0;i<1000;i++){
			v2.set(3*Math.cos(Math.PI*i*0.001),0,3*Math.sin(Math.PI*i*0.001));
			assertEquals("getAngle gives unexpected result.",  Math.PI*i*0.001,v1.getAngle(v2), WB_Epsilon.EPSILON);
		}
		expectedResult=new WB_Vector(2,0,0);
		assertEquals("getAngle modifies argument WB_Vector.", expectedResult,v1);
		v2.set(3*Math.cos(Math.PI*0.5),3*Math.sin(Math.PI*0.5),0);
		expectedResult=new WB_Vector(v2);
		v2.getAngle(v1);
		assertEquals("getAngle modifies argument WB_Vector.", expectedResult,v2);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#getAngle(wblut.geom.WB_Coord, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testGetAngleWB_CoordWB_Coord() {
		v1.set(2,0,0);
		for(int i=0;i<1000;i++){
			v2.set(3*Math.cos(Math.PI*i*0.001),3*Math.sin(Math.PI*i*0.001),0);
			res=WB_Vector.getAngle(v1,v2);
			assertEquals("getAngle gives unexpected result.",  Math.PI*i*0.001,res, WB_Epsilon.EPSILON);
			assertEquals("getAngle gives unexpected result.",  res,WB_Vector.getAngle(v2,v1), WB_Epsilon.EPSILON);
		}
		for(int i=0;i<1000;i++){
			v2.set(3*Math.cos(Math.PI*i*0.001),0,3*Math.sin(Math.PI*i*0.001));
			assertEquals("getAngle gives unexpected result.",  Math.PI*i*0.001,WB_Vector.getAngle(v1,v2), WB_Epsilon.EPSILON);
		}
		expectedResult=new WB_Vector(2,0,0);
		assertEquals("getAngle modifies argument WB_Vector.", expectedResult,v1);
		v2.set(3*Math.cos(Math.PI*0.5),3*Math.sin(Math.PI*0.5),0);
		expectedResult=new WB_Vector(v2);
		WB_Vector.getAngle(v2,v1);
		assertEquals("getAngle modifies argument WB_Vector.", expectedResult,v2);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#getAngleNorm(wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testGetAngleNormWB_Coord() {
		v1.set(1,0,0);
		for(int i=0;i<1000;i++){
			v2.set(Math.cos(Math.PI*i*0.001),Math.sin(Math.PI*i*0.001),0);
			res=WB_Vector.getAngleNorm(v1,v2);
			assertEquals("getAngleNorm gives unexpected result.",  Math.PI*i*0.001,res, WB_Epsilon.EPSILON);
			assertEquals("getAngleNorm gives unexpected result.", res,v2.getAngleNorm(v1), WB_Epsilon.EPSILON);
		}
		for(int i=0;i<1000;i++){
			v2.set(Math.cos(Math.PI*i*0.001),0,Math.sin(Math.PI*i*0.001));
			assertEquals("getAngleNorm gives unexpected result.",  Math.PI*i*0.001,v1.getAngleNorm(v2), WB_Epsilon.EPSILON);
		}
		expectedResult=new WB_Vector(1,0,0);
		assertEquals("getAngleNorm modifies argument WB_Vector.", expectedResult,v1);
		v2.set(Math.cos(Math.PI*0.5),Math.sin(Math.PI*0.5),0);
		expectedResult=new WB_Vector(v2);
		v2.getAngleNorm(v1);
		assertEquals("getAngleNorm modifies argument WB_Vector.", expectedResult,v2);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#getAngleNorm(wblut.geom.WB_Coord, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testGetAngleNormWB_CoordWB_Coord() {
		v1.set(1,0,0);
		for(int i=0;i<1000;i++){
			v2.set(Math.cos(Math.PI*i*0.001),Math.sin(Math.PI*i*0.001),0);
			res=WB_Vector.getAngleNorm(v1,v2);
			assertEquals("getAngleNorm gives unexpected result.",  Math.PI*i*0.001,res, WB_Epsilon.EPSILON);
			assertEquals("getAngleNorm gives unexpected result.",  res,WB_Vector.getAngleNorm(v2,v1), WB_Epsilon.EPSILON);
		}
		for(int i=0;i<1000;i++){
			v2.set(Math.cos(Math.PI*i*0.001),0,Math.sin(Math.PI*i*0.001));
			assertEquals("getAngleNorm gives unexpected result.",  Math.PI*i*0.001,WB_Vector.getAngleNorm(v1,v2), WB_Epsilon.EPSILON);
		}
		expectedResult=new WB_Vector(1,0,0);
		assertEquals("getAngleNorm modifies argument WB_Vector.", expectedResult,v1);
		v2.set(Math.cos(Math.PI*0.5),Math.sin(Math.PI*0.5),0);
		expectedResult=new WB_Vector(v2);
		WB_Vector.getAngleNorm(v2,v1);
		assertEquals("getAngleNorm modifies argument WB_Vector.", expectedResult,v2);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#getDistance(wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testGetDistance3DWB_Coord() {
		v1.set(1,0,-2);
		v2.set(1,3,2);
		assertEquals("getDistance3D gives unexpected result.", 5.0,v1.getDistance(v2), WB_Epsilon.EPSILON);
		expectedResult=new WB_Vector(1,0,-2);
		assertEquals("getDistance3D modifies argument WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(1,3,2);
		assertEquals("getDistance3D modifies argument WB_Vector.", expectedResult,v2);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#getDistance3D(wblut.geom.WB_Coord, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testGetDistance3DWB_CoordWB_Coord() {
		v1.set(1,0,-2);
		v2.set(1,3,2);
		assertEquals("getDistance3D gives unexpected result.", 5.0,WB_Vector.getDistance3D(v1,v2), WB_Epsilon.EPSILON);
		expectedResult=new WB_Vector(1,0,-2);
		assertEquals("getDistance3D modifies argument WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(1,3,2);
		assertEquals("getDistance3D modifies argument WB_Vector.", expectedResult,v2);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#getDistance2D(wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testGetDistance2DWB_Coord() {
		v1.set(1,0,-2);
		v2.set(1,3,2);
		assertEquals("getDistance2D gives unexpected result.",3.0,v1.getDistance2D(v2), WB_Epsilon.EPSILON);
		expectedResult=new WB_Vector(1,0,-2);
		assertEquals("getDistance2D modifies argument WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(1,3,2);
		assertEquals("getDistance2D modifies argument WB_Vector.", expectedResult,v2);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#getDistance2D(wblut.geom.WB_Coord, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testGetDistance2DWB_CoordWB_Coord() {
		v1.set(1,0,-2);
		v2.set(1,3,2);
		assertEquals("getDistance2D gives unexpected result.", 3.0,WB_Vector.getDistance2D(v1,v2), WB_Epsilon.EPSILON);
		expectedResult=new WB_Vector(1,0,-2);
		assertEquals("getDistance2D modifies argument WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(1,3,2);
		assertEquals("getDistance2D modifies argument WB_Vector.", expectedResult,v2);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#getLength()}.
	 */
	@Test
	public void testGetLength3D() {
		v1.set(3,0,-4);
		assertEquals("getLength3D gives unexpected result.", 5.0,v1.getLength(), WB_Epsilon.EPSILON);
		expectedResult=new WB_Vector(3,0,-4);
		assertEquals("getLength3D modifies argument WB_Vector.", expectedResult,v1);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#getLength3D(wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testGetLength3DWB_Coord() {
		v1.set(3,0,-4);
		assertEquals("getLength3D gives unexpected result.", 5.0,WB_Vector.getLength3D(v1), WB_Epsilon.EPSILON);
		expectedResult=new WB_Vector(3,0,-4);
		assertEquals("getLength3D modifies argument WB_Vector.", expectedResult,v1);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#getLength2D()}.
	 */
	@Test
	public void testGetLength2D() {
		v1.set(3,0,-4);
		assertEquals("getLength2D gives unexpected result.", 3.0,v1.getLength2D(), WB_Epsilon.EPSILON);
		expectedResult=new WB_Vector(3,0,-4);
		assertEquals("getLength2D modifies argument WB_Vector.", expectedResult,v1);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#getLength2D(wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testGetLength2DWB_Coord() {
		v1.set(3,0,-4);
		assertEquals("getLength2D gives unexpected result.", 3.0,WB_Vector.getLength2D(v1), WB_Epsilon.EPSILON);
		expectedResult=new WB_Vector(3,0,-4);
		assertEquals("getLength2D modifies argument WB_Vector.", expectedResult,v1);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#getSqDistance(wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testGetSqDistance3DWB_Coord() {
		v1.set(1,0,-2);
		v2.set(1,3,2);
		assertEquals("getSqDistance3D gives unexpected result.", 25.0,v1.getSqDistance(v2), WB_Epsilon.EPSILON);
		expectedResult=new WB_Vector(1,0,-2);
		assertEquals("getSqDistance3D modifies argument WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(1,3,2);
		assertEquals("getSqDistance3D modifies argument WB_Vector.", expectedResult,v2);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#getSqDistance3D(wblut.geom.WB_Coord, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testGetSqDistance3DWB_CoordWB_Coord() {
		v1.set(1,0,-2);
		v2.set(1,3,2);
		assertEquals("getSqDistance3D gives unexpected result.", 25.0,WB_Vector.getSqDistance3D(v1,v2), WB_Epsilon.EPSILON);
		expectedResult=new WB_Vector(1,0,-2);
		assertEquals("getSqDistance3D modifies argument WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(1,3,2);
		assertEquals("getSqDistance3D modifies argument WB_Vector.", expectedResult,v2);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#getSqDistance2D(wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testGetSqDistance2DWB_Coord() {
		v1.set(1,0,-2);
		v2.set(1,3,2);
		assertEquals("getSqDistance2D gives unexpected result.",9.0,v1.getSqDistance2D(v2), WB_Epsilon.EPSILON);
		expectedResult=new WB_Vector(1,0,-2);
		assertEquals("getSqDistance2D modifies argument WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(1,3,2);
		assertEquals("getSqDistance2D modifies argument WB_Vector.", expectedResult,v2);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#getSqDistance2D(wblut.geom.WB_Coord, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testGetSqDistance2DWB_CoordWB_Coord() {
		v1.set(3,0,-4);
		assertEquals("getSqLength2D gives unexpected result.", 9.0,WB_Vector.getSqLength2D(v1), WB_Epsilon.EPSILON);
		expectedResult=new WB_Vector(3,0,-4);
		assertEquals("getSqLength2D modifies argument WB_Vector.", expectedResult,v1);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#getSqLength()}.
	 */
	@Test
	public void testGetSqLength3D() {
		v1.set(3,0,-4);
		assertEquals("getSqLength3D gives unexpected result.", 25.0,v1.getSqLength(), WB_Epsilon.EPSILON);
		expectedResult=new WB_Vector(3,0,-4);
		assertEquals("getSqLength3D modifies argument WB_Vector.", expectedResult,v1);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#getSqLength3D(wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testGetSqLength3DWB_Coord() {
		v1.set(3,0,-4);
		assertEquals("getSqLength3D gives unexpected result.", 25.0,WB_Vector.getSqLength3D(v1), WB_Epsilon.EPSILON);
		expectedResult=new WB_Vector(3,0,-4);
		assertEquals("getSqLength3D modifies argument WB_Vector.", expectedResult,v1);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#getSqLength2D()}.
	 */
	@Test
	public void testGetSqLength2D() {
		v1.set(3,0,-4);
		assertEquals("getSqLength2D gives unexpected result.", 9.0,v1.getSqLength2D(), WB_Epsilon.EPSILON);
		expectedResult=new WB_Vector(3,0,-4);
		assertEquals("getSqLength2D modifies argument WB_Vector.", expectedResult,v1);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#getSqLength2D(wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testGetSqLength2DWB_Coord() {
		v1.set(3,0,-4);
		assertEquals("getSqLength2D gives unexpected result.", 9.0,WB_Vector.getSqLength2D(v1), WB_Epsilon.EPSILON);
		expectedResult=new WB_Vector(3,0,-4);
		assertEquals("getSqLength2D modifies argument WB_Vector.", expectedResult,v1);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#getHeading2D()}.
	 */
	@Test
	public void testGetHeading2D() {
		for(int i=0;i<1000;i++){
			v1.set(Math.cos(Math.PI*i*0.001),Math.sin(Math.PI*i*0.001),0);
			res=v1.getHeading2D();
			assertEquals("getHeading2D gives unexpected result.",  Math.PI*i*0.001,res, WB_Epsilon.EPSILON);
		}
		v1.set(Math.cos(Math.PI*0.5),Math.sin(Math.PI*0.5),0);
		expectedResult=new WB_Vector(v1);
		v1.getHeading2D();
		assertEquals("getHeading2D modifies argument WB_Vector.", expectedResult,v1);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#getHeading2D(wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testGetHeading2DWB_Coord() {
		for(int i=0;i<1000;i++){
			v1.set(Math.cos(Math.PI*i*0.001),Math.sin(Math.PI*i*0.001),0);
			res=WB_Vector.getHeading2D(v1);
			assertEquals("getHeading2D gives unexpected result.",  Math.PI*i*0.001,res, WB_Epsilon.EPSILON);
		}
		v1.set(Math.cos(Math.PI*0.5),Math.sin(Math.PI*0.5),0);
		expectedResult=new WB_Vector(v1);
		WB_Vector.getHeading2D(v1);
		assertEquals("getHeading2D modifies argument WB_Vector.", expectedResult,v1);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#normalizeSelf()}.
	 */
	@Test
	public void testNormalizeSelf() {
		v1.set(3,4,12);
		res=v1.normalizeSelf();
		assertEquals("normalizeSelf gives unexpected result.", res, 13,WB_Epsilon.EPSILON);
		expectedResult=new WB_Vector(3.0/13,4.0/13,12./13);
		assertEquals("normalizeSelf gives unexpected result.", expectedResult,v1);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#trimSelf(double)}.
	 */
	@Test
	public void testTrimSelf() {
		v1.set(3,3,3);
		result=v1.trimSelf(Math.sqrt(3.0));
		assertSame("trimSelf should return calling WB_Vector.", v1, result);
		expectedResult=new WB_Vector(1,1,1);
		assertEquals("trimSelf gives unexpected result.", expectedResult, result);
		v1.set(3,3,3);
		result=v1.trimSelf(6.0);
		expectedResult=new WB_Vector(3,3,3);
		assertEquals("trimSelf gives unexpected result.", expectedResult, result);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#apply(wblut.geom.WB_Transform)}.
	 */
	@Test
	public void testApply() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#applyInto(wblut.geom.WB_MutableCoord, wblut.geom.WB_Transform)}.
	 */
	@Test
	public void testApplyIntoWB_MutableCoordWB_Transform() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#applySelf(wblut.geom.WB_Transform)}.
	 */
	@Test
	public void testApplySelf() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#applyAsNormalSelf(wblut.geom.WB_Transform)}.
	 */
	@Test
	public void testApplyAsNormalSelf() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#applyAsNormalInto(wblut.geom.WB_MutableCoord, wblut.geom.WB_Transform)}.
	 */
	@Test
	public void testApplyAsNormalIntoWB_MutableCoordWB_Transform() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#applyAsNormal(wblut.geom.WB_Transform)}.
	 */
	@Test
	public void testApplyAsNormal() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#applyAsPointSelf(wblut.geom.WB_Transform)}.
	 */
	@Test
	public void testApplyAsPointSelf() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#applyAsPointInto(wblut.geom.WB_MutableCoord, wblut.geom.WB_Transform)}.
	 */
	@Test
	public void testApplyAsPointIntoWB_MutableCoordWB_Transform() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#applyAsPoint(wblut.geom.WB_Transform)}.
	 */
	@Test
	public void testApplyAsPoint() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#applyAsVectorSelf(wblut.geom.WB_Transform)}.
	 */
	@Test
	public void testApplyAsVectorSelf() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#applyAsVectorInto(wblut.geom.WB_MutableCoord, wblut.geom.WB_Transform)}.
	 */
	@Test
	public void testApplyAsVectorIntoWB_MutableCoordWB_Transform() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#applyAsVector(wblut.geom.WB_Transform)}.
	 */
	@Test
	public void testApplyAsVector() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#rotateAboutAxis2PSelf(double, double, double, double, double, double, double)}.
	 */
	@Test
	public void testRotateAboutAxis2PSelfDoubleDoubleDoubleDoubleDoubleDoubleDouble() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#rotateAboutAxis2PSelf(double, wblut.geom.WB_Coord, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testRotateAboutAxis2PSelfDoubleWB_CoordWB_Coord() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#rotateAboutAxisSelf(double, wblut.geom.WB_Coord, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testRotateAboutAxisSelfDoubleWB_CoordWB_Coord() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#rotateAboutAxisSelf(double, double, double, double, double, double, double)}.
	 */
	@Test
	public void testRotateAboutAxisSelfDoubleDoubleDoubleDoubleDoubleDoubleDouble() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#rotateAboutAxis2P(double, double, double, double, double, double, double)}.
	 */
	@Test
	public void testRotateAboutAxis2PDoubleDoubleDoubleDoubleDoubleDoubleDouble() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#rotateAboutAxis2P(double, wblut.geom.WB_Coord, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testRotateAboutAxis2PDoubleWB_CoordWB_Coord() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#rotateAboutAxis(double, double, double, double, double, double, double)}.
	 */
	@Test
	public void testRotateAboutAxisDoubleDoubleDoubleDoubleDoubleDoubleDouble() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#rotateAboutAxis(double, wblut.geom.WB_Coord, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testRotateAboutAxisDoubleWB_CoordWB_Coord() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#rotateAboutOrigin(double, double, double, double)}.
	 */
	@Test
	public void testRotateAboutOriginDoubleDoubleDoubleDouble() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#rotateAboutOrigin(double, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testRotateAboutOriginDoubleWB_Coord() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#rotateAboutOriginSelf(double, double, double, double)}.
	 */
	@Test
	public void testRotateAboutOriginSelfDoubleDoubleDoubleDouble() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#rotateAboutOriginSelf(double, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testRotateAboutOriginSelfDoubleWB_Coord() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#getOrthoNormal2D()}.
	 */
	@Test
	public void testGetOrthoNormal2D() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#getOrthoNormal2D(wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testGetOrthoNormal2DWB_Coord() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#getOrthoNormal()}.
	 */
	@Test
	public void testGetOrthoNormal3D() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#getOrthoNormal3D(wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testGetOrthoNormal3DWB_Coord() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#subToVector3D(wblut.geom.WB_Coord, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testSubToVector3D() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#subToVector2D(wblut.geom.WB_Coord, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testSubToVector2D() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#isCollinear(wblut.geom.WB_Coord, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testIsCollinearWB_CoordWB_Coord() {
		v1.set(1,-2,3);
		v2.set(3,1,7);
		v3.set(2,-0.5,5);
		assertTrue("isCollinear gives unexpected result.",v1.isCollinear(v2,v3));
		assertTrue("isCollinear gives unexpected result.",v2.isCollinear(v1,v3));
		assertTrue("isCollinear gives unexpected result.",v3.isCollinear(v1,v2));
		assertTrue("isCollinear gives unexpected result.",v1.isCollinear(v2,v2));
		assertTrue("isCollinear gives unexpected result.",v1.isCollinear(v1,v1));
		v3.set(2,-0.5+(2.0*WB_Epsilon.EPSILON),5);
		assertFalse("isCollinear gives unexpected result.",v1.isCollinear(v2,v3));
		assertFalse("isCollinear gives unexpected result.",v2.isCollinear(v1,v3));
		assertFalse("isCollinear gives unexpected result.",v3.isCollinear(v1,v2));
		expectedResult=new WB_Vector(1,-2,3);
		assertEquals("isCollinear modifies argument WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(3,1,7);
		assertEquals("isCollinear modifies argument WB_Vector.", expectedResult,v2);
		v3.set(2,-0.5,5);
		v1.isCollinear(v2,v3);
		expectedResult=new WB_Vector(2,-0.5,5);
		assertEquals("isCollinear modifies argument WB_Vector.", expectedResult,v3);
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#isCollinear(wblut.geom.WB_Coord, wblut.geom.WB_Coord, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testIsCollinearWB_CoordWB_CoordWB_Coord() {
		v1.set(1,-2,3);
		v2.set(3,1,7);
		v3.set(2,-0.5,5);
		assertTrue("isCollinear gives unexpected result.",WB_Vector.isCollinear(v1,v2,v3));
		assertTrue("isCollinear gives unexpected result.",WB_Vector.isCollinear(v2,v1,v3));
		assertTrue("isCollinear gives unexpected result.",WB_Vector.isCollinear(v3,v1,v2));
		assertTrue("isCollinear gives unexpected result.",WB_Vector.isCollinear(v1,v2,v2));
		assertTrue("isCollinear gives unexpected result.",WB_Vector.isCollinear(v1,v1,v1));
		v3.set(2,-0.5+(2.0*WB_Epsilon.EPSILON),5);
		assertFalse("isCollinear gives unexpected result.",WB_Vector.isCollinear(v1,v2,v3));
		assertFalse("isCollinear gives unexpected result.",WB_Vector.isCollinear(v2,v1,v3));
		assertFalse("isCollinear gives unexpected result.",WB_Vector.isCollinear(v3,v1,v2));
		expectedResult=new WB_Vector(1,-2,3);
		assertEquals("isCollinear modifies argument WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(3,1,7);
		assertEquals("isCollinear modifies argument WB_Vector.", expectedResult,v2);
		v3.set(2,-0.5,5);
		v1.isCollinear(v2,v3);
		expectedResult=new WB_Vector(2,-0.5,5);
		assertEquals("isCollinear modifies argument WB_Vector.", expectedResult,v3);
	}

	@Test
	public void testIsCollinear2DWB_CoordWB_Coord() {
		v1.set(1,-2,3);
		v2.set(3,1,27);
		v3.set(2,-0.5,5);
		assertTrue("isCollinear2D gives unexpected result.",v1.isCollinear2D(v2,v3));
		assertTrue("isCollinear2D gives unexpected result.",v2.isCollinear2D(v1,v3));
		assertTrue("isCollinear2D gives unexpected result.",v3.isCollinear2D(v1,v2));
		assertTrue("isCollinear2D gives unexpected result.",v1.isCollinear2D(v2,v2));
		assertTrue("isCollinear2D gives unexpected result.",v1.isCollinear2D(v1,v1));
		v3.set(2,-0.5+(2.0*WB_Epsilon.EPSILON),5);
		assertFalse("isCollinear2D gives unexpected result.",v1.isCollinear2D(v2,v3));
		assertFalse("isCollinear2D gives unexpected result.",v2.isCollinear2D(v1,v3));
		assertFalse("isCollinear2D gives unexpected result.",v3.isCollinear2D(v1,v2));
		expectedResult=new WB_Vector(1,-2,3);
		assertEquals("isCollinear2D modifies argument WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(3,1,27);
		assertEquals("isCollinear2D modifies argument WB_Vector.", expectedResult,v2);
		v3.set(2,-0.5,5);
		v1.isCollinear2D(v2,v3);
		expectedResult=new WB_Vector(2,-0.5,5);
		assertEquals("isCollinear2D modifies argument WB_Vector.", expectedResult,v3);
	}

	@Test
	public void testIsCollinear2DWB_CoordWB_CoordWB_Coord() {
		v1.set(1,-2,3);
		v2.set(3,1,27);
		v3.set(2,-0.5,5);
		assertTrue("isCollinear2D gives unexpected result.",WB_Vector.isCollinear2D(v1,v2,v3));
		assertTrue("isCollinear2D gives unexpected result.",WB_Vector.isCollinear2D(v2,v1,v3));
		assertTrue("isCollinear2D gives unexpected result.",WB_Vector.isCollinear2D(v3,v1,v2));
		assertTrue("isCollinear2D gives unexpected result.",WB_Vector.isCollinear2D(v1,v2,v2));
		assertTrue("isCollinear2D gives unexpected result.",WB_Vector.isCollinear2D(v1,v1,v1));
		v3.set(2,-0.5+(2.0*WB_Epsilon.EPSILON),5);
		assertFalse("isCollinear2D gives unexpected result.",WB_Vector.isCollinear2D(v1,v2,v3));
		assertFalse("isCollinear2D gives unexpected result.",WB_Vector.isCollinear2D(v2,v1,v3));
		assertFalse("isCollinear2D gives unexpected result.",WB_Vector.isCollinear2D(v3,v1,v2));
		expectedResult=new WB_Vector(1,-2,3);
		assertEquals("isCollinear2D modifies argument WB_Vector.", expectedResult,v1);
		expectedResult=new WB_Vector(3,1,27);
		assertEquals("isCollinear2D modifies argument WB_Vector.", expectedResult,v2);
		v3.set(2,-0.5,5);
		v1.isCollinear2D(v2,v3);
		expectedResult=new WB_Vector(2,-0.5,5);
		assertEquals("isCollinear2D modifies argument WB_Vector.", expectedResult,v3);
	}



	/**
	 * Test method for {@link wblut.geom.WB_Vector#isParallel(wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testIsParallelWB_Coord() {

	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#isParallel(wblut.geom.WB_Coord, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testIsParallelWB_CoordWB_Coord() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#isParallel(wblut.geom.WB_Coord, double)}.
	 */
	@Test
	public void testIsParallelWB_CoordDouble() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#isParallel(wblut.geom.WB_Coord, wblut.geom.WB_Coord, double)}.
	 */
	@Test
	public void testIsParallelWB_CoordWB_CoordDouble() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#isParallelNorm(wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testIsParallelNormWB_Coord() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#isParallelNorm(wblut.geom.WB_Coord, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testIsParallelNormWB_CoordWB_Coord() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#isParallelNorm(wblut.geom.WB_Coord, double)}.
	 */
	@Test
	public void testIsParallelNormWB_CoordDouble() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#isParallelNorm(wblut.geom.WB_Coord, wblut.geom.WB_Coord, double)}.
	 */
	@Test
	public void testIsParallelNormWB_CoordWB_CoordDouble() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#isParallel2D(wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testIsParallel2DWB_Coord() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#isParallel2D(wblut.geom.WB_Coord, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testIsParallel2DWB_CoordWB_Coord() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#isParallel2D(wblut.geom.WB_Coord, double)}.
	 */
	@Test
	public void testIsParallel2DWB_CoordDouble() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#isParallel2D(wblut.geom.WB_Coord, wblut.geom.WB_Coord, double)}.
	 */
	@Test
	public void testIsParallel2DWB_CoordWB_CoordDouble() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#isParallelNorm2D(wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testIsParallelNorm2DWB_Coord() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#isParallelNorm2D(wblut.geom.WB_Coord, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testIsParallelNorm2DWB_CoordWB_Coord() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#isParallelNorm2D(wblut.geom.WB_Coord, double)}.
	 */
	@Test
	public void testIsParallelNorm2DWB_CoordDouble() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#isParallelNorm2D(wblut.geom.WB_Coord, wblut.geom.WB_Coord, double)}.
	 */
	@Test
	public void testIsParallelNorm2DWB_CoordWB_CoordDouble() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#isOrthogonal(wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testIsOrthogonalWB_Coord() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#isOrthogonal(wblut.geom.WB_Coord, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testIsOrthogonalWB_CoordWB_Coord() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#isOrthogonal(wblut.geom.WB_Coord, double)}.
	 */
	@Test
	public void testIsOrthogonalWB_CoordDouble() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#isOrthogonal(wblut.geom.WB_Coord, wblut.geom.WB_Coord, double)}.
	 */
	@Test
	public void testIsOrthogonalWB_CoordWB_CoordDouble() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#isOrthogonalNorm(wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testIsOrthogonalNormWB_Coord() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#isOrthogonalNorm(wblut.geom.WB_Coord, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testIsOrthogonalNormWB_CoordWB_Coord() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#isOrthogonalNorm(wblut.geom.WB_Coord, double)}.
	 */
	@Test
	public void testIsOrthogonalNormWB_CoordDouble() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#isOrthogonalNorm(wblut.geom.WB_Coord, wblut.geom.WB_Coord, double)}.
	 */
	@Test
	public void testIsOrthogonalNormWB_CoordWB_CoordDouble() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#isOrthogonal2D(wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testIsOrthogonal2DWB_Coord() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#isOrthogonal2D(wblut.geom.WB_Coord, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testIsOrthogonal2DWB_CoordWB_Coord() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#isOrthogonal2D(wblut.geom.WB_Coord, double)}.
	 */
	@Test
	public void testIsOrthogonal2DWB_CoordDouble() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#isOrthogonal2D(wblut.geom.WB_Coord, wblut.geom.WB_Coord, double)}.
	 */
	@Test
	public void testIsOrthogonal2DWB_CoordWB_CoordDouble() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#isOrthogonalNorm2D(wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testIsOrthogonalNorm2DWB_Coord() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#isOrthogonalNorm2D(wblut.geom.WB_Coord, wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testIsOrthogonalNorm2DWB_CoordWB_Coord() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#isOrthogonalNorm2D(wblut.geom.WB_Coord, double)}.
	 */
	@Test
	public void testIsOrthogonalNorm2DWB_CoordDouble() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#isOrthogonalNorm2D(wblut.geom.WB_Coord, wblut.geom.WB_Coord, double)}.
	 */
	@Test
	public void testIsOrthogonalNorm2DWB_CoordWB_CoordDouble() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#isZero()}.
	 */
	@Test
	public void testIsZero() {
		v1=new WB_Vector(0,0,0);
		assertTrue("isZero gives unexpected result.",v1.isZero());
		v1=new WB_Vector(WB_Epsilon.EPSILON,0,0);
		assertFalse("isZero gives unexpected result.",v1.isZero());
	}

	/**
	 * Test method for {@link wblut.geom.WB_Vector#smallerThan(wblut.geom.WB_Coord)}.
	 */
	@Test
	public void testSmallerThan() {
		v1=new WB_Vector(0,0,0);
		v2=new WB_Vector(WB_Epsilon.EPSILON,0,0);
		assertTrue("smallerThan gives unexpected result.",v1.smallerThan(v2));

	}

}
