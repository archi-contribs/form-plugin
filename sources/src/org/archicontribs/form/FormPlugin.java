package org.archicontribs.form;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import org.apache.log4j.Level;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import com.archimatetool.model.IIdentifier;
import com.archimatetool.model.INameable;

/**
 * Form plugin for Archi, the Archimate modeler
 * 
 * The FormPlugin class implements static methods and properties used everywhere else in the form plugin. 
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
 * v1.2 :		23/05/2017		The plugin is now able to automatically download updates from GitHub
 * 								Solve a bug which prevent the form to save the updated values if it was related to a relationship
 * 								Add the ability to change the font name, size and style of label, text and combo controls
 * 
 * v1.3 :		27/05/2017		Allow to nest variables
 * 								Allow to change the character used to separate variable name from variable scope
 * 
 * v1.4 :		02/06/2017		Allow to select components directly on the tree on the left side of Archi window
 * 								Allow to select folders
 * 								Allow to select the model itself
 * 								Rewrite of error message to be more helpful in searching the error cause
 * 								Set the WRAP bit of the text controls
 * 								Solve bug which avoided the tooltip to showup on some controls
 * 
 * v1.4.1 :		06/06/2017		Solve "failed to get the model" error message
 * 
 * v1.5 :		17/06/2017		finally, it is now possible to choose the configuration file(s)
 *                              adding online help pages
 *                              The selected object can now be a referenced view, a canvas or a sketch view
 *                              
 * v1.5.1 :     19/06/2017		Removed dependency to Eclipse library which invalidated the form plugin
 * 
 * v1.5.2 :     21/06/2017      Correct default value on combo controls
 * 
 * v1.5.3 :		30/06/2017		Accept to change IDs
 * 
 * v1.5.4 :     05/08/2017      Add "in" and "iin" tests
 *                              Add "${source:xxx}" and "${target:xxx}" variables range
 *                              Add "${void}" variable
 *                              Bug corrections in export to Excel
 *                              Add "delete" excelDefault behaviour
 *                              Updating a control updates in real time all the other controls that refer to the same variable
 *                              Add "foreground" and "background" keywords for table columns
 *                              Labels are now in wrap mode
 * 
 * v1.6 :		25/09/2017		Add graphical interface to generate the configuration file
 * 								Configuration files can now contain a single form only
 * 								Change JSON file version to 3
 *  							Fix check for update at startup
 *                              Fix properties changes undo/redo
 *                              
 * v1.6.1 :		26/09/2017		Fix missing relationships in tables
 * 
 * v1.6.2 :     28/09/2017		Fix duplicate table lines when the form is applied to the whole model
 * 								Forcing layout of table widgets to display them quicker
 * 								Adding a filter editor on the form
 * 
 * v1.6.3 :		30/09/2017		Fix the "refers" field was not correctly set
 * 								Fix the plugin auto update
 *								Add tooltips on the graphical editor fields
 *
 * v1.6.4 :     03/10/2017      Fix getting default font from parent's
 *                              Fix checkbox value
 *                              
 * v1.6.5 :     25/10/2017      Fix Export to Excel button that was not shown when required
 *                              Add "not" keyword in filter tests
 *                              Fix table columns sorting that did not work since the graphical editor
 * 
 * v1.6.6 :     26/10/2017      Fix documentation variable
 * 
 * v1.6.7 :     27/10/2017      Fix table export to Excel after column sort
 *                              Fix table color and font after column sort
 *                              Fix default value for the "refers" property
 *                              
 * v1.7 :       12/11/2017		Fix the excelType value was not taken in account for all objects 
 * 
 * TODO LIST :
 * 								Add an option to continue in case of error (by default, errors raise exceptions that may completely stop the form)
 *                              Add "if" that works like "filter" but for individual controls ... if the condition is met, the control is created, else it is not created.
 */
