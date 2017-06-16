package org.archicontribs.form;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map.Entry;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Level;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
 * v1.5 :		18/06/2017		finally, it is now possible to choose the configuration file(s)
 *                              adding first online help pages
 * 
 * TODO LIST :
 * 								Add an option to continue in case of error (by default, errors raise exceptions that may completely stop the form)
 *                              Add "if" that works like "filter" but for individual controls ... if the condition is met, the control is created, else it is not created.
 * 								Create a graphical interface to generate the forms rather than requiring the user to edit a json file that is quite very complex to understand
 */
public class FormPlugin extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "org.archicontribs.form";

	public static final String pluginVersion = "1.5";
	public static final String pluginName = "FormPlugin";
	public static final String pluginTitle = "Form plugin v" + pluginVersion;
	
	public static final String storeConfigFilesPrefix = "configFile";
	
	public static String pluginsFolder;
	public static String pluginsPackage;
	public static String pluginsFilename;

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
					String installedPluginsFilename = Files.readAllBytes(Paths.get(pluginsFolder+File.separator+"formPlugin.new")).toString();

					if ( areEqual(pluginsFilename, installedPluginsFilename) ) 
						FormDialog.popup(Level.INFO, "The form plugin has been correctly updated to version "+pluginVersion);
					else
						FormDialog.popup(Level.ERROR, "The form plugin has been correctly downloaded to \""+installedPluginsFilename+"\" but you are still using the form plugin version "+pluginVersion+".\n\nPlease check the plugin files located in the \""+pluginsFolder+"\" folder.");
				} catch (IOException e1) {
					FormDialog.popup(Level.WARN, "A new version of the form plugin has been downloaded but we failed to check if you are using the latest version.\n\nPlease check the plugin files located in the \""+pluginsFolder+"\" folder.");
				}

				try {
					if ( logger.isDebugEnabled() ) logger.debug("deleting file "+pluginsFolder+File.separator+"formPlugin.new");
					Files.delete(FileSystems.getDefault().getPath(pluginsFolder+File.separator+"formPlugin.new"));
				} catch ( IOException e ) {
					FormDialog.popup(Level.ERROR, "Failed to delete file \""+pluginsFolder+File.separator+"formPlugin.new\"\n\nYou need to delete it manually.");
				}
			} else if ( preferenceStore.getBoolean("checkForUpdateAtStartup") ) {
				checkForUpdate(false);
			}
		} catch ( IOException e ) {
			FormDialog.popup(Level.ERROR, "Failed to get form plugin's folder.", e);
		}
	}

	@Override
	public IPreferenceStore getPreferenceStore() {
		if (preferenceStore == null) {
			preferenceStore = new ScopedPreferenceStore( InstanceScope.INSTANCE, PLUGIN_ID );
		}
		return preferenceStore;
	}
	
	private static ProgressBar updateProgressbar = null;
	private static int updateDownloaded = 0;
	public static void checkForUpdate(boolean verbose) {
		new Thread("checkForUpdate") {
			@Override
			public void run() {
				if ( verbose )
					FormDialog.popup("Please wait while checking for new form plugin ...");
				else
					logger.debug("Checking for a new plugin version on GitHub");
				
				// We connect to GitHub and get the latest plugin file version
				// Do not forget the "-Djdk.http.auth.tunneling.disabledSchemes=" in the ini file if you connect through a proxy
				String PLUGIN_API_URL = "https://api.github.com/repos/archi-contribs/form-plugin/contents";
				String RELEASENOTE_URL = "https://github.com/archi-contribs/form-plugin/blob/master/release_note.md";

				Map<String, String> versions = new TreeMap<String, String>(Collections.reverseOrder());

				try {
					JSONParser parser = new JSONParser();
					Authenticator.setDefault(new Authenticator() {
						@Override
						protected PasswordAuthentication getPasswordAuthentication() {
						    logger.debug("requestor type = "+getRequestorType());
							if (getRequestorType() == RequestorType.PROXY) {
								String prot = getRequestingProtocol().toLowerCase();
								String host = System.getProperty(prot + ".proxyHost", "");
								String port = System.getProperty(prot + ".proxyPort", "80");
								String user = System.getProperty(prot + ".proxyUser", "");
								String pass = System.getProperty(prot + ".proxyPassword", "");
								
								if ( logger.isDebugEnabled() ) {
								    logger.debug("proxy request from "+getRequestingHost()+":"+getRequestingPort());
								    logger.debug("proxy configuration:");
								    logger.debug("   prot : "+prot);
								    logger.debug("   host : "+host);
								    logger.debug("   port : "+port);
								    logger.debug("   user : "+user);
								    logger.debug("   pass : xxxxx");
								}

								// we check if the request comes from the proxy, else we do not send the password (for security reason)
								// TODO: check IP address in addition of the FQDN
								if ( getRequestingHost().equalsIgnoreCase(host) && (Integer.parseInt(port) == getRequestingPort()) ) {
									// Seems to be OK.
									logger.debug("Setting PasswordAuthenticator");
									return new PasswordAuthentication(user, pass.toCharArray());
								} else {
								    logger.debug("Not setting PasswordAuthenticator as the request does not come from the proxy (host + port)");
								}
							}
							return null;
						}  
					});
					
					
                    if ( logger.isDebugEnabled() ) logger.debug("connecting to "+PLUGIN_API_URL);
                    HttpsURLConnection conn = (HttpsURLConnection)new URL(PLUGIN_API_URL).openConnection();

					if ( logger.isDebugEnabled() ) logger.debug("getting file list");
					JSONArray result = (JSONArray)parser.parse(new InputStreamReader(conn.getInputStream()));

					if ( result == null ) {
						if ( verbose ) {
							FormDialog.closePopup();
							FormDialog.popup(Level.ERROR, "Failed to check for new form plugin version.\n\nParsing error.");
						} else
							logger.error("Failed to check for new form plugin version.\n\nParsing error.");
						return;
					}

					if ( logger.isDebugEnabled() ) logger.debug("searching for plugins jar files");
					Pattern p = Pattern.compile(pluginsPackage+"_v(.*).jar") ;

					@SuppressWarnings("unchecked")
					Iterator<JSONObject> iterator = result.iterator();
					while (iterator.hasNext()) {
						JSONObject file = iterator.next();
						Matcher m = p.matcher((String)file.get("name")) ;
						if ( m.matches() ) {
							if ( logger.isDebugEnabled() ) logger.debug("found version "+m.group(1)+" ("+(String)file.get("download_url")+")");
							versions.put(m.group(1), (String)file.get("download_url"));
						}
					}

					if ( verbose ) FormDialog.closePopup();

					if ( versions.isEmpty() ) {
						if ( verbose )
							FormDialog.popup(Level.ERROR, "Failed to check for new form plugin version.\n\nDid not find any "+pluginsPackage+" JAR file.");
						else
							logger.error("Failed to check for new form plugin version.\n\nDid not find any "+pluginsPackage+" JAR file.");
						return;
					}
				} catch (Exception e) {
					if ( verbose ) {
						FormDialog.closePopup();
						FormDialog.popup(Level.ERROR, "Failed to check for new version on GitHub.", e);
					} else {
						logger.error("Failed to check for new version on GitHub.", e);
					}
					return;
				}

				String newPluginFilename = null;
				String tmpFilename = null;
				try {
					// treemap is sorted in descending order, so first entry should have the "bigger" key value, i.e. the latest version
					Entry<String, String> entry = versions.entrySet().iterator().next();

					if ( pluginVersion.compareTo((String)entry.getKey()) >= 0 ) {
						if ( verbose )
							FormDialog.popup(Level.INFO, "You already have got the latest version : "+pluginVersion);
						else
							logger.info("You already have got the latest version : "+pluginVersion);
						return;
					}
					
					if ( !pluginsFilename.endsWith(".jar") ) {
						if ( verbose )
							FormDialog.popup(Level.ERROR,"A new version of the form plugin is available:\n     actual version: "+pluginVersion+"\n     new version: "+(String)entry.getKey()+"\n\nUnfortunately, it cannot be downloaded while Archi is running inside Eclipse.");
						else
							logger.error("A new version of the form plugin is available:\n     actual version: "+pluginVersion+"\n     new version: "+(String)entry.getKey()+"\n\nUnfortunately, it cannot be downloaded while Archi is running inside Eclipse.");
						return;
					}

					boolean ask = true;
					while ( ask ) {
					    switch ( FormDialog.question("A new version of the form plugin is available:\n     actual version: "+pluginVersion+"\n     new version: "+(String)entry.getKey()+"\n\nDo you wish to download and install it ?", new String[] {"Yes", "No", "Check release note"}) ) {
					        case 0 : ask = false ; break;  // Yes
					        case 1 : return ;              // No
					        case 2 : ask = true ;          // release note
        					         Program.launch(RELEASENOTE_URL);
        					         break;
					    }
					}

					Display.getDefault().syncExec(new Runnable() { @Override public void run() { updateProgressbar = FormDialog.progressbarPopup("Downloading new version of form plugin ..."); }});

					URLConnection conn = new URL(entry.getValue()).openConnection();
					String FileType = conn.getContentType();
					int fileLength = conn.getContentLength();

					newPluginFilename = pluginsFolder+File.separator+entry.getValue().substring(entry.getValue().lastIndexOf('/')+1, entry.getValue().length());
					tmpFilename = newPluginFilename+".tmp";

					if ( logger.isTraceEnabled() ) {
						logger.trace("   File URL : " + entry.getValue());
						logger.trace("   File type : " + FileType);
						logger.trace("   File length : "+fileLength);
						logger.trace("   Tmp download file path : " + tmpFilename);
						logger.trace("   New Plugin file path : " + newPluginFilename);
					}

					if (fileLength == -1)
						throw new IOException("Failed to get file size.");
					else
						Display.getDefault().syncExec(new Runnable() { @Override public void run() { updateProgressbar.setMaximum(fileLength); }});

					InputStream in = conn.getInputStream();
					FileOutputStream fos = new FileOutputStream(new File(tmpFilename));	                
					byte[] buff = new byte[1024];
					int n;
					updateDownloaded = 0;

					if ( logger.isDebugEnabled() ) logger.debug("downloading file ...");
					while ((n=in.read(buff)) !=-1) {
						fos.write(buff, 0, n);
						updateDownloaded +=n;
						Display.getDefault().syncExec(new Runnable() { @Override public void run() { updateProgressbar.setSelection(updateDownloaded); }});
						//if ( logger.isTraceEnabled() ) logger.trace(updateDownloaded+"/"+fileLength);
					}
					fos.flush();
					fos.close();

					if ( logger.isDebugEnabled() ) logger.debug("download finished");

				} catch (Exception e) {
					logger.info("here");
					if( updateProgressbar != null ) Display.getDefault().syncExec(new Runnable() { @Override public void run() { updateProgressbar.getShell().dispose(); updateProgressbar = null; }});
					try {
						if ( tmpFilename != null ) Files.deleteIfExists(FileSystems.getDefault().getPath(tmpFilename));
					} catch (IOException e1) {
						logger.error("cannot delete file \""+tmpFilename+"\"", e1);
					}
					if ( verbose )
						FormDialog.popup(Level.ERROR, "Failed to download new version of form plugin.", e);
					else
						logger.error("Failed to download new version of form plugin.",e);
					return;
				}

				if( updateProgressbar != null ) Display.getDefault().syncExec(new Runnable() { @Override public void run() { updateProgressbar.getShell().dispose(); updateProgressbar = null;}});

				//install new plugin

				// we rename the tmpFilename to its definitive filename
				if ( logger.isDebugEnabled() ) logger.debug("renaming \""+tmpFilename+"\" to \""+newPluginFilename+"\"");
				try {
					Files.move(FileSystems.getDefault().getPath(tmpFilename), FileSystems.getDefault().getPath(newPluginFilename), StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					if ( verbose )
						FormDialog.popup(Level.ERROR, "Failed to rename \""+tmpFilename+"\" to \""+newPluginFilename+"\"");
					else
						logger.error("Failed to rename \""+tmpFilename+"\" to \""+newPluginFilename+"\"");
					return;
				}

				try {
					Files.write(Paths.get(pluginsFolder+File.separator+"formPlugin.new"), newPluginFilename.getBytes());
				} catch(IOException ign) {
					// not a big deal, just that there will be no message after Archi is restarted
					logger.error("Cannot create file \""+pluginsFolder+File.separator+"formPlugin.new\"", ign);
				}

				// we delete the actual plugin file on Archi exit (can't do it here because the plugin is in use).
				(new File(pluginsFilename)).deleteOnExit();

				if( FormDialog.question("A new version on the form plugin has been downloaded. Archi needs to be restarted to install it.\n\nDo you wish to restart Archi now ?") ) {
					Display.getDefault().syncExec(new Runnable() { @Override public void run() { PlatformUI.getWorkbench().restart(); }});
				}
			};
		}.start();
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
