package org.archicontribs.form;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

import com.archimatetool.editor.diagram.ArchimateDiagramEditor;
import com.archimatetool.model.IArchimateDiagramModel;

public class FormEditorHandler extends AbstractHandler {
	public String menuLabel = "Fiche navette";
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		//IArchimateDiagramModel diagramModel = ((ArchimateDiagramPart)((IStructuredSelection)HandlerUtil.getCurrentSelection(event)).getFirstElement()).getModel();
		IArchimateDiagramModel diagramModel = ((ArchimateDiagramEditor)(HandlerUtil.getActiveEditor(event))).getModel();
		
		new FormDialog(event.getParameter("org.archicontribs.form.showFormName"), diagramModel);

		return null;
	}
}
