package org.archicontribs.form;

import java.io.File;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * Form plugin for Archi, the Archimate modeler
 * 
 * The FormPlugin class implements static methods and properties used everywhere else in the database plugin. 
 * 
 * @author Herve Jouin
 *
 * v0.1.0 :			28/11/2016		Plug-in creation
 * v0.2.0 :			04/03/2017		Solve several bugs
 * 									Add a preference page
 * 									Add log4j support for logging
 * 									Add filter in dynamic table items generation
 * 									Change RCP methods used to be more friendly with other plugins
 * 									The keywords are now case insensitive
 * 									The tables are now sortable 
 */
public class FormPlugin extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "org.archicontribs.form";
	
	public static final String pluginVersion = "0.2.0";
	public static final String pluginName = "FormPlugin";
	public static final String pluginTitle = "Form plugin v" + pluginVersion;
	
	public static String pluginsFolder = (new File(FormPlugin.class.getProtectionDomain().getCodeSource().getLocation().getPath())).getParent();
	public static String configFilePath = pluginsFolder + File.separatorChar + FormPlugin.PLUGIN_ID+".conf";
	
	/**
	 * static instance that allow to keep information between calls of the plugin
	 */
	public static FormPlugin INSTANCE;
	
	/**
	 * PreferenceStore allowing to store the plugin configuration.
	 */
	private static IPreferenceStore preferenceStore = null;
	
	private FormLogger logger;
	
	public FormPlugin() {
        INSTANCE = this;
        preferenceStore = this.getPreferenceStore();
		preferenceStore.setDefault("progressWindow",	"showAndWait");
		preferenceStore.setDefault("loggerMode",		"disabled");
		preferenceStore.setDefault("loggerLevel",		"INFO");
		preferenceStore.setDefault("loggerFilename",	System.getProperty("user.home")+File.separator+pluginName+".log");
		preferenceStore.setDefault("loggerExpert",		"log4j.rootLogger                               = INFO, stdout, file\n"+
														"\n"+
														"log4j.appender.stdout                          = org.apache.log4j.ConsoleAppender\n"+
														"log4j.appender.stdout.Target                   = System.out\n"+
														"log4j.appender.stdout.layout                   = org.apache.log4j.PatternLayout\n"+
														"log4j.appender.stdout.layout.ConversionPattern = %d{yyyy-MM-dd HH:mm:ss} %-5p %4L:%-30.30C{1} %m%n\n"+
														"\n"+
														"log4j.appender.file                            = org.apache.log4j.FileAppender\n"+
														"log4j.appender.file.ImmediateFlush             = true\n"+
														"log4j.appender.file.Append                     = false\n"+
														"log4j.appender.file.Encoding                   = UTF-8\n"+
														"log4j.appender.file.File                       = "+(System.getProperty("user.home")+File.separator+pluginName+".log").replace("\\", "\\\\")+"\n"+
														"log4j.appender.file.layout                     = org.apache.log4j.PatternLayout\n"+
														"log4j.appender.file.layout.ConversionPattern   = %d{yyyy-MM-dd HH:mm:ss} %-5p %4L:%-30.30C{1} %m%n");
		logger = new FormLogger(FormPlugin.class);
		logger.info("Initialising "+pluginName+" plugin ...");
    }
	
	@Override
	public IPreferenceStore getPreferenceStore() {
	    if (preferenceStore == null) {
	        preferenceStore = new ScopedPreferenceStore( InstanceScope.INSTANCE, PLUGIN_ID );
	    }
	    return preferenceStore;
	}
}
