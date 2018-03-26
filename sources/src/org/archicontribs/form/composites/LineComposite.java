package org.archicontribs.form.composites;

import java.util.List;
import java.util.Map;

import org.archicontribs.form.editors.FilterEditor;
import org.archicontribs.form.editors.StringEditor;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

public class LineComposite extends Composite implements CompositeInterface {
	private StringEditor            nameEditor;          // name
	private StringEditor            commentEditor;       // comment
    private StringEditor            cellsEditor;         // cells
    private FilterEditor            filterEditor;        // filter
    

	public LineComposite(Composite parent, int style) {
		super(parent, style);
        setLayout(new FormLayout());
        createContent();
	}
	
	private void createContent() {
		// name
		this.nameEditor = new StringEditor(this, "name", "Name:");
		this.nameEditor.setPosition(0);
		this.nameEditor.mustSetTreeItemText(true);
		this.nameEditor.setTooltipText("Name of the object.\n\nThis can be any arbitrary text.");
		
		// comment
		this.commentEditor = new StringEditor(this, "comment", "Comment:");
		this.commentEditor.setPosition(this.nameEditor.getControl());
		this.commentEditor.setTooltipText("You may enter any comment you wish.\nJust press 'return' to enter several lines of text.");
		
		// cells
		this.cellsEditor = new StringEditor(this, "cells", "Cells:");
		this.cellsEditor.setPosition(this.commentEditor.getControl());
		this.cellsEditor.mustSetControlText(true);
		this.cellsEditor.setTooltipText("Please enter the variables corresponding to the table columns, one line per variable.\n"+
			"\n"+
			"You must have as many lines than the columns.");
		
	    // filter
		this.filterEditor = new FilterEditor(this, true);
		this.filterEditor.setPosition(this.cellsEditor.getControl());
	}
	
    @Override
    @SuppressWarnings("unchecked")
	public void set(String key, Object value) throws RuntimeException {
    	switch ( key.toLowerCase() ) {
            case "name":          this.nameEditor.setText((String)value); break;
    		case "comment":       this.commentEditor.setText((String)value); break;
            case "cells":         this.cellsEditor.setText((String[])value);break;
            case "generate":      this.filterEditor.setGenerate((Boolean)value); break;
            case "tests":         this.filterEditor.setTests((List<Map<String, String>>)value); break;
            case "genre":         this.filterEditor.setGenre((String)value); break;
    		default:			  throw new RuntimeException("does not know key "+key);
    	}
    }
}
