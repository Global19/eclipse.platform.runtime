package org.eclipse.core.tests.internal.plugins;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.model.*;
import org.eclipse.core.internal.runtime.*;
import org.eclipse.core.internal.plugins.*;
import org.eclipse.core.tests.harness.*;
import java.io.*;
import java.net.URL;
import junit.framework.*;
import org.xml.sax.*;

public class BadPluginsTest extends EclipseWorkspaceTest {

public BadPluginsTest() {
	super(null);
}

public BadPluginsTest(String name) {
	super(name);
}

public static Test suite() {
	TestSuite suite = new TestSuite();
	suite.addTest(new BadPluginsTest("badElements"));
	suite.addTest(new BadPluginsTest("badAttributes"));
	return suite;
}

public void badElements() {
	String[] badElements = {
		"badTopLevelElementsTest", 
		"badPluginElementsTest", 
		"badExtensionPointElementsTest", 
		"badLibrary1ElementsTest",
		"badLibrary2ElementsTest",
		"badRequiresImportElementsTest",
		"badRequiresElementsTest",
		"badRuntimeElementsTest",
	};
	String[] errorMessages = {
		"Unknown element notAPlugin, found at the top level, ignored.",
		"Unknown element somethingBad, found within a plugin / fragment, ignored.",
		"Unknown element nameless, found within a extension-point, ignored.",
		"Unknown element notAnExport, found within a library, ignored.",
		"Unknown element badElement, found within a export, ignored.",
		"Unknown element unrecognizedElement, found within a requires, ignored.",
		"Unknown element notAnImport, found within a requires, ignored.",
		"Unknown element notALibrary, found within a runtime, ignored.",
	};

	PluginDescriptor tempPlugin = (PluginDescriptor)Platform.getPluginRegistry().getPluginDescriptor("org.eclipse.core.tests.runtime");
	try {
		for (int i = 0; i < badElements.length; i++) {
			MultiStatus problems = new MultiStatus(Platform.PI_RUNTIME, Platform.PARSE_PROBLEM, "badPluginsTestProblems", null);
			InternalFactory factory = new InternalFactory(problems);
			String pluginPath = tempPlugin.getLocation().concat("Plugin_Testing/badPluginsTest/" + badElements[i] + ".xml");
			URL pluginURLs[] = new URL[1];
			URL pURL = null;
			try {
				pURL = new URL (pluginPath);
			} catch (java.net.MalformedURLException e) {
				assertTrue("Bad URL for " + pluginPath, true);
			}
			pluginURLs[0] = pURL;
			IPluginRegistry registry = ParseHelper.doParsing (factory, pluginURLs, false);
			if (badElements[i].equals("badTopLevelElementsTest")) {
				IPluginDescriptor[] pluginDescriptors = registry.getPluginDescriptors();
				assertTrue(i + ".0 Only one plugin", pluginDescriptors.length == 0);
				if (errorMessages[i].equals("")) {
					System.out.println ("id = <no plugin>");
					System.out.println (problems.toString());
				} else
					assertTrue(i + ".1 Got the right errors", problems.toString().indexOf(errorMessages[i]) != -1);
			} else {
				IPluginDescriptor[] pluginDescriptors = registry.getPluginDescriptors();
				assertTrue(i + ".0 Only one plugin", pluginDescriptors.length == 1);
				PluginDescriptorModel plugin = (PluginDescriptorModel)pluginDescriptors[0];
				assertTrue(i + ".1 Got the right plugin", plugin.getId().equals(badElements[i]));
				if (errorMessages[i].equals("")) {
					System.out.println ("id = " + plugin.getId());
					System.out.println (problems.toString());
				} else
					assertTrue(i + ".2 Got the right errors", problems.toString().indexOf(errorMessages[i]) != -1);
			}
		}
	} catch (Exception e) {}
}

public void badAttributes() {
	String[] badAttrs = {
		"badPluginAttributesTest", 
		"badFragmentAttributesTest", 
		"badExtensionPointAttributesTest", 
		"badExtensionAttributesTest",
		"badRequiresImport1AttributesTest",
		"badRequiresImport2AttributesTest",
		"badRequiresImport3AttributesTest",
		"badLibrary1AttributesTest",
		"badLibrary2AttributesTest",
	};
	String[] errorMessages = {
		"Unknown attribute a-bad-attribute for element plugin ignored.",
		"Unknown attribute vendor-name for element fragment ignored.",
		"Unknown attribute bogusAttribute for element extension-point ignored.",
		"Unknown attribute hello for element extension ignored.",
		"Unknown attribute badImportAttr for element import ignored.",
		"notTrue is not a valid value for the attribute \"export\".   Use \"true\" or \"false\".",
		"incompatible is not a valid value for the attribute \"match\".   Use \"exact\" or \"compatible\".",
		"Unknown attribute badAttribute for element library ignored.",
		"Unknown attribute badExportAttribute for element library ignored.",
	};
	
	PluginDescriptor tempPlugin = (PluginDescriptor)Platform.getPluginRegistry().getPluginDescriptor("org.eclipse.core.tests.runtime");
	try {
		for (int i = 0; i < badAttrs.length; i++) {
			MultiStatus problems = new MultiStatus(Platform.PI_RUNTIME, Platform.PARSE_PROBLEM, "badPluginsTestProblems", null);
			InternalFactory factory = new InternalFactory(problems);
			String pluginPath = tempPlugin.getLocation().concat("Plugin_Testing/badPluginsTest/" + badAttrs[i] + ".xml");
			URL pluginURLs[] = new URL[1];
			URL pURL = null;
			try {
				pURL = new URL (pluginPath);
			} catch (java.net.MalformedURLException e) {
				assertTrue("Bad URL for " + pluginPath, true);
			}
			pluginURLs[0] = pURL;
			IPluginRegistry registry = ParseHelper.doParsing (factory, pluginURLs, false);
			String id = null;
			if (badAttrs[i].equals("badFragmentAttributesTest")) {
				PluginFragmentModel[] fragmentDescriptors = ((PluginRegistryModel)registry).getFragments();
				assertTrue(i + ".0 Only one fragment", fragmentDescriptors.length == 1);
				PluginFragmentModel fragment = (PluginFragmentModel)fragmentDescriptors[0];
				id = fragment.getId();
				assertTrue(i + ".1 Got the right fragment", id.equals(badAttrs[i]));
			} else {
				IPluginDescriptor[] pluginDescriptors = registry.getPluginDescriptors();
				assertTrue(i + ".0 Only one plugin", pluginDescriptors.length == 1);
				PluginDescriptorModel plugin = (PluginDescriptorModel)pluginDescriptors[0];
				id = plugin.getId();
				assertTrue(i + ".1 Got the right plugin", id.equals(badAttrs[i]));
			}
			if (errorMessages[i].equals("")) {
				System.out.println("id = " + id);
				System.out.println(problems.toString());
			} else {
				assertTrue(i + ".2 Got the right errors", problems.toString().indexOf(errorMessages[i]) != -1);
			}
		}
	} catch (Exception e) {}
}

}

