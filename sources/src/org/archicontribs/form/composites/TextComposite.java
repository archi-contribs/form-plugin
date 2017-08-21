package org.archicontribs.form.composites;

import org.archicontribs.form.editors.AlignmentEditor;
import org.archicontribs.form.editors.ColorEditor;
import org.archicontribs.form.editors.ComboEditor;
import org.archicontribs.form.editors.FontEditor;
import org.archicontribs.form.editors.SizeEditor;
import org.archicontribs.form.editors.StringEditor;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

public class TextComposite extends Composite {
	private StringEditor            nameEditor;          // name
    private StringEditor            textEditor;			 // text
	private SizeEditor              sizeEditor;          // x, y, width, height
	private ColorEditor             colorEditor;         // foreground, background
	private FontEditor				fontEditor;			 // font, fontBold, fontItalic
	private StringEditor		    tooltipEditor;       // tooltip
	private AlignmentEditor         alignmentEditor;     // alignment
	
	private StringEditor            excelSheetEditor;    // excelSheet
	private StringEditor            excelCellEditor;     // excelCell
	private ComboEditor             excelCellTypeEditor; // excelCellType
	private ComboEditor             excelDefaultEditor;  //excelDefault


	public TextComposite(Composite parent, int style) {
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
		nameEditor.treeItemTextPrefix("Name: ");
		
		// text
		textEditor = new StringEditor(this, "Text:");
		textEditor.setPosition(nameEditor.getControl());
		textEditor.setProperty("text");
		textEditor.mustSetTreeItemText(true);
		textEditor.treeItemTextPrefix("Text: ");
						
		// x, y, width, height
		sizeEditor = new SizeEditor(this);
		sizeEditor.setPosition(textEditor.getControl());
		        
		// Background
		colorEditor = new ColorEditor(this, "Color:");
		colorEditor.setPosition(sizeEditor.getControl());
		
		// font, fontBold, fontItalic
		fontEditor = new FontEditor(this, "Font");
		fontEditor.setPosition(colorEditor.getControl());
		
		// tooltip
		tooltipEditor = new StringEditor(this, "Tooltip:", 5);
		tooltipEditor.setPosition(fontEditor.getControl());
		tooltipEditor.setProperty("tooltip");
		tooltipEditor.mustSetControlTolltip(true);
		
        // alignment
        alignmentEditor = new AlignmentEditor(this, "Alignment:");
        alignmentEditor.setPosition(tooltipEditor.getControl());
        alignmentEditor.setItems(new String[] {"", "left", "center", "right"});
        alignmentEditor.setTooltipText("Choose the alignment.\n\nDefault: left.");
        alignmentEditor.setProperty("alignment");
        
        // excelSheet
        excelSheetEditor = new StringEditor(this, "Excel sheet:");
        excelSheetEditor.setPosition(alignmentEditor.getControl());
        excelSheetEditor.setProperty("excelsheet");
        excelSheetEditor.setTooltipText("Name of the Excel sheet where the variable should be exported to.\n\nIf this field is left blank, then the variable will not be exported to Excel, even if the others Excel related field are set.");
        
        // excelCell
        excelCellEditor = new StringEditor(this, "Excel cell:");
        excelCellEditor.setPosition(excelSheetEditor.getControl());
        excelCellEditor.setProperty("excelcell");
        excelCellEditor.setTooltipText("Adress of the Excel cell where the variable should be exported to (like A3 or D14).\n\nIf the \"Excel sheet\" field is not set, then the variable will not be exported to Excel even if this field is set.");
        
        // excelCellType
        excelCellTypeEditor = new ComboEditor(this, "Excel type:");
        excelCellTypeEditor.setPosition(excelCellEditor.getControl());
        excelCellTypeEditor.setProperty("exceltype");
        excelCellTypeEditor.setItems(new String[] {"", "string", "boolean", "numeric", "formula"});
        excelCellTypeEditor.setTooltipText("Type of the Excel cell.\n\nDefault: string");
        
        // excelDefault
        excelDefaultEditor = new ComboEditor(this, "Excel default:");
        excelDefaultEditor.setPosition(excelCellTypeEditor.getControl());
        excelDefaultEditor.setProperty("exceldefault");
        excelDefaultEditor.setItems(new String[] {"", "blank", "zero", "delete"});
        excelDefaultEditor.setTooltipText("Behaviour of the plugin when exporting an empty value:\n"+
                "   - blank : a blank cell will be created (ie a cell with no content)\n"+
                "   - zero : a cell with a zero value in it:\n"+
                "                - 0 for numeric cells\n"+
                "                - empty string for string and formula cells\n"+
                "                - false for boolean cells\n"+
                "   - delete : the cell will be deleted.\n"+
                "\n"+
                "Default: blank");
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
            case "alignment":
                return alignmentEditor.getText();
                                
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
