package org.archicontribs.form.menu;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.archicontribs.form.FormDialog;
import org.archicontribs.form.FormLogger;
import org.archicontribs.form.FormPlugin;
import org.archicontribs.form.FormVariable;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
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

import com.archimatetool.model.IArchimateDiagramModel;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimateModelObject;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelArchimateConnection;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.IDiagramModelComponent;
import com.archimatetool.model.IDiagramModelObject;
import com.archimatetool.model.IFolder;


/**
 * Read the configuration files from the preferences and create the menu entries accordingly.<br>
 * <br>
 * This class voluntary does not show up any popup in case of error (as this would break up the menu)
 * 
 * @author Herve Jouin
 *
 */
//TODO: add a preference to popup the errors
public class FormMenu extends ExtensionContributionFactory {
	private static final FormLogger logger = new FormLogger(FormMenu.class);
	
	// the following variables are used by the getPosition method
    private static String formName = null;
    private static String tabName = null;
    private static String controlName = null;
    private static String controlClass = null;
    private static String columnName = null;

	@Override
	public void createContributionItems(IServiceLocator serviceLocator, IContributionRoot additions) {
	    if ( logger.isDebugEnabled() ) logger.debug("Form plugin : creating menu entries.");
        Object[] selection = ((IStructuredSelection) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection()).toArray();
        ImageDescriptor formMenuIcon = ImageDescriptor.createFromURL(FileLocator.find(Platform.getBundle(FormPlugin.PLUGIN_ID), new Path("icons/form.jpg"), null));	    
	    IPreferenceStore store = FormPlugin.INSTANCE.getPreferenceStore();
        boolean addSeparator = true;
        int menuEntriesLimit = 5;       // TODO: add a preference entry
        
        int lines = store.getInt(FormPlugin.storeConfigFilesPrefix+"_#");
        if ( logger.isDebugEnabled() ) logger.debug("Found "+lines+" files");
        
        loopOnConfigFiles:
        for (int line = 0; line <lines; line++) {
            String configFilename = store.getString(FormPlugin.storeConfigFilesPrefix+"_"+String.valueOf(line));
            if ( logger.isDebugEnabled() ) logger.debug("Opening configuration file \""+configFilename+"\" ...");

		    File f = new File(configFilename);

		    if( f.isDirectory() ) {
			    logger.error("Failed : is a directory");
			    continue;
		    }

    		if ( !f.canRead() ) {
    			logger.error("Failed : permission denied");
    			continue;
    		}

    		try {
    			JSONObject json = (JSONObject) new JSONParser().parse(new FileReader(configFilename));
    			int version;
    			try {
    			    version = FormDialog.getInt(json, "version");
    	            if ( version != 2 ) {
	                    logger.error("Ignored : not the right version (should be 2).");
	                    continue loopOnConfigFiles;
	                }
    			} catch (ClassCastException e) {
                    logger.error("Ignored : the version specified is not an integer (should be 2).");
                    continue loopOnConfigFiles;
                } catch (RuntimeException e) {
    			    logger.error("Ignored : the version is not specified (should be 2).");
                    continue loopOnConfigFiles;
    			}
    
    			JSONArray forms = FormDialog.getJSONArray(json, FormPlugin.PLUGIN_ID);
    			if ( logger.isTraceEnabled() ) logger.trace("Configuration file has got "+forms.size()+" forms.");
    
    			// we loop over the forms
    			for ( int formRank = 0; formRank < forms.size(); ++formRank ) {
    				JSONObject form = (JSONObject) forms.get(formRank);
    
    				HashSet<EObject>selected = new HashSet<EObject>();
    				String variableSeparator = FormDialog.getString(form,"variableSeparator", ":");
    				
    				formName = FormDialog.getString(form,"name");
    				if ( formName.isEmpty() ) {
    				    logger.error(getPosition("name")+" - cannot be empty");
    				    continue loopOnConfigFiles;
    				}
                    if ( logger.isDebugEnabled() ) logger.debug("Found form \""+formName+"\"");
    
    				JSONObject filter = FormDialog.getJSONObject(form, "filter", null);
    				if ( (filter != null) && logger.isDebugEnabled() ) logger.debug("Applying filter to selected components");
    
    				//we loop over the selected components
    				int menuEntries = 0;
                    loopOnForms:
    				for ( int selectionRank = 0; selectionRank < selection.length; ++selectionRank ) {
    					if ( ++menuEntries <= menuEntriesLimit ) {
    						EObject selectedObject = getSelectedObject(selection[selectionRank]);

		                    String refers = FormDialog.getString(form,"refers", "selected");
		                    switch ( refers.toLowerCase() ) {
		                        case "selected" :
		                            if ( logger.isTraceEnabled() ) logger.trace("Refers to selected object "+FormPlugin.getDebugName(selectedObject));
		                            break;
		                        
		                        case "view" :
    	                            if ( !(selectedObject instanceof IDiagramModelObject) && !(selectedObject instanceof IArchimateDiagramModel) ) {
	                                    logger.error(getPosition("refers")+" - the form refers to the object's view but the object \""+selectedObject.getClass().getSimpleName()+"\" is not inside a view");
	                                    continue loopOnForms;
	                                }
    	                            
                                    if ( selectedObject instanceof IDiagramModelObject ) {
                                        selectedObject = getContainer(selectedObject);
                                    }
                                    
                                    if ( logger.isTraceEnabled() ) logger.trace("Refers to the view "+FormPlugin.getDebugName(selectedObject));
		                            break;
		                            
		                        case "folder" :
		                            if ( selectedObject instanceof IArchimateModel ) {
	                                    logger.error(getPosition("refers")+" - the form refers to the object's folder but the object \""+selectedObject.getClass().getSimpleName()+"\" is not inside a folder");
	                                    continue loopOnForms;
	                                }
	                                else if ( selectedObject instanceof IDiagramModelArchimateObject ) {
	                                    selectedObject = ((IDiagramModelArchimateObject)selectedObject).getArchimateElement();
	                                }
	                                else if ( selectedObject instanceof IDiagramModelArchimateConnection ) {
	                                    selectedObject = ((IDiagramModelArchimateConnection)selectedObject).getArchimateRelationship();
	                                }
	                                
		                            selectedObject = getContainer(selectedObject);
		                            
	                                if ( logger.isTraceEnabled() ) logger.trace("Refers to the folder "+FormPlugin.getDebugName(selectedObject));
		                            break;
		                        case "model" :
		                            selectedObject = getModel(selectedObject);
		                            
	                                if ( logger.isTraceEnabled() ) logger.trace("Refers to the model "+FormPlugin.getDebugName(selectedObject));
		                            break;
		                            
		                        default :
		                            logger.error(getPosition("refers")+" - unknown value, must be \"selected\", \"view\" or \"model\".");
		                            continue loopOnConfigFiles;
		                    }
    
    						// we guarantee than an object is not included in the same menu several times
    						if ( !selected.contains(selectedObject) && ((filter == null) || FormDialog.checkFilter(selectedObject, variableSeparator, filter)) ) {
    							String menuLabel = FormVariable.expand(formName, variableSeparator, selectedObject);
    							if ( logger.isDebugEnabled() ) logger.debug("Adding menu entry \""+menuLabel+"\"");
    							
    							// we add the parameters
    							//    org.archicontribs.form.fileName            configuration file name
    							//    org.archicontribs.form.formRank            rank of the form in the file
    							//    org.archicontribs.form.selectionRank       rank of the selected object
    							Map<String, Object> commandParameters = new HashMap<String, Object>();
    							commandParameters.put("org.archicontribs.form.fileName", configFilename);
    							commandParameters.put("org.archicontribs.form.formRank", String.valueOf(formRank));
    							commandParameters.put("org.archicontribs.form.selectionRank", String.valueOf(selectionRank));
    
    							CommandContributionItemParameter p = new CommandContributionItemParameter(
    									PlatformUI.getWorkbench().getActiveWorkbenchWindow(),	// serviceLocator
    									"org.archicontribs.form.formMenuContributionItem",		// id
    									"org.archicontribs.form.showForm",						// commandId
    									commandParameters,										// parameters
    									formMenuIcon,											// icon
    									null,													// disabledIcon
    									null,													// hoverIcon
    									menuLabel,												// label
    									null,													// mnemonic
    									null,													// tooltip 
    									CommandContributionItem.STYLE_PUSH,						// style
    									null,													// helpContextId
    									true);													// visibleEnabled
    
    							if ( logger.isDebugEnabled() ) logger.debug("Adding menu \""+formName+"\"");
    
    							CommandContributionItem item = new CommandContributionItem(p);
    							item.setVisible(true);
    							if ( addSeparator ) {
    								additions.addContributionItem(new Separator(), null);
    								addSeparator = false;
    							}
    							additions.addContributionItem(item, null);
    							selected.add(selectedObject);
    						}
    					}
    				}
    			}
    		} catch (IOException e) {
    			logger.error("I/O Error while reading configuration file \""+configFilename+"\"",e);
    		} catch (ParseException e) {
    			if ( e.getMessage() !=null )
    				logger.error("Parsing error while reading configuration file \""+configFilename+"\"",e);
    			else
    				logger.error("Parsing error while reading configuration file \""+configFilename+"\" : Unexpected "+e.getUnexpectedObject().toString()+" at position "+e.getPosition());
    		}  catch (ClassCastException e) {
    		    logger.error("Wrong key type in the configuration files \""+configFilename+"\"",e);
    		}
        }
	}
	
