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
 * v0.1 :		28/11/2016		Plug-in creation
 * 
 * v0.2 :		04/03/2017		Solve several bugs
 * 								Add a preference page
 * 								Add log4j support for logging
 * 								Add filter in dynamic table items generation
 * 								Change RCP methods to insert entries in menus in order to be more friendly with other plugins
 * 								The keywords are now case insensitive
 * 								Add ability to sort table columns
 * 
 * v1.0 :		16/04/2017		Add ability to change foreground and background color of all components
 * 								Add ability to export to Excel files
 * 								Update dynamic tables filter to add "AND" and "OR" genre
 * 
 * v1.1 :		21/05/2017		The plugin now uses Eclipse Commands to allow undo / redo
 * 								Change the plugin behaviour to update variables only when the OK button is clicked rather than on every keystroke
 * 								It is now possible to choose to which component the form refers to: the selected component in the view, the view itself, or the whole model
 * 								Change the menu icon to make it clearer
 * 								Updates in the configuration file:
 * 									Add "version" property to indicate that the other changes have been correctly applied
 * 									Add ability to change the Ok, Cancel and Export to Excel button labels, width and height
 * 									The "objects" array has been renamed to "controls" to fit to the SWT controls it allows to create
 * 									The "value" property has been renamed to "variable" to clearly indicates that it uniquely can contain a variable
 * 									The "values" array in the table lines has been renamed to "cells" as it can contain literal strings and variables, depending on the corresponding column class
 *  								The "category : dynamic" property in the table lines has been renamed to "generate: yes" as it was confusing
 * 									Add the ability to use variables anywhere in literal strings (form name, tab name, labels, ...)
 * 									Add the ability to specify a default content to any variable
 * 									Add the ability to choose what to do when a variable is set to empty : ignore, create, or delete
 * 									Add the ability to set combo box as editable (it is possible to write any value) or not editable (only values listed in the combo can be selected)
 * 
 * TODO LIST :
 * 								Add an option to continue in case of error (by default, errors raise exceptions that may completely stop the form)
 * 								Add the ability to choose configuration files in the preference rather than having a single configuration file at a fixed location
 * 								Create a graphical interface to generate the forms rather than requiring the user to edit a json file that is quite very complex to understand
 */
public class FormPlugin extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "org.archicontribs.form";
	
	public static final String pluginVersion = "1.1";
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
	
	private static FormLogger logger;
	
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
	
	/**
	 * Check if two strings are equals<br>
	 * Replaces string.equals() to avoid nullPointerException
	 */
	public static boolean areEqual(String str1, String str2) {
		if ( str1 == null )
			return str2 == null;

		if ( str2 == null )
			return false;			// as str1 cannot be null at this stage

		return str1.equals(str2);
	}
}
