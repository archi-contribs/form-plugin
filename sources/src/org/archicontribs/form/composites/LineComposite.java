package org.archicontribs.form.composites;

import java.util.List;
import java.util.Map;

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
		nameEditor = new StringEditor(this, "name", "Name:");
		nameEditor.setPosition(0);
		nameEditor.mustSetTreeItemText(true);
		nameEditor.setTooltipText("Name of the object.\n\nThis can be any arbitrary text.");
		
		// cells
		cellsEditor = new StringEditor(this, "cells", "Cells:");
		cellsEditor.setPosition(nameEditor.getControl());
		cellsEditor.mustSetControlText(true);
		cellsEditor.setTooltipText("Please enter the variables corresponding to the table columns, one line per variable.\n"+
			"\n"+
			"You must have as many lines than the columns.");
		
	    // filter
		filterEditor = new FilterEditor(this, true);
		filterEditor.setPosition(cellsEditor.getControl());
	}
	
    @SuppressWarnings("unchecked")
	public void set(String key, Object value) throws RuntimeException {
    	switch ( key.toLowerCase() ) {
            case "name":          nameEditor.setText((String)value); break;
            case "cells":         cellsEditor.setText((String[])value);break;
            case "generate":      filterEditor.setGenerate((Boolean)value); break;
            case "tests":         filterEditor.setTests((List<Map<String, String>>)value); break;
            case "genre":         filterEditor.setGenre((String)value); break;
    		default:			  throw new RuntimeException("does not know key "+key);
    	}
    }
}