    public static String getPosition(String attributeName) {
        StringBuilder str = new StringBuilder();
                
        str.append("In form \"").append(formName).append("\"");
        
        if ( tabName != null )
            str.append("\nIn tab \"").append(tabName).append("\"");
        
        if ( controlName != null )
            str.append("\nIn ").append(controlClass).append(" \"").append(controlName).append("\"");
        
        if ( columnName != null )
            str.append("\nIn column \"").append(columnName).append("\"");
        
        if ( attributeName != null )
            str.append("\nAttribute \"").append(attributeName).append("\"");
        
        return str.toString();
    }
    
    public static EObject getSelectedObject(Object obj) {
    	// if graphical object in a view
        if ( obj instanceof AbstractEditPart )
        	return (EObject) ((AbstractEditPart)obj).getModel();

        // if not a graphical object
        return (EObject)obj;
    }
    
    public static EObject getContainer(EObject obj) {
        EObject container = obj;
        while ( !(container instanceof IDiagramModel || container instanceof IFolder) ) {
            container = container.eContainer();
        }
        return container;
    }
    
    
    public static EObject getModel(EObject obj) {
        if ( obj instanceof IArchimateModel )
            return (IArchimateModel)obj;
        
        if ( obj instanceof IArchimateModelObject )
            return ((IArchimateModelObject)obj).getArchimateModel();
        
        if ( obj instanceof IDiagramModelComponent )
        	return ((IDiagramModelComponent)obj).getDiagramModel().getArchimateModel();
        
        return ((IDiagramModelObject)obj).getDiagramModel().getArchimateModel();
    }
}
