package org.archicontribs.form.composites;

import org.archicontribs.form.editors.AlignmentEditor;
import org.archicontribs.form.editors.ColorEditor;
import org.archicontribs.form.editors.FontEditor;
import org.archicontribs.form.editors.SizeEditor;
import org.archicontribs.form.editors.StringEditor;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

public class LabelComposite extends Composite {
	private StringEditor            nameEditor;         // name
    private StringEditor            textEditor;			// text
	private SizeEditor              sizeEditor;         // x, y, width, height
	private ColorEditor             colorEditor;        // foreground, background
	private FontEditor				fontEditor;			// font, fontBold, fontItalic
	private StringEditor            tooltipEditor;      // tooltip
	private AlignmentEditor         alignmentEditor;    // alignment
	    
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
		nameEditor = new StringEditor(this, "Name:");
		nameEditor.setPosition(0);
		nameEditor.setProperty("name");
		nameEditor.mustSetTreeItemText(true);
		nameEditor.treeItemTextPrefix("Label: ");
		
		// text
		textEditor = new StringEditor(this, "Text:", 5);
		textEditor.setPosition(nameEditor.getControl());
		textEditor.setProperty("text");
		textEditor.mustSetControlText(true);
						
		// x, y, width, height
		sizeEditor = new SizeEditor(this);
		sizeEditor.setPosition(textEditor.getControl());
		        
		// foreground, background
		colorEditor = new ColorEditor(this, "Color:");
		colorEditor.setPosition(sizeEditor.getControl());
		
		// font, fontBold, fontItalic
		fontEditor = new FontEditor(this, "Font:");
		fontEditor.setPosition(colorEditor.getControl());
		
		// tooltip
		tooltipEditor = new StringEditor(this, "Tooltip:", 5);
		tooltipEditor.setPosition(fontEditor.getControl());
		tooltipEditor.setProperty("tooltip");
		tooltipEditor.mustSetControlTolltip(true);
		
	      // tooltip
        alignmentEditor = new AlignmentEditor(this, "Alignment:");
        alignmentEditor.setPosition(tooltipEditor.getControl());
        alignmentEditor.setProperty("alignment");
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
    	    case "alignment":
    	        alignmentEditor.setText(value);
    	        return;
    	        
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
    	    case "alignment":
    	        return alignmentEditor.getText();
    	        
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
