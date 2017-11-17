package org.archicontribs.form.composites;

import org.archicontribs.form.editors.ColorEditor;
import org.archicontribs.form.editors.StringEditor;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

public class TabComposite extends Composite implements CompositeInterface {
	private StringEditor            nameEditor;          // name
	private StringEditor            commentEditor;       // comment
	private ColorEditor             colorEditor;         // foreground, background

	public TabComposite(Composite parent, int style) {
		super(parent, style);
        setLayout(new FormLayout());
        createContent();
	}
	
	private void createContent() {
		// name
		nameEditor = new StringEditor(this, "name", "Name:");
		nameEditor.setPosition(0);
		nameEditor.mustSetTreeItemText(true);
		nameEditor.mustSetControlText(true);
		nameEditor.setTooltipText("Name of the object.\n\nThis can be any arbitrary text.");
		
		// comment
		commentEditor = new StringEditor(this, "comment", "Comment:");
		commentEditor.setPosition(nameEditor.getControl());
		commentEditor.setTooltipText("You may enter any comment you wish.\nJust press 'return' to enter several lines of text.");
        
		// Background
		colorEditor = new ColorEditor(this, "Color:");
		colorEditor.setPosition(commentEditor.getControl());
	}
	
    public void set(String key, Object value) throws RuntimeException {
    	switch ( key ) {
            case "name":		  nameEditor.setText((String)value); break;
    		case "comment":       commentEditor.setText((String)value); break;
            case "foreground":	  colorEditor.setForeground((String)value); break;
    		case "background":	  colorEditor.setBackground((String)value); break;	
    		default:			  throw new RuntimeException("does not know key "+key);
    	}
    }
}
