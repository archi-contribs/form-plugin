package org.archicontribs.form.Composites;

import org.archicontribs.form.Editors.ColorEditor;
import org.archicontribs.form.Editors.NameEditor;
import org.archicontribs.form.Editors.SizeEditor;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class TextComposite extends Composite {
	private NameEditor              nameEditor;         // name
    private Label                   txtText;			// text
	private SizeEditor              sizeEditor;         // x, y, width, height
	private ColorEditor             colorEditor;        // foreground, background


    private Label      txtTooltip           = null;			// tooltip
    private Label      lblFont              = null;         // font
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
		nameEditor = new NameEditor(this);
		nameEditor.setPosition(0);
						
		// x, y, width, height
		sizeEditor = new SizeEditor(this);
		sizeEditor.setPosition(nameEditor.getControl());
		        
		// Background
		colorEditor = new ColorEditor(this);
		colorEditor.setPosition(sizeEditor.getControl());
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
    			nameEditor.setName(value);
    			
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
    			return nameEditor.getName();
    			
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
