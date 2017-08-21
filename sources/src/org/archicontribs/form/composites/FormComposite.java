package org.archicontribs.form.composites;

import org.archicontribs.form.FormDialog;
import org.archicontribs.form.editors.ColorEditor;
import org.archicontribs.form.editors.ComboEditor;
import org.archicontribs.form.editors.FormSizeEditor;
import org.archicontribs.form.editors.StringEditor;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

public class FormComposite extends Composite {
	private StringEditor            nameEditor;         // name
	private FormSizeEditor          formSizeEditor;     // width, height, spacing						//TODO : rename spacing to margin
	private ColorEditor             colorEditor;        // foreground, background
    private ComboEditor             refersEditor; 		// refers
    private StringEditor            buttonOkEditor; 	// buttonOk
    private StringEditor            buttonCancelEditor; // buttonCancel
    private StringEditor            buttonExportEditor; // buttonExport
    private ComboEditor             whenEmptyEditor;	// whenEmpty

	public FormComposite(Composite parent, int style) {
		super(parent, style);
        setLayout(new FormLayout());
        createContent();
	}
	
	private void createContent() {
		// name
		nameEditor = new StringEditor(this, "Name:");
		nameEditor.setPosition(0);
		nameEditor.setProperty("name");
		nameEditor.mustSetControlText(true);
				
        // width, height, spacing
		formSizeEditor = new FormSizeEditor(this);
		formSizeEditor.setPosition(nameEditor.getControl());
        
        // Background
		colorEditor = new ColorEditor(this, "Color:");
		colorEditor.setPosition(formSizeEditor.getControl());
		
		// Refers
		refersEditor = new ComboEditor(this, "Refers to:");
		refersEditor.setPosition(colorEditor.getControl());
		refersEditor.setItems(FormDialog.validRefers);
		refersEditor.setTooltipText("Choose which component the form will apply to:\n"+
                "   - Selected: the component(s) that will be selected when the form will be ran,\n"+
                "   - Folder: the folder in which the selected components are,\n"+
                "   - View: the view in which the selected components are,\n"+
                "   - Model: the whole model.\n"+
                "\n"+
                "Default: "+FormDialog.defaultRefers+"."
                );
		
	    // buttonOk
		buttonOkEditor = new StringEditor(this, "OK button text:");
		buttonOkEditor.setPosition(refersEditor.getControl());
		buttonOkEditor.setProperty("buttonOk");
		buttonOkEditor.setControlKey("buttonOk");
		buttonOkEditor.mustSetControlText(true);
		buttonOkEditor.setTooltipText("Text of the OK button\n\nDefault: OK.");
        
        // buttonCancel
		buttonCancelEditor = new StringEditor(this, "Cancel button text:");
		buttonCancelEditor.setPosition(buttonOkEditor.getControl());
		buttonCancelEditor.setProperty("buttonCancel");
		buttonCancelEditor.setControlKey("buttonCancel");
		buttonCancelEditor.mustSetControlText(true);
		buttonCancelEditor.setTooltipText("Text of the Cancel button\n\nDefault: Cancel.");
        
        // buttonExport
		buttonExportEditor = new StringEditor(this, "Export button text:");
		buttonExportEditor.setPosition(buttonExportEditor.getControl());
		buttonExportEditor.setProperty("buttonExport");
		buttonExportEditor.setControlKey("buttonExport");
		buttonExportEditor.mustSetControlText(true);
		buttonExportEditor.setTooltipText("Text of the Export to Excel button\n\nDefault: Export to Excel.");
		
        // whenEmpty
        whenEmptyEditor = new ComboEditor(this, "When empty :");
        whenEmptyEditor.setPosition(buttonExportEditor.getControl());
        whenEmptyEditor.setItems(FormDialog.validWhenEmpty);
        whenEmptyEditor.setTooltipText("Choose the plugin behaviour when a variable is left empty in the form:\n"+
                "   - ignore: do not change the property value:\n"+
                "                 - if the property does not already exist, it will not be created,\n"+
                "                 - if the propety does already exist, its value is left unmodified.\n"+
                "   - create: empty the property's value if it does already exist, or create a new one with an empty value,\n"+
                "   - delete: delete the property if it does already exist.\n"+
                "\n"+
                "Default: "+FormDialog.defaultWhenEmpty+"."
                );
	}
    
    public void set(String key, String value) throws RuntimeException {
    	switch ( key.toLowerCase() ) {
    		case "name":
    			nameEditor.setText(value);
    			return;
    			
    		case "foreground":
    			colorEditor.setBackround(value);
    			
    		case "background":
    			colorEditor.setBackround(value);
				
    		case "refers":
    			refersEditor.setText(value);
    			return;
    			
    		case "buttonok":
    			buttonOkEditor.setText(value);
    			return;
    			
    		case "buttoncancel":
    		    buttonCancelEditor.setText(value);
    			return;
    			
    		case "buttonexport":
    		    buttonExportEditor.setText(value);
    			return;
    			
    		case "whenempty":
    		    whenEmptyEditor.setText(value);
    			return;
    	}
    	throw new RuntimeException("does not know key "+key);
    }
    
    public void set(String key, int value) throws RuntimeException {
    	switch ( key.toLowerCase() ) {
    		case "width":
    			formSizeEditor.setWidth(value);
    			return;
    			
    		case "height":
    			formSizeEditor.setHeight(value);
    			return;
    			
    		case "spacing":
    			formSizeEditor.setSpacing(value);
    			return;
    	}
    	throw new RuntimeException("does not know key "+key);
    }
    
    public String getString(String key) throws RuntimeException {
    	switch ( key.toLowerCase() ) {
    		case "name":              return nameEditor.getText();
    		case "background":        return colorEditor.getBackground();
    		case "foreground":        return colorEditor.getForeground();
    		case "refers":            return refersEditor.getText();
    		case "buttonok":          return buttonOkEditor.getText();
    		case "buttoncancel":      return buttonCancelEditor.getText();
    		case "buttonexport":      return buttonExportEditor.getText();
    		case "whenempty":         return whenEmptyEditor.getText();
    	}
    	throw new RuntimeException("does not know key "+key);
    }
    
    public int getInt(String key) throws RuntimeException {
    	switch ( key.toLowerCase() ) {
    		case "width":
    			return formSizeEditor.getWidth();

    		case "height":
    			return formSizeEditor.getHeight();
    			
    		case "spacing":
    			return formSizeEditor.getSpacing();
    	}
    	throw new RuntimeException("does not know key "+key);
    }
}
