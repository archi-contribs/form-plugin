package org.archicontribs.form;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Level;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.ExtensionContributionFactory;
import org.eclipse.ui.menus.IContributionRoot;
import org.eclipse.ui.services.IServiceLocator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class FormMenu extends ExtensionContributionFactory {
	private static final FormLogger logger = new FormLogger(FormMenu.class);

	@Override
    public void createContributionItems(IServiceLocator serviceLocator, IContributionRoot additions) {
		if ( logger.isDebugEnabled() ) logger.debug("Config file path = " + FormPlugin.configFilePath);
		
		try {
			JSONParser parser = new JSONParser();
			ImageDescriptor menuIcon;
			
			JSONObject jsonFile = (JSONObject)parser.parse(new FileReader(FormPlugin.configFilePath));
			
			JSONArray forms = (JSONArray) jsonFile.get(FormPlugin.PLUGIN_ID);
			
			if ( (forms == null) || forms.isEmpty() ) {
				FormDialog.popup(Level.ERROR, "The file should begin with a \""+FormPlugin.PLUGIN_ID+"\" object.");
				return;
			}
			
			@SuppressWarnings("unchecked")
			Iterator<JSONObject> iterator = forms.iterator();
            while (iterator.hasNext()) {
            	JSONObject form = iterator.next();
    			String name = (String) form.get("name");
    			if ( logger.isDebugEnabled() ) logger.debug("Found form \""+name+"\"");
    			
    			menuIcon = ImageDescriptor.createFromURL(FileLocator.find(Platform.getBundle(FormPlugin.PLUGIN_ID), new Path("icons/array.jpg"), null));
    			
    			Map<String, Object> commandParameters = new HashMap<String, Object>();
    			commandParameters.put("org.archicontribs.form.showFormName", name);
    			
    			CommandContributionItemParameter p = new CommandContributionItemParameter(
    					PlatformUI.getWorkbench().getActiveWorkbenchWindow(),	// serviceLocator
    					"org.archicontribs.form.formMenuContributionItem",		// id
    					"org.archicontribs.form.showForm",						// commandId
    					commandParameters,										// parameters
    					menuIcon,												// icon
    					null,													// disabledIcon
    					null,													// hoverIcon
    					name,													// label
    					null,													// mnemonic
    					null,													// tooltip 
    					CommandContributionItem.STYLE_PUSH,						// style
    					null,													// helpContextId
    					true);													// visibleEnabled
    			
    			if ( logger.isDebugEnabled() ) logger.debug("Adding menu \""+name+"\"");
    			
    			CommandContributionItem item = new CommandContributionItem(p);
    	        item.setVisible(true);
    	        additions.addContributionItem(new Separator(), null);
    	        additions.addContributionItem(item, null);
            }
            
		} catch (FileNotFoundException e) {
			FormDialog.popup(Level.ERROR, "Configuration file \""+FormPlugin.configFilePath+"\" not found.");
		} catch (IOException e) {
			FormDialog.popup(Level.ERROR, "I/O Error while reading configuration file \""+FormPlugin.configFilePath+"\"",e);
		} catch (ParseException e) {
			if ( e.getMessage() !=null )
				FormDialog.popup(Level.ERROR, "Parsing error while reading configuration file \""+FormPlugin.configFilePath+"\"",e);
			else
				FormDialog.popup(Level.ERROR, "Parsing error while reading configuration file \""+FormPlugin.configFilePath+"\"\n\nUnexpected "+e.getUnexpectedObject().toString()+" at position "+e.getPosition());
		}
	}
}
