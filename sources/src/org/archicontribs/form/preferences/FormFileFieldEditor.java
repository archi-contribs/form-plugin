package org.archicontribs.form.preferences;

import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * This class extends the FileFieldEditor. It allows invalid filenames.
 * 
 * @author Herve Jouin
 *
 */
public class FormFileFieldEditor extends FileFieldEditor {
    public FormFileFieldEditor() {
    	super();
    }
    
    public FormFileFieldEditor(String name, String labelText, Composite parent) {
        super(name, labelText, false, parent);
    }
    
    public FormFileFieldEditor(String name, String labelText, boolean enforceAbsolute, Composite parent) {
        super(name, labelText, enforceAbsolute, VALIDATE_ON_FOCUS_LOST, parent);
    }
    
    public FormFileFieldEditor(String name, String labelText, boolean enforceAbsolute, int validationStrategy, Composite parent) {
    	super(name, labelText, enforceAbsolute, validationStrategy, parent);
    }
    
    @Override
	protected String changePressed() {
        return getTextControl().getText();
    }
    
    @Override
	protected boolean checkState() {
    	return true;
    }
}
