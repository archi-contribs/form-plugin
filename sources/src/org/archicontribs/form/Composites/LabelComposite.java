package org.archicontribs.form.Composites;

import org.archicontribs.form.Editors.ColorEditor;
import org.archicontribs.form.Editors.FontEditor;
import org.archicontribs.form.Editors.SizeEditor;
import org.archicontribs.form.Editors.StringEditor;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class LabelComposite extends Composite {
	private StringEditor            nameEditor;         // name
    private StringEditor            textEditor;			// text
	private SizeEditor              sizeEditor;         // x, y, width, height
	private ColorEditor             colorEditor;        // foreground, background
	private FontEditor				fontEditor;			// font, fontBold, fontItalic
	private StringEditor            tooltipEditor;      // tooltip

    private Label      lblFont              = null;         // font
    private CCombo     comboAlignment       = null;         // alignment
    private StyledText txtExcelSheet        = null;         // excelSheet
    private StyledText txtExcelCell         = null;         // excelCell
    private CCombo     txtExcelCellType     = null;         // excelCellType
    private CCombo     txtExcelDefault      = null;         // excelDefault

	public LabelComposite(Composite parent, int style) {
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
		nameEditor.treeItemTextPrefix("Label: ");
		
		// name
		textEditor = new StringEditor(this, 5);
		textEditor.setPosition(nameEditor.getControl());
		textEditor.setProperty("text");
		textEditor.mustSetControlText(true);
						
		// x, y, width, height
		sizeEditor = new SizeEditor(this);
		sizeEditor.setPosition(textEditor.getControl());
		        
		// foreground, background
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
    			return;
    			
    		case "foreground":
    			colorEditor.setForeground(value);
    			return;
    			
    		case "background":
    			colorEditor.setBackround(value);
    			return;
    			
    		case "text":
    			textEditor.setString(value);
    			return;
    			
    		case "tooltip":
    			tooltipEditor.setString(value);
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
    			
    		case "text":
    			return textEditor.getString();
    			
    		case "tooltip":
    			return tooltipEditor.getString();
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
