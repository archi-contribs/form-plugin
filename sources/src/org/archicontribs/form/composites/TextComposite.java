package org.archicontribs.form.composites;

import org.archicontribs.form.editors.ColorEditor;
import org.archicontribs.form.editors.FontEditor;
import org.archicontribs.form.editors.SizeEditor;
import org.archicontribs.form.editors.StringEditor;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

public class TextComposite extends Composite {
	private StringEditor            nameEditor;         // name
    private StringEditor            textEditor;			// text
	private SizeEditor              sizeEditor;         // x, y, width, height
	private ColorEditor             colorEditor;        // foreground, background
	private FontEditor				fontEditor;			// font, fontBold, fontItalic
	private StringEditor		    tooltipEditor;      // tooltip
	
    private CCombo     comboAlignment       = null;         // alignment
    private StyledText txtExcelSheet        = null;         // excelSheet
    private StyledText txtExcelCell         = null;         // excelCell
    private CCombo     txtExcelCellType     = null;         // excelCellType
    private CCombo     txtExcelDefault      = null;         // excelDefault

	public TextComposite(Composite parent, int style) {
		super(parent, style);
        setLayout(new FormLayout());
        createContent();
	}
	
	private void createContent() {
		// name
		nameEditor = new StringEditor(this);
		nameEditor.setPosition(0);
		nameEditor.setProperty("name");
		nameEditor.mustSetTreeItemText(true);
		nameEditor.treeItemTextPrefix("Name: ");
		
		// text
		textEditor = new StringEditor(this);
		textEditor.setPosition(nameEditor.getControl());
		textEditor.setProperty("text");
		textEditor.mustSetTreeItemText(true);
		textEditor.treeItemTextPrefix("Text: ");
						
		// x, y, width, height
		sizeEditor = new SizeEditor(this);
		sizeEditor.setPosition(textEditor.getControl());
		        
		// Background
		colorEditor = new ColorEditor(this);
		colorEditor.setPosition(sizeEditor.getControl());
		
		// font, fontBold, fontItalic
		fontEditor = new FontEditor(this);
		fontEditor.setPosition(colorEditor.getControl());
		
		// tooltip
		tooltipEditor = new StringEditor(this, 5);
		tooltipEditor.setPosition(fontEditor.getControl());
		tooltipEditor.setProperty("tooltip");
		tooltipEditor.mustSetControlTolltip(true);
	}
	
    public void set(String key, int value) throws RuntimeException {
    	switch ( key ) {
    		case "x":
    			sizeEditor.setX(value);
    			
    		case "y":
    			sizeEditor.setY(value);
    			return;
    			
    		case "width":
    			sizeEditor.setWidth(value);
    			return;
    			
    		case "height":
    			sizeEditor.setHeight(value);
    			return;
    	}
    	throw new RuntimeException("does not know key "+key);
    }
	
    public void set(String key, String value) throws RuntimeException {
    	switch ( key ) {
    		case "name":
    			nameEditor.setString(value);
    			
    		case "foreground":
    			colorEditor.setForeground(value);
    			return;
    			
    		case "background":
    			colorEditor.setBackround(value);
    			return;
    	}
    	throw new RuntimeException("does not know key "+key);
    }
    
    public String getString(String key) throws RuntimeException {
    	switch ( key ) {
    		case "name":
    			return nameEditor.getString();
    			
    		case "foreground":
    			return colorEditor.getForeground();
    			
    		case "background":
    			return colorEditor.getBackground();
    	}
    	throw new RuntimeException("does not know key "+key);
    }
    
    public int getInt(String key) throws RuntimeException {
    	switch ( key ) {
    		case "x":		return sizeEditor.getX();
    		case "y":		return sizeEditor.getY();
    		case "width": 	return sizeEditor.getWidth();
    		case "height": 	return sizeEditor.getHeight();
    	}
    	throw new RuntimeException("does not know key "+key);
    }
}
