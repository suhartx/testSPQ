package es.deusto.testing.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;
import es.deusto.testing.junit.IMoney;
import es.deusto.testing.junit.Money;
import es.deusto.testing.junit.MoneyBag;
import org.junit.Before;
import org.junit.Test;

import org.junit.Rule;
import org.databene.contiperf.Required;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.junit.ContiPerfRule;
import org.databene.contiperf.report.EmptyReportModule;

//import org.apache.log4j.Logger;
//import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author      Diego López de Ipiña &lt;dipina@deusto.es&gt;
 * @version     1.0                                    
 * @since       2012-03-24          
 * <p>This program tests all the functionality of a money handling system. It operates over the Money and MoneyBag instances. All the methods in this class except testBagMultiply() are executed 5 times. Furhermore, they all must be executed within 1.2 seconds and with an average speed of 250 milliseconds. </p> 
 *
 */
 
@PerfTest(invocations = 5)
@Required(max = 1200, average = 250)
public class MoneyPerfTest {
	/**
	 * This variable represents 12 swiss francs
	 */
	private Money f12CHF;
	/**
	 * This variable represents 14 swiss francs
	 */
	private Money f14CHF;
	/**
	 * This variable represents 7 USA dollars
	 */
	private Money f7USD;
	/**
	 * This variable represents 21 USA dollars
	 */
	private Money f21USD; 
	
	public IMoney fMB1; /** This variable has any type of IMoney */
	public IMoney fMB2; /** This variable has any type of IMoney */
	
	//static Logger logger = Logger.getLogger(MoneyPerfTest.class.getName());
	final Logger logger = LoggerFactory.getLogger(MoneyPerfTest.class);
	static int iteration = 0;

	// If you use the EmptyReportModule, this is not generated
	//@Rule public ContiPerfRule rule = new ContiPerfRule(new EmptyReportModule());
	@Rule public ContiPerfRule rule = new ContiPerfRule();
	
	public static junit.framework.Test suite() {
		 return new JUnit4TestAdapter(MoneyPerfTest.class);
	}

	/**
	 * This method is executed before any JUnit test method is executed: It is annotated with the Before annotation. Since we are using ContiPerf if a method is run 100 times, only this method will be executed the first time. 
	*/
	@Before public void setUp() {
		//logger.info("Entering setUp");
		logger.info("Entering setUp: {}", iteration++);
		f12CHF= new Money(12, "CHF");
		f14CHF= new Money(14, "CHF");
		f7USD= new Money( 7, "USD");
		f21USD= new Money(21, "USD");

		fMB1= MoneyBag.create(f12CHF, f7USD);
		fMB2= MoneyBag.create(f14CHF, f21USD);
		logger.info("Leaving setUp");
	}
	
