package main.diff_L1_L2.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for main.diff_L1_L2.main.diff_L1_L2.test");

		suite.addTestSuite(RemergeTest.class);

		return suite;
	}

}
