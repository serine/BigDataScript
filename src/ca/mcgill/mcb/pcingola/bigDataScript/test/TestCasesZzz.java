package ca.mcgill.mcb.pcingola.bigDataScript.test;

import org.junit.Test;

/**
 * Quick test cases when creating a new feature...
 *
 * @author pcingola
 *
 */
public class TestCasesZzz extends TestCasesBase {

	@Test
	public void test34() {
		verbose = true;
		runAndCheck("test/run_34.bds", "exitStat", "0");
	}

	//	@Test
	//	public void test15_checkpoint_par_function_call() {
	//		runAndCheckpoint("test/checkpoint_15.bds", "test/checkpoint_15.chp", "ok", "true");
	//	}
	//
	//	@Test
	//	public void test16_checkpoint_recursive() {
	//		runAndCheckpoint("test/checkpoint_16.bds", "test/checkpoint_16.chp", "fn", "120");
	//	}

}
