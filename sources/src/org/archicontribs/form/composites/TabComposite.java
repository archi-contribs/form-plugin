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
		this.nameEditor = new StringEditor(this, "name", "Name:");
		this.nameEditor.setPosition(0);
		this.nameEditor.mustSetTreeItemText(true);
		this.nameEditor.mustSetControlText(true);
		this.nameEditor.setTooltipText("Name of the object.\n\nThis can be any arbitrary text.");
		
		// comment
		this.commentEditor = new StringEditor(this, "comment", "Comment:");
		this.commentEditor.setPosition(this.nameEditor.getControl());
		this.commentEditor.setTooltipText("You may enter any comment you wish.\nJust press 'return' to enter several lines of text.");
        
		// Background
		this.colorEditor = new ColorEditor(this, "Color:");
		this.colorEditor.setPosition(this.commentEditor.getControl());
	}
	
    @Override
    public void set(String key, Object value) throws RuntimeException {
    	switch ( key ) {
            case "name":		  this.nameEditor.setText((String)value); break;
    		case "comment":       this.commentEditor.setText((String)value); break;
            case "foreground":	  this.colorEditor.setForeground((String)value); break;
    		case "background":	  this.colorEditor.setBackground((String)value); break;	
    		default:			  throw new RuntimeException("does not know key "+key);
    	}
    }
}
