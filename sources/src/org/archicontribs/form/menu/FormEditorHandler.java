package org.archicontribs.form.menu;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import org.apache.log4j.Level;
import org.archicontribs.form.FormDialog;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.archimatetool.canvas.editparts.CanvasBlockEditPart;
import com.archimatetool.canvas.editparts.CanvasDiagramPart;
import com.archimatetool.canvas.editparts.CanvasStickyEditPart;
import com.archimatetool.editor.diagram.editparts.ArchimateDiagramPart;
import com.archimatetool.editor.diagram.editparts.ArchimateElementEditPart;
import com.archimatetool.editor.diagram.editparts.ArchimateRelationshipEditPart;
import com.archimatetool.editor.diagram.editparts.DiagramConnectionEditPart;
import com.archimatetool.editor.diagram.editparts.diagram.DiagramImageEditPart;
import com.archimatetool.editor.diagram.editparts.diagram.GroupEditPart;
import com.archimatetool.editor.diagram.editparts.diagram.NoteEditPart;
import com.archimatetool.editor.diagram.sketch.editparts.SketchActorEditPart;
import com.archimatetool.editor.diagram.sketch.editparts.SketchDiagramPart;
import com.archimatetool.editor.diagram.sketch.editparts.SketchGroupEditPart;
import com.archimatetool.editor.diagram.sketch.editparts.StickyEditPart;
import com.archimatetool.model.IArchimateDiagramModel;
import com.archimatetool.model.IArchimateModelObject;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.IFolder;

/**
 * This classes is instantiated when a form is selected on the right-click menu
 * @author Herve Jouin
 *
 */
public class FormEditorHandler extends AbstractHandler {
    public static final Cursor CURSOR_WAIT = new Cursor(null, SWT.CURSOR_WAIT);
    public static final Cursor CURSOR_ARROW = new Cursor(null, SWT.CURSOR_ARROW);
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Object[] selection = ((IStructuredSelection) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection()).toArray();
		String configFilename;
		
		try {
			configFilename = Paths.get(FormPlugin.pluginsFilename.replace(".jar", "")+".conf").toRealPath().toString();
		} catch (IOException e1) {
			configFilename = FormPlugin.pluginsFilename.replace(".jar", "")+".conf";
		}
		
		File f = new File(configFilename);

		if( !f.exists() || f.isDirectory() ) {
			FormDialog.popup(Level.ERROR, "Configuration file not found:\n"+configFilename);
			return null;
		}

		if ( !f.canRead() ) {
			FormDialog.popup(Level.ERROR, "Cannot read configuration file:\n"+configFilename+"\n\nPermission denied.");
			return null;
		}

		try {			
			JSONObject json = (JSONObject) new JSONParser().parse(new FileReader(configFilename));
			int version = FormDialog.getInt(json, "version");
			if ( version != 2 )
				throw new RuntimeException("Not the right version (should be 2).");
			
			JSONArray forms = FormDialog.getJSONArray(json, FormPlugin.PLUGIN_ID);

			int formRank = Integer.valueOf(event.getParameter("org.archicontribs.form.formRank"));
			int selectionRank = Integer.valueOf(event.getParameter("org.archicontribs.form.selectionRank"));
			Object obj = selection[selectionRank];
			EObject selectedObject;
			switch ( obj.getClass().getSimpleName() ) {
				case "ArchimateElementEditPart" : selectedObject = ((ArchimateElementEditPart)obj).getModel(); break;
				case "ArchimateRelationshipEditPart" : selectedObject = ((ArchimateRelationshipEditPart)obj).getModel(); break;
				case "ArchimateDiagramPart" : selectedObject = ((ArchimateDiagramPart)obj).getModel(); break;
				case "CanvasDiagramPart" : selectedObject = ((CanvasDiagramPart)obj).getModel(); break;
				case "SketchDiagramPart" : selectedObject = ((SketchDiagramPart)obj).getModel(); break;
				case "CanvasBlockEditPart" : selectedObject = ((CanvasBlockEditPart)obj).getModel(); break;
				case "CanvasStickyEditPart" : selectedObject = ((CanvasStickyEditPart)obj).getModel(); break;
				case "DiagramConnectionEditPart" : selectedObject = ((DiagramConnectionEditPart)obj).getModel(); break;
				case "DiagramImageEditPart" : selectedObject = ((DiagramImageEditPart)obj).getModel(); break;
				case "GroupEditPart" : selectedObject = ((GroupEditPart)obj).getModel(); break;
				case "NoteEditPart" : selectedObject = ((NoteEditPart)obj).getModel(); break;
				case "SketchActorEditPart" : selectedObject = ((SketchActorEditPart)obj).getModel(); break;
				case "SketchGroupEditPart" : selectedObject = ((SketchGroupEditPart)obj).getModel(); break;
				case "StickyEditPart" : selectedObject = ((StickyEditPart)obj).getModel(); break;
                                                         
                default : selectedObject = (EObject)obj;                 // elements, relationships
			}
			
			JSONObject form = (JSONObject) forms.get(formRank);
			
			String refers = FormDialog.getString(form,"refers", "");
			switch ( refers.toLowerCase() ) {
				case "":
				case "selected" :
				    break;
				    
				case "view" :
				    while ( !(selectedObject instanceof IDiagramModel) ) {
				        selectedObject = selectedObject.eContainer();
				    }
				    break;
				    
				case "folder":
				    if ( !(selectedObject instanceof IFolder) ) {
				        selectedObject = selectedObject.eContainer();
				    }
				    break;
				    
				case "model" :
				    if ( selectedObject instanceof IArchimateDiagramModel )
				        selectedObject = ((IArchimateDiagramModel)selectedObject).getArchimateModel();
				    else if ( selectedObject instanceof IArchimateModelObject )
				        selectedObject = ((IArchimateModelObject)selectedObject).getArchimateModel();
				    else
				        selectedObject = ((IDiagramModelArchimateObject)selectedObject).getDiagramModel().getArchimateModel();
				    break;
				    
				default :
				    throw new RuntimeException("Unknown \"refers\" value \""+refers+"\".\n\nMust be \"selected\", \"view\", \"folder\" or \"model\".");
			}
			
            for ( Shell shell: Display.getDefault().getShells() ) {
                shell.setCursor(CURSOR_WAIT);
            }
			
			new FormDialog(form, selectedObject);
			
		} catch (IOException e) {
			FormDialog.popup(Level.ERROR, "I/O Error while reading configuration file:\n"+configFilename,e);
		} catch (ParseException e) {
			if ( e.getMessage() !=null )
				FormDialog.popup(Level.ERROR, "Parsing error while reading configuration file:\n"+configFilename,e);
			else
				FormDialog.popup(Level.ERROR, "Parsing error while reading configuration file:\n"+configFilename+"\n\nUnexpected "+e.getUnexpectedObject().toString()+" at position "+e.getPosition());
		}  catch (ClassCastException e) {
			FormDialog.popup(Level.ERROR, "Wrong key type in the configuration files:\n"+configFilename,e);
		} catch (RuntimeException e) {
			FormDialog.popup(Level.ERROR, "Parsing error while reading configuration file:\n"+configFilename,e);
		}
		
        for ( Shell shell: Display.getDefault().getShells() ) {
            shell.setCursor(CURSOR_ARROW);
        }
		
		return null;
	}
}