	/**
	 * This method is executed 1000 times through 20 threads. Waht it does is to create a bag of Money mixing swiss francs with USA dollars. Then it performs several operations over them . It has to run in less that 120 milliseconds and with an average response time of 30 milliseconds. 
	*/
	@Test 
    @PerfTest(invocations = 1000, threads = 20)
    @Required(max = 1200, average = 300)
	public void testBagMultiply() throws Exception {
		logger.info("Starting testBagMultiply");
		// {[12 CHF][7 USD]} *2 == {[24 CHF][14 USD]}
		IMoney expected= MoneyBag.create(new Money(24, "CHF"), new Money(14, "USD"));
		assertEquals(expected, fMB1.multiply(2)); 
		assertEquals(fMB1, fMB1.multiply(1));
		assertTrue(fMB1.multiply(0).isZero());
		Thread.sleep(0);
		logger.debug("Finishing testBagMultiply");
	}
	@Test public void testBagNegate() {
		// {[12 CHF][7 USD]} negate == {[-12 CHF][-7 USD]}
		IMoney expected= MoneyBag.create(new Money(-12, "CHF"), new Money(-7, "USD"));
		assertEquals(expected, fMB1.negate());
	}
	@Test public void testBagSimpleAdd() {
		// {[12 CHF][7 USD]} + [14 CHF] == {[26 CHF][7 USD]}
		IMoney expected= MoneyBag.create(new Money(26, "CHF"), new Money(7, "USD"));
		assertEquals(expected, fMB1.add(f14CHF));
	}
	@Test public void testBagSubtract() {
		// {[12 CHF][7 USD]} - {[14 CHF][21 USD] == {[-2 CHF][-14 USD]}
		IMoney expected= MoneyBag.create(new Money(-2, "CHF"), new Money(-14, "USD"));
		assertEquals(expected, fMB1.subtract(fMB2));
	}
	@Test public void testBagSumAdd() {
		// {[12 CHF][7 USD]} + {[14 CHF][21 USD]} == {[26 CHF][28 USD]}
		IMoney expected= MoneyBag.create(new Money(26, "CHF"), new Money(28, "USD"));
		assertEquals(expected, fMB1.add(fMB2));
	}
	@Test public void testIsZero() {
		assertTrue(fMB1.subtract(fMB1).isZero()); 
		assertTrue(MoneyBag.create(new Money (0, "CHF"), new Money (0, "USD")).isZero());
	}
	@Test public void testMixedSimpleAdd() {
		// [12 CHF] + [7 USD] == {[12 CHF][7 USD]}
		IMoney expected= MoneyBag.create(f12CHF, f7USD);
		assertEquals(expected, f12CHF.add(f7USD));
	}
	@Test public void testBagNotEquals() {
		IMoney bag= MoneyBag.create(f12CHF, f7USD);
		assertFalse(bag.equals(new Money(12, "DEM").add(f7USD)));
	}
	@Test public void testMoneyBagEquals() {
		assertTrue(!fMB1.equals(null)); 

		assertEquals(fMB1, fMB1);
		IMoney equal= MoneyBag.create(new Money(12, "CHF"), new Money(7, "USD"));
		assertTrue(fMB1.equals(equal));
		assertTrue(!fMB1.equals(f12CHF));
		assertTrue(!f12CHF.equals(fMB1));
		assertTrue(!fMB1.equals(fMB2));
	}
	@Test public void testMoneyBagHash() {
		IMoney equal= MoneyBag.create(new Money(12, "CHF"), new Money(7, "USD"));
		assertEquals(fMB1.hashCode(), equal.hashCode());
	}
	@Test public void testMoneyEquals() {
		assertTrue(!f12CHF.equals(null)); 
		Money equalMoney= new Money(12, "CHF");
		assertEquals(f12CHF, f12CHF);
		assertEquals(f12CHF, equalMoney);
		assertEquals(f12CHF.hashCode(), equalMoney.hashCode());
		assertTrue(!f12CHF.equals(f14CHF));
	}
	@Test public void zeroMoniesAreEqualRegardlessOfCurrency() {
		   Money zeroDollars= new Money(0, "USD");
		   Money zeroFrancs= new Money(0, "CHF");
				
		   assertEquals(zeroDollars, zeroFrancs);
		   assertEquals(zeroDollars.hashCode(), zeroFrancs.hashCode());
		}
	@Test public void testMoneyHash() {
		assertTrue(!f12CHF.equals(null)); 
		Money equal= new Money(12, "CHF");
		assertEquals(f12CHF.hashCode(), equal.hashCode());
	}
	@Test public void testSimplify() {
		IMoney money= MoneyBag.create(new Money(26, "CHF"), new Money(28, "CHF"));
		assertEquals(new Money(54, "CHF"), money);
	}
	@Test public void testNormalize2() {
		// {[12 CHF][7 USD]} - [12 CHF] == [7 USD]
		Money expected= new Money(7, "USD");
		assertEquals(expected, fMB1.subtract(f12CHF));
	}
	@Test public void testNormalize3() {
		// {[12 CHF][7 USD]} - {[12 CHF][3 USD]} == [4 USD]
		IMoney ms1= MoneyBag.create(new Money(12, "CHF"), new Money(3, "USD"));
		Money expected= new Money(4, "USD");
		assertEquals(expected, fMB1.subtract(ms1));
	}
	@Test public void testNormalize4() { // [12 CHF] - {[12 CHF][3 USD]} == [-3 USD]
		IMoney ms1= MoneyBag.create(new Money(12, "CHF"), new Money(3, "USD"));
		Money expected= new Money(-3, "USD");
		assertEquals(expected, f12CHF.subtract(ms1));
	}
	@Test public void testPrint() {
		assertEquals("[12 CHF]", f12CHF.toString());
	}
	@Test public void testSimpleAdd() {
		// [12 CHF] + [14 CHF] == [26 CHF]
		Money expected= new Money(26, "CHF");
		assertEquals(expected, f12CHF.add(f14CHF));
	}
	@Test public void testSimpleBagAdd() {
		// [14 CHF] + {[12 CHF][7 USD]} == {[26 CHF][7 USD]}
		IMoney expected= MoneyBag.create(new Money(26, "CHF"), new Money(7, "USD"));
		assertEquals(expected, f14CHF.add(fMB1));
	}
	@Test public void testSimpleMultiply() {
		// [14 CHF] *2 == [28 CHF]
		Money expected= new Money(28, "CHF");
		assertEquals(expected, f14CHF.multiply(2));
	}
	@Test public void testSimpleNegate() {
		// [14 CHF] negate == [-14 CHF]
		Money expected= new Money(-14, "CHF");
		assertEquals(expected, f14CHF.negate());
	}
	@Test public void testSimpleSubtract() {
		// [14 CHF] - [12 CHF] == [2 CHF]
		Money expected= new Money(2, "CHF");
		assertEquals(expected, f14CHF.subtract(f12CHF));
	}
}