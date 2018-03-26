package org.archicontribs.form.composites;

import java.util.List;
import java.util.Map;

import org.archicontribs.form.FormDialog;
import org.archicontribs.form.editors.ColorEditor;
import org.archicontribs.form.editors.ComboEditor;
import org.archicontribs.form.editors.FilterEditor;
import org.archicontribs.form.editors.FormSizeEditor;
import org.archicontribs.form.editors.StringEditor;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

public class FormComposite extends Composite implements CompositeInterface {
	private StringEditor            nameEditor;         		// name
	private StringEditor            commentEditor;         		// comment
	private FormSizeEditor          formSizeEditor;    			// width, height, spacing, buttonWidth, buttonHeight						//TODO : rename spacing to margin
	private ColorEditor             colorEditor;        		// foreground, background
    private ComboEditor             refersEditor; 				// refers
    private FilterEditor			filterEditor;               // filter
    private StringEditor            variableSeparatorEditor;	// variableSeparator
    private StringEditor            buttonOkEditor; 			// buttonOk
    private StringEditor            buttonCancelEditor; 		// buttonCancel
    private StringEditor            buttonExportEditor; 		// buttonExport
    private ComboEditor             whenEmptyEditor;			// whenEmpty

	public FormComposite(Composite parent, int style) {
		super(parent, style);
        setLayout(new FormLayout());
        createContent();
	}
	
	private void createContent() {
		// name
		this.nameEditor = new StringEditor(this, "name", "Name:");
		this.nameEditor.setPosition(0);
		this.nameEditor.mustSetControlText(true);
		this.nameEditor.setTooltipText("Name of the object.\n\nThis can be any arbitrary text.");
		
		// comment
		this.commentEditor = new StringEditor(this, "comment", "Comment:");
		this.commentEditor.setPosition(this.nameEditor.getControl());
		this.commentEditor.setTooltipText("You may enter any comment you wish.\nJust press 'return' to enter several lines of text.");
				
        // width, height, spacing, buttonWidth, buttonHeight
		this.formSizeEditor = new FormSizeEditor(this);
		this.formSizeEditor.setPosition(this.commentEditor.getControl());
        
        // Background
		this.colorEditor = new ColorEditor(this, "Color:");
		this.colorEditor.setPosition(this.formSizeEditor.getControl());
		
		// Refers
		this.refersEditor = new ComboEditor(this, "refers", "Refers to:");
		this.refersEditor.setPosition(this.colorEditor.getControl());
		this.refersEditor.setItems(FormDialog.validRefers);
		this.refersEditor.setTooltipText("Choose which component the form will apply to:\n"+
                "   - Selected: the component(s) that will be selected when the form will be ran,\n"+
                "   - Folder: the folder in which the selected components are,\n"+
                "   - View: the view in which the selected components are,\n"+
                "   - Model: the whole model.\n"+
                "\n"+
                "Default: "+FormDialog.validRefers[0]+"."
                );
		
	    // filter
		this.filterEditor = new FilterEditor(this, false);
		this.filterEditor.setPosition(this.refersEditor.getControl());
		
	    // variableSeparator
		this.variableSeparatorEditor = new StringEditor(this, "variableSeparator", "Variable separator:");
		this.variableSeparatorEditor.setPosition(this.filterEditor.getControl());
		this.variableSeparatorEditor.setTextLimit(1);
		this.variableSeparatorEditor.setWidth(25);
		this.variableSeparatorEditor.setTooltipText("Character used to separate the different fields of a variable\n\nDefault: "+FormDialog.defaultVariableSeparator+".");
		
	    // buttonOk
		this.buttonOkEditor = new StringEditor(this, "buttonOk", "OK button text:");
		this.buttonOkEditor.setPosition(this.variableSeparatorEditor.getControl());
		this.buttonOkEditor.setWidget((Widget)getShell().getData("ok button"));
		this.buttonOkEditor.mustSetControlText(true);
		this.buttonOkEditor.setTooltipText("Text of the OK button\n\nDefault: "+FormDialog.defaultButtonOkText+".");
        
        // buttonCancel
		this.buttonCancelEditor = new StringEditor(this, "buttonCancel", "Cancel button text:");
		this.buttonCancelEditor.setPosition(this.buttonOkEditor.getControl());
		this.buttonCancelEditor.setWidget((Widget)getShell().getData("cancel button"));
		this.buttonCancelEditor.mustSetControlText(true);
		this.buttonCancelEditor.setTooltipText("Text of the Cancel button\n\nDefault: "+FormDialog.defaultButtonCancelText+".");
        
        // buttonExport
		this.buttonExportEditor = new StringEditor(this, "buttonExport", "Export button text:");
		this.buttonExportEditor.setPosition(this.buttonCancelEditor.getControl());
		this.buttonExportEditor.setWidget((Widget)getShell().getData("export button"));
		this.buttonExportEditor.mustSetControlText(true);
		this.buttonExportEditor.setTooltipText("Text of the Export to Excel button\n\nDefault: "+FormDialog.defaultButtonExportText+".");
		
        // whenEmpty
        this.whenEmptyEditor = new ComboEditor(this, "whenEmpty", "When empty :");
        this.whenEmptyEditor.setPosition(this.buttonExportEditor.getControl());
        this.whenEmptyEditor.setItems(FormDialog.validWhenEmpty);
        this.whenEmptyEditor.setTooltipText("Choose the plugin behaviour when a variable is left empty in the form:\n"+
                "   - ignore: do not change the property value:\n"+
                "                 - if the property does not already exist, it will not be created,\n"+
                "                 - if the propety does already exist, its value is left unmodified.\n"+
                "   - create: empty the property's value if it does already exist, or create a new one with an empty value,\n"+
                "   - delete: delete the property if it does already exist.\n"+
                "\n"+
                "Default: "+FormDialog.validWhenEmpty[0]+"."
                );
	}
    
    @Override
    @SuppressWarnings("unchecked")
	public void set(String key, Object value) throws RuntimeException {
    	switch ( key.toLowerCase() ) {
    		case "name":		      this.nameEditor.setText((String)value); break;
    		case "comment":		      this.commentEditor.setText((String)value); break;
    		case "foreground":	      this.colorEditor.setForeground((String)value); break;
    		case "background":	      this.colorEditor.setBackground((String)value); break;
    		case "refers":		      this.refersEditor.setText((String)value); break;
    		case "variableseparator": this.variableSeparatorEditor.setText((String)value); break;
    		case "buttonok":	      this.buttonOkEditor.setText((String)value); break;
    		case "buttoncancel":      this.buttonCancelEditor.setText((String)value); break;
    		case "buttonexport":      this.buttonExportEditor.setText((String)value); break;
    		case "whenempty":         this.whenEmptyEditor.setText((String)value); break;
    		case "width":		      this.formSizeEditor.setWidth((Integer)value); break;
    		case "height":		      this.formSizeEditor.setHeight((Integer)value); break;
    		case "spacing":		      this.formSizeEditor.setSpacing((Integer)value); break;
    		case "buttonwidth":	      this.formSizeEditor.setButtonWidth((Integer)value); break;
    		case "buttonheight":      this.formSizeEditor.setButtonHeight((Integer)value); break;
            case "tests":             this.filterEditor.setTests((List<Map<String, String>>)value); break;
            case "genre":             this.filterEditor.setGenre((String)value); break;
    		default:
    			throw new RuntimeException("does not know key "+key);
    	}
    }
}
