package org.eclipse.core.tests.runtime;

import org.eclipse.core.runtime.*;
import junit.framework.*;

/**
 * Test cases for the Path class.
 */
public class ProgressMonitorWrapperTest extends RuntimeTest {
/**
 * Need a zero argument constructor to satisfy the test harness.
 * This constructor should not do any real work nor should it be
 * called by user code.
 */
public ProgressMonitorWrapperTest() {
	super(null);
}
public ProgressMonitorWrapperTest(String name) {
	super(name);
}
public static Test suite() {
	return new TestSuite(ProgressMonitorWrapperTest.class);
}
public void testProgressMonitorWrapper() {
	NullProgressMonitor nullMonitor = new NullProgressMonitor();
	SubProgressMonitor wrapped = new SubProgressMonitor(nullMonitor,10);
	ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(wrapped) {};
	
	assertSame("1.0",nullMonitor,wrapped.getWrappedProgressMonitor());
	assertSame("1.1",wrapped,wrapper.getWrappedProgressMonitor());
	
	assertTrue("1.2",!nullMonitor.isCanceled());
	assertTrue("1.3",!wrapped.isCanceled());
	assertTrue("1.4",!wrapper.isCanceled());
	
	nullMonitor.setCanceled(true);
	assertTrue("1.5",nullMonitor.isCanceled());
	assertTrue("1.6",wrapped.isCanceled());
	assertTrue("1.7",wrapper.isCanceled());
}
}
