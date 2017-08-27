package org.archicontribs.form.composites;

import org.archicontribs.form.editors.ColorEditor;
import org.archicontribs.form.editors.IntegerEditor;
import org.archicontribs.form.editors.SizeEditor;
import org.archicontribs.form.editors.StringEditor;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

public class TableComposite extends Composite implements CompositeInterface {
	private StringEditor            nameEditor;             // name
	private SizeEditor              sizeEditor;             // x, y, width, height
	private ColorEditor             colorEditor;            // foreground, background
	private StringEditor            tooltipEditor;          // tooltip
    private StringEditor            excelSheetEditor;       // excelSheet
    private IntegerEditor           excelFirstLineEditor;   // excelFirstLine
    private IntegerEditor           excelLastLineEditor;    // excelLastLine

	public TableComposite(Composite parent, int style) {
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
		
		// x, y, width, height
		sizeEditor = new SizeEditor(this);
		sizeEditor.setPosition(nameEditor.getControl());
		
		// foreground, background
		colorEditor = new ColorEditor(this, "Color:");
		colorEditor.setPosition(sizeEditor.getControl());
		
		// tooltip
		tooltipEditor = new StringEditor(this, "Tooltip:", 5);
		tooltipEditor.setPosition(colorEditor.getControl());
		tooltipEditor.setProperty("tooltip");
		tooltipEditor.mustSetControlTolltip(true);
		
        // excelSheet
        excelSheetEditor = new StringEditor(this, "Excel sheet:");
        excelSheetEditor.setPosition(tooltipEditor.getControl());
        excelSheetEditor.setProperty("excelSheet");
        excelSheetEditor.setTooltipText("Name of the Excel sheet where the text should be exported to.\n\nIf this field is left blank, then the variable will not be exported to Excel, even if the others Excel related field are set.");
        
		// name
        excelFirstLineEditor = new IntegerEditor(this, "Excel last line:");
        excelLastLineEditor.setPosition(excelSheetEditor.getControl());
        excelFirstLineEditor.setProperty("excelFirstLine");
		
		// name
        excelLastLineEditor = new IntegerEditor(this, "Name:");
        excelLastLineEditor.setPosition(excelFirstLineEditor.getControl());
        excelLastLineEditor.setProperty("excelLastLine");
        
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
