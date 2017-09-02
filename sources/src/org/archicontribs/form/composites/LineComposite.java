package org.archicontribs.form.composites;

import org.archicontribs.form.editors.CheckEditor;
import org.archicontribs.form.editors.FilterEditor;
import org.archicontribs.form.editors.StringEditor;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

public class LineComposite extends Composite implements CompositeInterface {
	private StringEditor            nameEditor;          // name
    private StringEditor            cellsEditor;         // cells
    private FilterEditor            filterEditor;        // filter
    

	public LineComposite(Composite parent, int style) {
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
		
		// variable
		cellsEditor = new StringEditor(this, "Cells:", 5);
		cellsEditor.setPosition(nameEditor.getControl());
		cellsEditor.setProperty("cells");
		cellsEditor.setTooltipText("Please enter the variables corresponding to the table columns, one line per variable.\n"+
			"\n"+
			"You must have as many lines than the columns.");
		
	    // defaultText
		filterEditor = new FilterEditor(this);
		filterEditor.setPosition(cellsEditor.getControl());
	}
	
    public void set(String key, Object value) throws RuntimeException {
    	switch ( key.toLowerCase() ) {
            case "name":          nameEditor.setText((String)value); break;
            case "cells":         if ( value instanceof String[] ) {
            						 StringBuilder str = new StringBuilder();
            						 for ( String v: (String[])value ) {
            							 if ( str.length() != 0 ) str.append("\n");
            							 str.append(v);
            						 }
            						 cellsEditor.setText(str.toString());
            					  } else
            						  cellsEditor.setText((String)value); break;
            case "generate":      filterEditor.setGenerate((Boolean)value); break;
    		default:			  throw new RuntimeException("does not know key "+key);
    	}
    }
}
