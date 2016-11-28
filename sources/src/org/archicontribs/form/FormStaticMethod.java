package org.archicontribs.form;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

public class FormStaticMethod {
	public static final String PLUGIN_ID = "org.archicontribs.form";
	
	public static final String pluginVersion = "0.1.0";
	public static final String pluginTitle = "Form v" + pluginVersion;
	
	public static String pluginsFolder = (new File(FormStaticMethod.class.getProtectionDomain().getCodeSource().getLocation().getPath())).getParent();
	public static String configFilePath = pluginsFolder + File.separatorChar + FormStaticMethod.PLUGIN_ID+".conf";
	
	/**
	 * level (Info, Warning, Error) for reporting  
	 */
	public enum Level { Info, Warning, Error };
	
	/**
	 * Shows up an on screen popup, displaying the message
	 * @param level
	 * @param msg
	 */
	public static void popup(Level level, String msg) {
		popup(level,msg,null);
	}
	
	/**
	 * Shows up an on screen popup, displaying the message and the exception message (if any).
	 * The exception stacktrace is also printed on the standard error stream
	 * @param level
	 * @param msg
	 * @param e
	 */
	public static void popup(Level level, String msg, Exception e) {
		String msg2 = msg;
		System.out.println(msg);
		System.out.println(e);
		if ( e != null ) {
			String errMsg = e.getMessage();
			if ( errMsg == null ) errMsg = e.toString();
			System.out.println("exception :" + errMsg);
			msg2 += "\n\n" + errMsg;
			e.printStackTrace(System.err);
		}
		switch ( level ) {
		case Info :
			MessageDialog.openInformation(Display.getDefault().getActiveShell(), pluginTitle, msg2);
			break;
		case Warning :
			MessageDialog.openWarning(Display.getDefault().getActiveShell(), pluginTitle, msg2);
			break;
		case Error :
			MessageDialog.openError(Display.getDefault().getActiveShell(), pluginTitle, msg2);
			break;
		}
	}
	
	/**
	 * Shows up an on screen popup, displaying the question and the exception message (if any).
	 * The exception stacktrace is also printed on the standard error stream
	 * 
	 * @param msg
	 * @return true or false
	 */
	public static boolean question(String msg) {
		boolean result = MessageDialog.openQuestion(Display.getDefault().getActiveShell(), pluginTitle, msg);
		return result;
	}
	
}
