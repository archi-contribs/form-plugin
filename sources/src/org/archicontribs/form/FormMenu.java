package org.archicontribs.form;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.archicontribs.form.FormStaticMethod.Level;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class FormMenu extends CompoundContributionItem {

	protected IContributionItem[] getContributionItems() {
		//ArchimateDiagramPart
		List<IContributionItem> contributionItems = new ArrayList<IContributionItem>();
		
		System.out.println("config file path = " + FormStaticMethod.configFilePath);
		
		try {
			JSONParser parser = new JSONParser();
			ImageDescriptor menuIcon;
			
			JSONObject jsonFile = (JSONObject)parser.parse(new FileReader(FormStaticMethod.configFilePath));
			
			JSONArray forms = (JSONArray) jsonFile.get(FormStaticMethod.PLUGIN_ID);
			
			if ( (forms == null) || forms.isEmpty() ) {
				FormStaticMethod.popup(Level.Error, "The file should begin with a \""+FormStaticMethod.PLUGIN_ID+"\" object.");
				return null;
			}
			
			@SuppressWarnings("unchecked")
			Iterator<JSONObject> iterator = forms.iterator();
            while (iterator.hasNext()) {
            	JSONObject form = iterator.next();
    			String name = (String) form.get("name");
    			System.out.println("   found form \""+name+"\"");
    			
    			menuIcon = ImageDescriptor.createFromURL(FileLocator.find(Platform.getBundle(FormStaticMethod.PLUGIN_ID), new Path("icons/array.jpg"), null));
    			
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
    			
    			contributionItems.add(new CommandContributionItem(p));
            }
            
		} catch (FileNotFoundException e) {
			FormStaticMethod.popup(Level.Error, "Configuration file \""+FormStaticMethod.configFilePath+"\" not found.");
		} catch (IOException e) {
			FormStaticMethod.popup(Level.Error, "I/O Error while reading configuration file \""+FormStaticMethod.configFilePath+"\"",e);
		} catch (ParseException e) {
			FormStaticMethod.popup(Level.Error, "Parsing error while reading configuration file \""+FormStaticMethod.configFilePath+"\"",e);
		}
		
		return contributionItems.toArray(new IContributionItem[contributionItems.size()]);
	}
}