public class FormPlugin extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "org.archicontribs.form";

	public static final String pluginVersion = "1.7";
	public static final String pluginName = "FormPlugin";
	public static final String pluginTitle = "Form plugin v" + pluginVersion;

	public static final String storeConfigFilesPrefix = "configFile";

	public static String pluginsFolder;
	public static String pluginsPackage;
	public static String pluginsFilename;

	private static boolean mustIgnoreErrors = false;

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
		preferenceStore.setDefault("progressWindow",	        "showAndWait");
		preferenceStore.setDefault("checkForUpdateAtStartup",   false);
		preferenceStore.setDefault("loggerMode",		        "disabled");
		preferenceStore.setDefault("loggerLevel",		        "INFO");
		preferenceStore.setDefault("loggerFilename",	        System.getProperty("user.home")+File.separator+pluginName+".log");
		preferenceStore.setDefault("loggerExpert",		        "log4j.rootLogger                               = INFO, stdout, file\n"+
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

		// we check if the plugin has been upgraded using the automatic procedure
		try {
			pluginsPackage = FormPlugin.class.getPackage().getName();
			pluginsFilename = new File(FormPlugin.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getCanonicalPath();
			pluginsFolder = (new File(pluginsFilename+File.separator+"..")).getCanonicalPath();

			if ( logger.isDebugEnabled() ) {
				logger.debug("plugin's package  = "+pluginsPackage);
				logger.debug("plugin's folder   = "+pluginsFolder);
				logger.debug("plugin's filename = "+pluginsFilename);
				if ( !pluginsFilename.endsWith(".jar") )
					logger.debug("(the plugin's filename is not a jar file, so Archi is running inside Eclipse)");
			}

			if ( Files.exists(FileSystems.getDefault().getPath(pluginsFolder+File.separator+"formPlugin.new"), LinkOption.NOFOLLOW_LINKS) ) {
				if ( logger.isDebugEnabled() ) logger.debug("found file \""+pluginsFolder+File.separator+"formPlugin.new\"");

				try {
					BufferedReader reader = new BufferedReader(new FileReader (pluginsFolder+File.separator+"formPlugin.new"));
					String installedPluginsFilename = reader.readLine();
					if ( areEqual(pluginsFilename, installedPluginsFilename) ) 
						Display.getDefault().syncExec(new Runnable() { @Override public void run() { FormDialog.popup(Level.INFO, "The form plugin has been correctly updated to version "+pluginVersion); }});
					else
						Display.getDefault().syncExec(new Runnable() { @Override public void run() { FormDialog.popup(Level.ERROR, "The form plugin has been correctly downloaded to \""+installedPluginsFilename+"\" but you are still using the form plugin version "+pluginVersion+".\n\nPlease check the plugin files located in the \""+pluginsFolder+"\" folder."); }});
					
					reader.close();
				} catch (IOException e1) {
					Display.getDefault().syncExec(new Runnable() { @Override public void run() { FormDialog.popup(Level.WARN, "A new version of the form plugin has been downloaded but we failed to check if you are using the latest version.\n\nPlease check the plugin files located in the \""+pluginsFolder+"\" folder."); }});
				}

				try {
					if ( logger.isDebugEnabled() ) logger.debug("deleting file "+pluginsFolder+File.separator+"formPlugin.new");
					Files.delete(FileSystems.getDefault().getPath(pluginsFolder+File.separator+"formPlugin.new"));
				} catch ( IOException e ) {
					Display.getDefault().syncExec(new Runnable() { @Override public void run() { FormDialog.popup(Level.ERROR, "Failed to delete file \""+pluginsFolder+File.separator+"formPlugin.new\"\n\nYou need to delete it manually."); }});
				}
			} else if ( preferenceStore.getBoolean("checkForUpdateAtStartup") ) {
				new Thread("checkForUpdate") {
					@Override
					public void run() {
						FormUpdate formUpdate = new FormUpdate();
						formUpdate.checkUpdate(false);
					}
				}.start();
			}
		} catch ( IOException e ) {
			FormDialog.popup(Level.ERROR, "Failed to get form plugin's folder.", e);
		}
	}

	public static void setMustIgnoreErrors(boolean ignoreErrors) {
		mustIgnoreErrors = ignoreErrors;
	}
	public static boolean mustIgnoreErrors() {
		return mustIgnoreErrors;
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
	
	/**
	 * Check if two strings are equals<br>
	 * Replaces string.equalsIgnoreCase() to avoid nullPointerException
	 */
	public static boolean areEqualIgnoreCase(String str1, String str2) {
		if ( str1 == null )
			return str2 == null;

		if ( str2 == null )
			return false;			// as str1 cannot be null at this stage

		return str1.equalsIgnoreCase(str2);
	}

	/**
	 * Check if a string is null or empty<br>
	 * Replaces string.isEmpty() to avoid nullPointerException
	 */
	public static boolean isEmpty(String str) {
		return str==null || str.isEmpty();
	}

	/**
	 * Calculates the debug name of an EObject
	 * @return getclass().getSimpleName()+":\""+getName()+"\"("+getId()+")"
	 */
	public static String getDebugName(EObject eObject) {
		if ( eObject == null ) 
			return "null";

		StringBuilder debugName = new StringBuilder(eObject.getClass().getSimpleName());
		debugName.append(":\""+((INameable)eObject).getName()+"\"");
		debugName.append("("+((IIdentifier)eObject).getId()+")");
		return debugName.toString();
	}

	/**
	 * Throw a RuntimeException with the errStrin ,except when if in "mustIgnoreError" mode 
	 */
	public static void error(String errString) {
		if ( mustIgnoreErrors )
			logger.error(errString);
		else 
			throw new RuntimeException(errString);
	}

	/**
	 * Throw a RuntimeException with the errStrin ,except when if in "mustIgnoreError" mode 
	 */
	public static void error(String errString, Exception e) {
		if ( mustIgnoreErrors )
			logger.error(errString, e);
		else 
			throw new RuntimeException(errString, e);
	}

	/**
	 * Sets the control's foreground color according to the RGB values encoded in the foreground parameter, or to its parent's foreground in case of error 
	 */
	public static void setColor(Control control, String color, int colorType) {
		assert (colorType==SWT.FOREGROUND || colorType==SWT.BACKGROUND);

		String colorTypeString = colorType==SWT.FOREGROUND ? "foreground" : "background";

		if ( !FormPlugin.isEmpty(color) ) {
			String[] colorArray = color.split(",");
			if ( colorArray.length != 3 ) {
				error(FormPosition.getPosition(colorTypeString) + "\n\nCannot set "+colorTypeString+" as it is not under the \"R,G,B\" form (where R, G and B are numeric values between 0 and 255).");
			} else try {
				if ( colorType == SWT.FOREGROUND )
					control.setForeground(new Color(control.getDisplay(), Integer.parseInt(colorArray[0].trim()), Integer.parseInt(colorArray[1].trim()), Integer.parseInt(colorArray[2].trim())));
				else
					control.setBackground(new Color(control.getDisplay(), Integer.parseInt(colorArray[0].trim()), Integer.parseInt(colorArray[1].trim()), Integer.parseInt(colorArray[2].trim())));
			} catch (Exception e) {
				error(FormPosition.getPosition(colorTypeString) + "\n\nFailed to set "+colorTypeString, e);
			}
		} else if ( !(control instanceof Shell) ) {
			try {
				if ( colorType == SWT.FOREGROUND )
					control.setForeground(control.getParent().getForeground());
				else
					control.setBackground(control.getParent().getBackground());
			} catch (Exception e) {
				error(FormPosition.getPosition(colorTypeString) + "\n\nFailed to set "+colorTypeString+" from parent's one.", e);
			}
		}
	}

	/**
	 * Sets the control's font  color according to the RGB vlues encoded in the foreground parameter, or to its parent's foreground in case of error 
	 */
	public static void setFont(Control control, String fontName, Integer fontSize, Boolean fontBold, Boolean fontItalic) {
		if ( FormPlugin.isEmpty(fontName) && (fontSize == null || fontSize <= 0) && fontBold == null && fontItalic == null ) {
			try {
				control.setFont(control.getParent().getFont());
			} catch (Exception e) {
				error(FormPosition.getPosition("font") + "\n\nFailed to set font from parent's one.", e);
			}
        } else {
            int style = SWT.NORMAL;
            if ( fontBold != null && fontBold )   style |= SWT.BOLD;
            if ( fontItalic != null && fontItalic ) style |= SWT.ITALIC;
            if ( fontSize == null ) fontSize = 0;
            try {
	            if ( FormPlugin.isEmpty(fontName) )
	            	control.setFont(FontDescriptor.createFrom(control.getFont()).setHeight(fontSize).setStyle(style).createFont(control.getDisplay()));
	            else
	            	control.setFont(new Font(control.getDisplay(), fontName, fontSize, style));
            } catch (Exception e) {
				error(FormPosition.getPosition("font") + "\n\nFailed to set font.", e);
            }
        }
	}
	
	/**
	 * Sets the control's alignment if defined
	 */
	public static void setAlignment(Widget widget, String alignment) {
		if ( !FormPlugin.isEmpty(alignment) ) {
			int alignmentValue;
			switch ( alignment.toLowerCase() ) {
	            case "left":   alignmentValue = SWT.LEFT;  break;
	            case "right":  alignmentValue = SWT.RIGHT; break;
	            case "center": alignmentValue = SWT.CENTER;break;
	            default:
	            	error(FormPosition.getPosition("alignment") + "\n\nInvalid alignment value \""+alignment+"\", must be \"right\", \"left\" or \"center\".");
	            	return;
			}
			switch ( widget.getClass().getSimpleName() ) {
				case "Label":		((Label)widget).setAlignment(alignmentValue); break;
				case "StyledText":	((StyledText)widget).setAlignment(alignmentValue); break;
				case "Button":		((Button)widget).setAlignment(alignmentValue); break;
				case "TableColumn":	((TableColumn)widget).setAlignment(alignmentValue); break;
				default:
	            	throw new RuntimeException("Cannot set alignment to a "+widget.getClass().getSimpleName());
			}
        }
	}
	
	public static String concat(String[] array, String quote, String separator) {
		StringBuilder sb = new StringBuilder();
		
		if ( quote == null )
			quote = "";
		
		boolean isFirst = true;
		
		for ( String elem: array) {
			if ( !isFirst )
				sb.append(separator);
			sb.append(quote + elem + quote);
			isFirst = false;
		}
		
		return sb.toString();
	}
}
