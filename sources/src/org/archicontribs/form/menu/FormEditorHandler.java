package org.archicontribs.form.menu;

import java.io.FileReader;
import java.io.IOException;
import org.apache.log4j.Level;
import org.archicontribs.form.FormDialog;
import org.archicontribs.form.FormJsonParser;
import org.archicontribs.form.FormPlugin;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * This classes is instantiated when a form is selected on the right-click menu
 * 
 * @author Herve Jouin
 *
 */
public class FormEditorHandler extends AbstractHandler {
    public static final Cursor CURSOR_WAIT = new Cursor(null, SWT.CURSOR_WAIT);
    public static final Cursor CURSOR_ARROW = new Cursor(null, SWT.CURSOR_ARROW);
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Object[] selection = ((IStructuredSelection) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection()).toArray();
		
		String configFilename = event.getParameter("org.archicontribs.form.fileName");
        int selectionRank = Integer.valueOf(event.getParameter("org.archicontribs.form.selectionRank"));
        
        for ( Shell shell: Display.getDefault().getShells() ) {
            shell.setCursor(CURSOR_WAIT);
        }
		
		// We do not redo all the testing as the file has just been parsed by the FormMenu class
		try ( FileReader reader = new FileReader(configFilename) ){
			JSONObject json = (JSONObject) new JSONParser().parse(reader);
			JSONObject form = FormJsonParser.getJSONObject(json, FormPlugin.PLUGIN_ID);
			EObject selectedObject = FormMenu.getSelectedObject(selection[selectionRank]);
			
			String refers = FormJsonParser.getString(form, "refers", FormDialog.validRefers[0], FormDialog.validRefers);
			switch ( refers.toLowerCase() ) {
				case "view":   selectedObject = FormMenu.getContainer(selectedObject); break;
				case "folder": selectedObject = FormMenu.getContainer(selectedObject); break;
				case "model":  selectedObject = FormMenu.getModel(selectedObject);     break;
				default:
				    // selectedObject remains the same
			}
			
			@SuppressWarnings("unused")
            FormDialog formdialog = new FormDialog(configFilename, form, selectedObject);
			
		} catch (IOException e) {
			FormDialog.popup(Level.ERROR, "I/O Error while reading configuration file:\n"+configFilename,e);
		} catch (ParseException e) {
			if ( e.getMessage() !=null )
				FormDialog.popup(Level.ERROR, "Parsing error while reading configuration file:\n"+configFilename,e);
			else
				FormDialog.popup(Level.ERROR, "Parsing error while reading configuration file:\n"+configFilename+"\n\nUnexpected "+e.getUnexpectedObject().toString()+" at position "+e.getPosition());
		}  catch (ClassCastException e) {
			FormDialog.popup(Level.ERROR, "Wrong key type in the configuration files:\n"+configFilename,e);
		}
		
        for ( Shell shell: Display.getDefault().getShells() ) {
            shell.setCursor(CURSOR_ARROW);
        }
		
		return null;
	}
}
