package org.archicontribs.form;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

import com.archimatetool.editor.diagram.ArchimateDiagramEditor;

/**
 * This classes is instantiated when a form is selected on the right-click menu
 * @author Herve Jouin
 *
 */
public class FormEditorHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		new FormDialog(event.getParameter("org.archicontribs.form.showFormName"), ((ArchimateDiagramEditor)(HandlerUtil.getActiveEditor(event))).getModel());
		return null;
	}
}
