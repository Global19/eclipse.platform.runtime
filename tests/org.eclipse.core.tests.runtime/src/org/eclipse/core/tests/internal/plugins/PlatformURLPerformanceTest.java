package org.eclipse.core.tests.internal.plugins;

import java.io.InputStream;
import java.net.*;
import junit.framework.*;
import org.eclipse.core.internal.plugins.InternalFactory;
import org.eclipse.core.internal.plugins.PluginDescriptor;
import org.eclipse.core.internal.runtime.InternalPlatform;
import org.eclipse.core.internal.runtime.Policy;
import org.eclipse.core.runtime.*;
import org.eclipse.core.tests.harness.EclipseWorkspaceTest;
/**
 */
public class PlatformURLPerformanceTest extends EclipseWorkspaceTest {
public PlatformURLPerformanceTest() {
	super(null);
}
public PlatformURLPerformanceTest(String name) {
	super(name);
}
public long doLoad(String urls, String clazz, int count) {

	// force gc at start of test
	for (int i = 0; i < 2; i++) {
		System.runFinalization();
		System.gc();
	}

	long time = 0;
	long first = 0;
	long last = 0;
	try {
		System.out.println(urls);
		URL path = new URL(urls);
		time = (new java.util.Date()).getTime();
		for (int i=0; i<count; i++) {
			if (i==1) first = (new java.util.Date()).getTime();
			URLClassLoader l = null;
			l = new URLClassLoader(new URL[] {path},null);
			Class c = l.loadClass(clazz);
		}
		last = (new java.util.Date()).getTime();
		time = last-time;
		System.out.println("Total: "+time+" ["+(last-first)+"]");
		System.out.println("Average: "+(time/count)+" ["+((last-first)/count-1)+"]");
	}
	catch(Exception e) { System.out.println(e); }
	
	return time;
}
public void platformURLCompareTest() {

	// -cp file:some.jar
	// -cp jar:file:some.jar!/
	// -cp platform:/test/some.jar!/ with file: install

	// -cp jar:http:some.jar!/
	// -cp platform:/test/some.jar!/ with http: install

	int count = 250; // # of load iterations
	String jar = "plugin_a_external.jar";
	String clazz = "org.eclipse.core.tests.internal.plugin.a.Dummy";

	IPluginRegistry registry = InternalPlatform.getPluginRegistry();

	// get descriptors
	IPluginDescriptor pd = registry.getPluginDescriptor("plugin.a");
	assertNotNull ("0.0", pd);

	URL base = ((PluginDescriptor) pd).getInstallURLInternal();
	PlatformURLPerformanceTestConnection.startup(base);

	System.out.println(clazz);
	System.out.println("Count: " + count);
	long time;

	// local loads

	String f = (new java.io.File(base.getFile())).getAbsolutePath();
	f = (f + "/").replace('\\', '/');
	String pathFile = "file:" + f + jar;
	String pathJar = "jar:" + base.toExternalForm() + jar + "!/";
	String pathEclipse = "platform:/test/" + jar + "!/";
	String pathPlugin = pd.getInstallURL().toExternalForm() + jar + "!/";

	doLoad(pathFile, clazz, count);

	doLoad(pathJar, clazz, count);

	doLoad(pathEclipse, clazz, count);

	doLoad(pathPlugin, clazz, count);

	doLoad(pathPlugin, clazz, count);

	doLoad(pathEclipse, clazz, count);

	doLoad(pathJar, clazz, count);

	doLoad(pathFile, clazz, count);
}
public void platformURLCompareTestLocal() {

	// -cp file:some.jar
	// -cp jar:file:some.jar!/
	// -cp platform:/test/some.jar!/
	// -cp platform:/plugin/.../some.jar!/

	int count = 250; // # of load iterations
	String jar = "plugin_a_external.jar";
	String clazz = "org.eclipse.core.tests.internal.plugin.a.Dummy";

	IPluginRegistry registry = InternalPlatform.getPluginRegistry();

	// get descriptors
	IPluginDescriptor pd = registry.getPluginDescriptor("plugin.a");
	assertNotNull("0.0", pd);

	URL base = ((PluginDescriptor) pd).getInstallURLInternal();
	PlatformURLPerformanceTestConnection.startup(base);

	System.out.println(clazz);
	System.out.println("Count: " + count);
	long time;

	// local loads

	String f = (new java.io.File(base.getFile())).getAbsolutePath();
	f = (f + "/").replace('\\', '/');
	String pathFile = "file:" + f + jar;
	String pathJar = "jar:" + base.toExternalForm() + jar + "!/";
	String pathEclipse = "platform:/test/" + jar + "!/";
	String pathPlugin = pd.getInstallURL().toExternalForm() + jar + "!/";

	doLoad(pathFile, clazz, count);

	doLoad(pathJar, clazz, count);

	doLoad(pathEclipse, clazz, count);

	doLoad(pathPlugin, clazz, count);

	doLoad(pathPlugin, clazz, count);

	doLoad(pathEclipse, clazz, count);

	doLoad(pathJar, clazz, count);

	doLoad(pathFile, clazz, count);
}
public void platformURLCompareTestServer() {

	// -cp jar:http:some.jar!/
	// -cp platform:/test/some.jar!/

	int count = 250; // # of load iterations
	String jar = "plugin_a_external.jar";
	String clazz = "org.eclipse.core.tests.internal.plugin.a.Dummy";
	String server = "http://vlad.torolab.ibm.com/test/";

	IPluginRegistry registry = InternalPlatform.getPluginRegistry();

	// get descriptors
	IPluginDescriptor pd = registry.getPluginDescriptor("plugin.a");
	assertNotNull("0.0", pd);

	URL base = null;
	try {
		base = new URL(server);
	} catch (Exception e) {
		System.out.println(e);
		return;
	}
	PlatformURLPerformanceTestConnection.startup(base);

	System.out.println("Test requires manual setup of " + server);
	System.out.println(clazz);
	System.out.println("Count: " + count);
	long time;

	// poke server to wake it up plus force any server caching so all tests
	// are on equal footing

	InputStream tmpis = null;
	try {
		URL tmp = new URL(server + jar);
		tmpis = tmp.openStream();
		byte[] buf = new byte[4096];
		int tmpcount = tmpis.read(buf);
		while (tmpcount != -1) {
			tmpcount = tmpis.read(buf);
		}
		tmpis.close();
		tmpis = null;
	} catch (Exception e) {
		System.out.println("Error initializing server");
		if (tmpis != null)
			try {
				tmpis.close();
			} catch (Exception e2) {
			}
		return;
	}

	// remote loads

	String pathJar = "jar:" + base.toExternalForm() + jar + "!/";
	String pathEclipse = "platform:/test/" + jar + "!/";

	doLoad(pathJar, clazz, count);

	doLoad(pathEclipse, clazz, count);

	doLoad(pathEclipse, clazz, count);

	doLoad(pathJar, clazz, count);
}
public static Test suite() {
	TestSuite suite = new TestSuite();
	suite.addTest(new PlatformURLPerformanceTest("platformURLCompareTestLocal"));
//	suite.addTest(new PlatformURLPerformanceTest("platformURLCompareTestServer"));
	return suite;
}
}
