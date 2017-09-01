package org.archicontribs.form.composites;

import org.archicontribs.form.editors.ColorEditor;
import org.archicontribs.form.editors.StringEditor;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

public class TabComposite extends Composite implements CompositeInterface {
	private StringEditor            nameEditor;         // name
	private ColorEditor             colorEditor;         // foreground, background

	public TabComposite(Composite parent, int style) {
		super(parent, style);
        setLayout(new FormLayout());
        createContent();
	}
	
	private void createContent() {
		// name
		nameEditor = new StringEditor(this, "Name:");
		nameEditor.setPosition(0);
		nameEditor.setProperty("name");
		nameEditor.mustSetTreeItemText(true);
        
		// Background
		colorEditor = new ColorEditor(this, "Color:");
		colorEditor.setPosition(nameEditor.getControl());
	}
	
    public void set(String key, Object value) throws RuntimeException {
    	switch ( key ) {
            case "name":		  nameEditor.setText((String)value); break;
            case "foreground":	  colorEditor.setForeground((String)value); break;
    		case "background":	  colorEditor.setBackround((String)value); break;	
    		default:			  throw new RuntimeException("does not know key "+key);
    	}
    }
}
